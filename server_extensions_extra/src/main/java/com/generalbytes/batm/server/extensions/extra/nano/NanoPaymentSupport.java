package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.common.PollingPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoWsClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.node.INanoRpcWallet;
import com.generalbytes.batm.server.extensions.payment.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * This payment support will utilise the websocket value if configured to reduce load on the RPC endpoint, and minimize
 * delays when processing and verifying deposits. If not provided by the wallet (either websocket is null, or wallet
 * doesn't implement INanoRpcWallet), then it will simply resort to standard RPC polling.
 */
public class NanoPaymentSupport extends PollingPaymentSupport {

    private static final Logger log = LoggerFactory.getLogger(NanoPaymentSupport.class);

    private static final long POLL_PERIOD = 750; // 750 ms
    private static final int POLL_SKIP_CYCLES = 20; // 15 sec (multiplier of POLL_PERIOD when using websockets)

    private final NanoExtensionContext context;
    private final Map<PaymentRequest, PaymentRequestContext> requests = new ConcurrentHashMap<>();
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public NanoPaymentSupport(NanoExtensionContext context) {
        this.context = context;
    }


    @Override
    protected long getPollingPeriodMillis() {
        return POLL_PERIOD;
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return 1500;
    }

    @Override
    protected String getCryptoCurrency() {
        return context.getCurrencyCode();
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (!(spec.getWallet() instanceof IGeneratesNewDepositCryptoAddress))
            throw new IllegalArgumentException("Wallet '" + spec.getWallet().getClass() +
                    "' does not implement IGeneratesNewDepositCryptoAddress");
        if (!(spec.getWallet() instanceof IQueryableWallet))
            throw new IllegalArgumentException("Wallet '" + spec.getWallet().getClass() +
                    "' does not implement IQueryableWallet");
        context.getUtil().validateAmount(spec.getTotal()); // Throws exception if amount is invalid

        // Format addresses
        for (IPaymentOutput output : spec.getOutputs()) {
            output.setAddress(context.getUtil().parseAddress(output.getAddress()));
        }
        return super.createPaymentRequest(spec);
    }

    @Override
    public void registerPaymentRequest(PaymentRequest request) {
        // Request websocket notifications (if supported by wallet)
        PaymentRequestContext requestContext = new PaymentRequestContext(null);
        if (request.getWallet() instanceof INanoRpcWallet) {
            NanoWsClient wsClient = ((INanoRpcWallet)request.getWallet()).getWsClient();
            if (wsClient != null) {
                requestContext = new PaymentRequestContext(wsClient);
                final PaymentRequestContext fContext = requestContext; // Final for lambda
                // Attempt to add the account to the topic filter
                threadPool.submit(() -> {
                    fContext.wsRequestInitialized = wsClient.addDepositWatcher(
                            request.getAddress(), () -> poll(request, fContext, true));
                });
            } else {
                log.debug("Using RPC polling as the wallet doesn't have a websocket configured. {}", request);
            }
        } else {
            log.debug("Using wallet polling as the wallet isn't an INanoRpcWallet. {}", request);
        }
        requests.put(request, requestContext);

        // Register in polling support
        super.registerPaymentRequest(request);
    }

    @Override
    protected void poll(PaymentRequest request) {
        PaymentRequestContext reqContext = requests.get(request);
        if (reqContext != null) {
            /*
             * We submit to a thread pool to concurrently handle, otherwise requests will queue up when an RPC
             * connection isn't available. Polling is still synchronized and made thread-safe within the custom poll
             * method through a lock.
             */
            threadPool.submit(() -> poll(request, reqContext, false));
        } else {
            log.debug("Unknown PaymentRequest was supplied to poll: {}", request);
        }
    }

    public void poll(PaymentRequest request, PaymentRequestContext reqContext, boolean wsNotification) {
        if (!request.isRemovalCondition() && reqContext.shouldPoll(wsNotification)) {
            // Acquire lock (block and wait for forced poll requests)
            if (reqContext.acquirePollLock(wsNotification)) {
                try {
                    log.debug("Polling (from ws notification: {}) {}", wsNotification, request);

                    // Fetch total amount
                    IQueryableWallet wallet = (IQueryableWallet)request.getWallet();
                    ReceivedAmount received = wallet.getReceivedAmount(request.getAddress(),
                            request.getCryptoCurrency());

                    // Update request state
                    updateRequestState(request, received.getTotalAmountReceived(), received.getConfirmations());
                } catch (Exception e) {
                    log.error("Couldn't poll payment with RPC.", e);
                } finally {
                    reqContext.releasePollLock();
                }
            } else {
                log.debug("Skipping poll call as another is in progress {}", request);
            }
        }
    }

    /**
     * Override to allow state to be re-fired more than once.
     * Also expire websocket requests upon completion or timeout.
     * */
    @Override
    protected void setState(PaymentRequest request, int newState) {
        // Only allow re-fires for SEEN states
        int prevState = request.getState();
        if (newState == prevState && newState != PaymentRequest.STATE_SEEN_TRANSACTION)
            return;

        request.setState(newState);
        log.debug("Transaction state changed: {} -> {}, {}", prevState, newState, request);

        // Notify listener
        IPaymentRequestListener listener = request.getListener();
        if (listener != null)
            listener.stateChanged(request, prevState, request.getState());

        // Finalize payment
        if (request.isRemovalCondition()) {
            PaymentRequestContext context = requests.get(request);
            if (context != null) {
                log.debug("Finalizing payment request for address {}", request.getAddress());
                requests.remove(request);
                // End websocket watcher
                if (context.wsClient != null) {
                    threadPool.submit(() -> context.wsClient.endDepositWatcher(request.getAddress()));
                }
                // Send all funds to hot wallet
                if (request.getWallet() instanceof INanoRpcWallet) {
                    ((INanoRpcWallet)request.getWallet()).moveFundsToHotWallet(request.getAddress());
                }
            }
        }
    }

    /** Updates the current state of the payment request (if applicable) */
    private void updateRequestState(PaymentRequest request, BigDecimal totalReceived, int confirmations) {
        int initialState = request.getState();
        if (request.getTxValue().compareTo(totalReceived) != 0) {
            log.debug("Updating request state, total received: {} with {} confs", totalReceived, confirmations);
            request.setTxValue(totalReceived);
            if (totalReceived.compareTo(BigDecimal.ZERO) >= 0) {
                // Change state if new
                if (request.getState() == PaymentRequest.STATE_NEW)
                    setState(request, PaymentRequest.STATE_SEEN_TRANSACTION);

                // Final confirmation state
                if (confirmations > 0 && request.getState() == PaymentRequest.STATE_SEEN_TRANSACTION
                    && totalReceived.compareTo(request.getAmount()) >= 0) {
                    log.info("Transaction confirmed. Total amount received: {}", totalReceived);
                    request.setRemovalConditionForIncomingTransaction();
                    setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                    updateNumberOfConfirmations(request, confirmations);
                    setState(request, PaymentRequest.STATE_REMOVED);
                } else {
                    if (initialState != PaymentRequest.STATE_NEW)
                        setState(request, PaymentRequest.STATE_SEEN_TRANSACTION); // Re-fire state
                    updateNumberOfConfirmations(request, 0);
                }
            }
        }
    }


    /** Contains additional state information about an active payment request. */
    private static class PaymentRequestContext {
        private final Lock pollLock = new ReentrantLock(); // Only allow one request poll at a time
        private final NanoWsClient wsClient;
        private volatile int pollCounter = POLL_SKIP_CYCLES; // First attempt should poll
        private volatile boolean wsRequestInitialized = false;

        public PaymentRequestContext(NanoWsClient wsClient) {
            this.wsClient = wsClient;
        }


        public synchronized boolean shouldPoll(boolean forced) {
            if (forced || !hasActiveWebSocket() || pollCounter++ >= POLL_SKIP_CYCLES) {
                pollCounter = 0;
                return true;
            }
            return false;
        }

        public boolean acquirePollLock(boolean force) {
            if (force) {
                pollLock.lock();
                return true;
            } else {
                return pollLock.tryLock();
            }
        }

        public void releasePollLock() {
            pollLock.unlock();
        }

        public boolean hasActiveWebSocket() {
            return wsRequestInitialized && wsClient != null && wsClient.isActive();
        }
    }

}
