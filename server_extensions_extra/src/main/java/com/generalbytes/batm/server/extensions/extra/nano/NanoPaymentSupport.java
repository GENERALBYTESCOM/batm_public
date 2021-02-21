/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.common.PollingPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Payment;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoNodeWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoWSClient;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.oczadly.karl.jnano.model.NanoAmount;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * PaymentSupport utilising both RPC polling and websocket notifications
 */
public class NanoPaymentSupport extends PollingPaymentSupport {

    private static final long POLL_PERIOD_MS = 500; // 500 ms

    /** Multiplier of POLL_PERIOD_MS, used when WebSocket is active. */
    private static final int POLL_SKIP_CYCLES = 30; // 15 sec


    private static final Logger log = LoggerFactory.getLogger(NanoPaymentSupport.class);

    private final NanoCurrencySpecification currencySpec;
    /** Context info for payment requests. */
    private final Map<PaymentRequest, PaymentRequestContext> requestContexts = new ConcurrentHashMap<>();
    private final ExecutorService pollExecutor = Executors.newFixedThreadPool(2);

    public NanoPaymentSupport(NanoCurrencySpecification currencySpec) {
        this.currencySpec = currencySpec;
    }


    @Override
    protected long getPollingPeriodMillis() {
        return POLL_PERIOD_MS;
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return 0;
    }

    @Override
    protected String getCryptoCurrency() {
        return currencySpec.getCurrencyCode();
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (!(spec.getWallet() instanceof IGeneratesNewDepositCryptoAddress)) {
            throw new IllegalArgumentException("Wallet '" + spec.getWallet().getClass() + "' does not implement "
                    + IGeneratesNewDepositCryptoAddress.class.getSimpleName());
        }
        if (!(spec.getWallet() instanceof IQueryableWallet)) {
            throw new IllegalArgumentException("Wallet '" + spec.getWallet().getClass() + "' does not implement "
                    + IQueryableWallet.class.getSimpleName());
        }
        NanoAmount.valueOfNano(spec.getTotal()); // Assert value is valid (throws exception)
        return super.createPaymentRequest(spec);
    }

    @Override
    public void registerPaymentRequest(PaymentRequest request) {
        // Request websocket notifications (if supported by wallet)
        PaymentRequestContext requestContext = null;
        if (request.getWallet() instanceof NanoNodeWallet) {
            NanoWSClient wsClient = ((NanoNodeWallet)request.getWallet()).getWebSocketClient();
            if (wsClient != null) {
                final PaymentRequestContext context = new PaymentRequestContext(request, wsClient);
                wsClient.requestPaymentNotifications(request.getOutputs(), (hash, amount) -> poll(context, true));
                requestContext = context;
            } else {
                log.debug("Using RPC polling for request as the wallet doesn't support websockets. {}", request);
            }
        } else {
            log.debug("Using wallet polling for request as the wallet isn't a NanoNodeWallet. {}", request);
        }

        // Register context
        if (requestContext == null)
            requestContext = new PaymentRequestContext(request, null);
        requestContexts.put(request, requestContext);

        // Register in polling support
        super.registerPaymentRequest(request);
    }

    @Override
    protected void poll(PaymentRequest request) {
        PaymentRequestContext context = requestContexts.get(request);
        if (context != null) {
            // Submit to an executor to prevent requests from stacking up
            pollExecutor.submit(() -> poll(context, false));
        } else {
            log.warn("Unknown PaymentRequest was supplied to poll() {}", request);
        }
    }

    public void poll(PaymentRequestContext context, boolean force) {
        PaymentRequest request = context.request;
        if (context.shouldPoll(force)) {
            // Acquire lock (block and wait for forced poll requests)
            if (context.acquireLock(force)) {
                try {
                    log.debug("Polling (forced: {}) {}", force, request);

                    // Fetch total amount
                    IQueryableWallet wallet = (IQueryableWallet)request.getWallet();
                    ReceivedAmount received = wallet.getReceivedAmount(request.getAddress(), request.getCryptoCurrency());

                    // Update request state
                    updateRequestState(request, received.getTotalAmountReceived(), received.getConfirmations());
                } catch (Exception e) {
                    log.error("Couldn't poll payment with RPC.", e);
                } finally {
                    context.pollLock.unlock();
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

        // Expire websocket if supported and a final state
        if (isStateFinished(newState)) {
            PaymentRequestContext context = requestContexts.get(request);
            if (context != null) context.finalizeRequest();
        }

        // Notify listener
        IPaymentRequestListener listener = request.getListener();
        if (listener != null)
            listener.stateChanged(request, prevState, request.getState());
    }

    private void updateRequestState(PaymentRequest request, BigDecimal totalReceived, int confirmations) {
        int initialState = request.getState();
        if (!isStateFinished(initialState)) {
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
                        setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                        updateNumberOfConfirmations(request, confirmations);
                    } else {
                        if (initialState != PaymentRequest.STATE_NEW)
                            setState(request, PaymentRequest.STATE_SEEN_TRANSACTION); // Re-fire state
                        updateNumberOfConfirmations(request, 0);
                    }
                }
            }
        } else {
            log.warn("Couldn't update state as payment is already finalized. {}", request);
        }
    }

    private static boolean isStateFinished(int state) {
        return state != PaymentRequest.STATE_NEW && state != PaymentRequest.STATE_SEEN_TRANSACTION;
    }


    /** Contains additional state information about an active payment request. */
    private class PaymentRequestContext {
        private final Lock pollLock = new ReentrantLock(); // Only allow one request poll at a time
        private final PaymentRequest request;
        private final NanoWSClient wsClient;
        private volatile int pollCounter = POLL_SKIP_CYCLES; // First poll should succeed

        public PaymentRequestContext(PaymentRequest request, NanoWSClient wsClient) {
            this.request = request;
            this.wsClient = wsClient;
        }


        public synchronized boolean shouldPoll(boolean forced) {
            if (forced || !isUsingWebSocket() || pollCounter++ >= POLL_SKIP_CYCLES) {
                pollCounter = 0;
                return true;
            }
            return false;
        }

        public boolean acquireLock(boolean force) {
            if (force) {
                pollLock.lock();
                return true;
            } else {
                return pollLock.tryLock();
            }
        }

        public boolean isUsingWebSocket() {
            return wsClient != null && wsClient.isConnected();
        }

        public void finalizeRequest() {
            log.debug("Finalizing payment request {}", request);
            requestContexts.remove(request);
            wsClient.endPaymentNotifications(request.getOutputs());
        }
    }

}