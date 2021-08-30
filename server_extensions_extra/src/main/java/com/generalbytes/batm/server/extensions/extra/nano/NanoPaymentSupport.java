package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.common.PollingPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoWsClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.node.INanoRpcWallet;
import com.generalbytes.batm.server.extensions.payment.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * This payment support will utilise the websocket value if configured to reduce load on the RPC endpoint, and minimize
 * delays when processing and verifying deposits. If not provided by the wallet (websocket is null, or cannot connect),
 * then it will simply resort to standard RPC polling.
 */
public class NanoPaymentSupport extends PollingPaymentSupport {

    private static final Logger log = LoggerFactory.getLogger(NanoPaymentSupport.class);

    private static final long POLL_PERIOD = 750; // 750ms
    private static final int POLL_SKIP_CYCLES = 26; // 19.5s (multiplier of POLL_PERIOD when websocket is active)

    private static final List<Integer> FINAL_STATES = Arrays.asList(
        PaymentRequest.STATE_TRANSACTION_INVALID,
        PaymentRequest.STATE_REMOVED,
        PaymentRequest.STATE_SOMETHING_ARRIVED_AFTER_TIMEOUT,
        PaymentRequest.STATE_TRANSACTION_TIMED_OUT
    );

    private final NanoExtensionContext currencyContext;
    private final Map<PaymentRequest, PaymentRequestContext> requests = new ConcurrentHashMap<>();
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public NanoPaymentSupport(NanoExtensionContext currencyContext) {
        this.currencyContext = currencyContext;
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
        return currencyContext.getCurrencyCode();
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (!currencyContext.getCurrencyCode().equalsIgnoreCase(spec.getCryptoCurrency()))
            throw new IllegalArgumentException("Unsupported CryptoCurrency.");
        if (!(spec.getWallet() instanceof INanoRpcWallet))
            throw new IllegalArgumentException("Wallet " + spec.getWallet().getClass() + " does not implement INanoRpcWallet");
        currencyContext.getUtil().validateAmount(spec.getTotal()); // Throws exception if amount is invalid

        long validTillMillis = System.currentTimeMillis() + (spec.getValidInSeconds() * 1000);
        String address = currencyContext.getUtil().parseAddress(spec.getOutputs().get(0).getAddress());
        String refundAddr = spec.getTimeoutRefundAddress() == null ? null :
                currencyContext.getUtil().parseAddress(spec.getTimeoutRefundAddress());

        PaymentRequest request = new PaymentRequest(spec.getCryptoCurrency(), spec.getDescription(), validTillMillis,
            address, spec.getTotal(), spec.getTolerance(),
            spec.getRemoveAfterNumberOfConfirmationsOfIncomingTransaction(),
            spec.getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction(), spec.getWallet(),
            refundAddr, spec.getOutputs(), spec.isDoNotForward(), null);

        registerPaymentRequest(request);
        return request;
    }

