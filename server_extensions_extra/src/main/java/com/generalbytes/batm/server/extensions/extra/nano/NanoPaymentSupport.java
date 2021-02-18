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
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoNodeWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoWSClient;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.oczadly.karl.jnano.model.HexData;
import uk.oczadly.karl.jnano.model.NanoAmount;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PaymentSupport utilising both RPC polling and websocket notifications
 */
public class NanoPaymentSupport extends PollingPaymentSupport {

    private static final Logger log = LoggerFactory.getLogger(NanoPaymentSupport.class);

    /** Context info for websocket payment requests. */
    private final Map<PaymentRequest, PaymentRequestContext> wsRequestContexts = new ConcurrentHashMap<>();


    @Override
    protected long getPollingPeriodMillis() {
        return 500;
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return 1000;
    }

    @Override
    protected String getCryptoCurrency() {
        return NanoExtension.CURRENCY_CODE;
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
        // Register in websocket client (if supported by wallet)
        if (request.getWallet() instanceof NanoNodeWallet) {
            NanoWSClient wsClient = ((NanoNodeWallet)request.getWallet()).getWebSocketClient();
            if (wsClient != null) {
                PaymentRequestContext requestContext = new PaymentRequestContext(request, wsClient);
                wsRequestContexts.put(request, requestContext);
                wsClient.registerPaymentRequest(request, new DepositHandler(requestContext));
            } else {
                log.debug("Using RPC polling for request as the wallet doesnt support websockets. {}", request);
            }
        } else {
            log.debug("Using wallet polling for request as the wallet isnt a NanoNodeWallet. {}", request);
        }

        // Register in polling support
        super.registerPaymentRequest(request);
    }

    @Override
    protected void poll(PaymentRequest request) {
        // Skip polling if using websockets
        if (wsRequestContexts.containsKey(request)) return;

        // Poll with RPC
        try {
            // Fetch total amount
            IQueryableWallet wallet = (IQueryableWallet)request.getWallet();
            ReceivedAmount received = wallet.getReceivedAmount(request.getAddress(), request.getCryptoCurrency());
            BigDecimal receivedAmount = received.getTotalAmountReceived();

            // Update request state (if value changed)
            if (request.getTxValue().compareTo(receivedAmount) != 0) {
                request.setTxValue(receivedAmount);
                updateRequestState(request, received.getConfirmations());
            }
        } catch (Exception e) {
            log.error("Couldn't poll payment with RPC.", e);
        }
    }

    /**
     * Override to allow state to be re-fired more than once.
     * Also expire websocket requests upon completion or timeout.
     * */
    @Override
    protected void setState(PaymentRequest request, int newState) {
        // Only allow re-fires for SEEN states
        int previousState = request.getState();
        if (newState == previousState && newState != PaymentRequest.STATE_SEEN_TRANSACTION)
            return;

        log.debug("Transaction state changed: {} -> {}, {}", previousState, newState, request);
        request.setState(newState);

        // Expire websocket if supported and a final state
        if (!isActiveState(newState)) {
            PaymentRequestContext context = wsRequestContexts.get(request);
            if (context != null)
                context.finalizeWsRequest();
        }

        // Notify listener
        IPaymentRequestListener listener = request.getListener();
        if (listener != null)
            listener.stateChanged(request, previousState, request.getState());
    }

    private void updateRequestState(PaymentRequest request, int confirmations) {
        // Ignore if state already finalized
        if (!isActiveState(request.getState())) return;

        log.debug("Updating request state.");
        BigDecimal received = request.getTxValue();
        if (received.compareTo(BigDecimal.ZERO) >= 0) {
            // Change state (or re-fire for more amounts)
            if (request.getState() == PaymentRequest.STATE_NEW ||
                    request.getState() == PaymentRequest.STATE_SEEN_TRANSACTION) {
                setState(request, PaymentRequest.STATE_SEEN_TRANSACTION);
            }
            // Final confirmation state
            if (confirmations > 0 && request.getState() == PaymentRequest.STATE_SEEN_TRANSACTION
                        && received.compareTo(request.getAmount()) >= 0) {
                log.info("Transaction confirmed. Total amount received: {}", received);
                setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                updateNumberOfConfirmations(request, confirmations);
            } else {
                updateNumberOfConfirmations(request, 0);
            }
        }
    }

    private static boolean isActiveState(int state) {
        return state == PaymentRequest.STATE_NEW || state == PaymentRequest.STATE_SEEN_TRANSACTION;
    }


    /** Handles incoming deposits from the WebSocket. */
    private class DepositHandler implements NanoWSClient.DepositListener {
        private final PaymentRequestContext context;

        public DepositHandler(PaymentRequestContext requestContext) {
            this.context = requestContext;
        }

        @Override
        public void onDeposit(HexData hash, NanoAmount amount) {
            context.addDeposit(hash, amount.getAsNano());
        }

        @Override
        public void onSocketDisconnect() {
            context.finalizeWsRequest();
        }
    }

    /** Contains additional state information about an active payment request. */
    private class PaymentRequestContext {
        private final PaymentRequest request;
        private final NanoWSClient wsClient;
        private final Set<HexData> confirmedHashes = new HashSet<>();
        private volatile boolean isWsActive = true;

        public PaymentRequestContext(PaymentRequest request, NanoWSClient wsClient) {
            this.request = request;
            this.wsClient = wsClient;
        }


        public synchronized void addDeposit(HexData hash, BigDecimal amount) {
            if (isWsActive && confirmedHashes.add(hash)) {
                request.setTxValue(request.getTxValue().add(amount));
                request.setIncomingTransactionHash(hash.toHexString());
                updateRequestState(request, 1);
            }
        }

        public synchronized void finalizeWsRequest() {
            log.debug("Finalizing websocket request {}", request);
            isWsActive = false;
            wsRequestContexts.remove(request);
            wsClient.expirePaymentRequest(request);
        }
    }

}