    @Override
    public void registerPaymentRequest(PaymentRequest request) {
        INanoRpcWallet wallet = (INanoRpcWallet)request.getWallet();

        // Request websocket notifications (if supported by wallet)
        PaymentRequestContext requestContext = new PaymentRequestContext(wallet, null);
        if (request.getWallet() instanceof INanoRpcWallet) {
            NanoWsClient wsClient = ((INanoRpcWallet)request.getWallet()).getWsClient();
            if (wsClient != null) {
                requestContext = new PaymentRequestContext(wallet, wsClient);
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

    @Override
    protected void setState(PaymentRequest request, int newState) {
        int prevState = request.getState();
        if (newState == prevState) return;

        request.setState(newState);
        log.debug("Transaction state changed: {} -> {}, {}", prevState, newState, request);

        // Notify listener
        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.stateChanged(request, prevState, request.getState());
        }

        if (!FINAL_STATES.contains(prevState) && newState == PaymentRequest.STATE_TRANSACTION_TIMED_OUT) {
            // Payment timed out - process refund
            processRefund(request, requests.get(request));
        }

        // Finalize payment
        if (FINAL_STATES.contains(newState)) {
            PaymentRequestContext context = requests.get(request);
            if (context != null) {
                log.debug("Stopping payment request for deposit address {}", request.getAddress());
                requests.remove(request);
                // End websocket watcher
                if (context.wsClient != null) {
                    threadPool.submit(() -> context.wsClient.endDepositWatcher(request.getAddress()));
                }
            }
        }
    }

    /** Updates the current state of the payment request (if applicable) */
    private void updateRequestState(PaymentRequest request, BigDecimal totalReceived, int confirmations) {
        int initialState = request.getState();
        if (request.getTxValue().compareTo(totalReceived) != 0 || confirmations >= 1) {
            log.debug("Updating request state, total received: {} with {} confs", totalReceived, confirmations);
            request.setTxValue(totalReceived);
            PaymentRequestContext context = requests.get(request);
            if (!request.wasAlreadyRefunded() && totalReceived.compareTo(BigDecimal.ZERO) >= 0) {
                // Change state if new
                if (request.getState() == PaymentRequest.STATE_NEW)
                    setState(request, PaymentRequest.STATE_SEEN_TRANSACTION);

                if (confirmations > 0 && request.getState() == PaymentRequest.STATE_SEEN_TRANSACTION
                    && totalReceived.compareTo(request.getAmount()) >= 0) {
                    // Transaction confirmed
                    if (totalReceived.subtract(request.getTolerance()).compareTo(request.getAmount()) <= 0) {
                        // Within tolerance
                        log.info("Transaction confirmed. Total amount received: {}", totalReceived);
                        request.setRemovalConditionForIncomingTransaction();
                        setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                        updateNumberOfConfirmations(request, Integer.MAX_VALUE);
                        setState(request, PaymentRequest.STATE_REMOVED);
                        // Send all funds to hot wallet
                        context.wallet.moveFundsToHotWallet(request.getAddress());
                    } else {
                        // Too many funds sent, refund
                        setState(request, PaymentRequest.STATE_TRANSACTION_INVALID);
                        processRefund(request, context);
                    }
                } else {
                    // Not enough funds, or no confirmation
                    if (initialState != PaymentRequest.STATE_NEW)
                        setState(request, PaymentRequest.STATE_SEEN_TRANSACTION); // Re-fire state
                    updateNumberOfConfirmations(request, 0);
                }
            }
        }
    }

    private void processRefund(PaymentRequest request, PaymentRequestContext context) {
        if (!request.wasAlreadyRefunded() && context != null) {
            log.debug("Attempting to process refund for request {}", request);
            try {
                // Find suitable refund account
                String refundAddr = request.getTimeoutRefundAddress();
                if (refundAddr == null) {
                    List<NanoRpcClient.Block> blocks =
                        context.wallet.getRpcClient().getTransactionHistory(request.getAddress());
                    for (NanoRpcClient.Block block : blocks) {
                        if (block.type.equals("receive")) {
                            refundAddr = block.account;
                        }
                    }
                }
                if (refundAddr != null) {
                    request.setAsAlreadyRefunded();
                    // Process refund
                    log.debug("Sending refund to {}", refundAddr);
                    BigInteger amount = context.wallet.sendAllFromWallet(request.getAddress(), refundAddr);
                    // Notify listener
                    IPaymentRequestListener listener = request.getListener();
                    if (listener != null) {
                        listener.refundSent(request, refundAddr, request.getCryptoCurrency(),
                                currencyContext.getUtil().amountFromRaw(amount));
                    }
                } else {
                    log.warn("Couldn't process refund as no refund account was found.");
                }
            } catch (IOException | NanoRpcClient.RpcException e) {
                log.error("Failed to process refund transaction", e);
            }
        }
    }


    /** Contains additional state information about an active payment request. */
    private static class PaymentRequestContext {
        private final INanoRpcWallet wallet;
        private final Lock pollLock = new ReentrantLock(); // Only allow one request poll at a time
        private final NanoWsClient wsClient;
        private volatile int pollCounter = POLL_SKIP_CYCLES; // First attempt should poll
        private volatile boolean wsRequestInitialized = false;

        public PaymentRequestContext(INanoRpcWallet wallet, NanoWsClient wsClient) {
            this.wallet = wallet;
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
