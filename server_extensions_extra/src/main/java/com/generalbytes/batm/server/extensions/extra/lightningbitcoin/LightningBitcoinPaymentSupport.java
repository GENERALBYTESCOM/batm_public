/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ILightningWallet;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentReceipt;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LightningBitcoinPaymentSupport implements IPaymentSupport {
    private static final Logger log = LoggerFactory.getLogger(LightningBitcoinPaymentSupport.class);
    private final Map<String, PaymentRequest> requests = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public boolean init(IExtensionContext context) {
        return true;
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (!(spec.getWallet() instanceof ILightningWallet)) {
            throw new IllegalArgumentException("Unsupported Wallet: " + spec.getWallet().getClass());
        }
        ILightningWallet wallet = (ILightningWallet) spec.getWallet();

        if (spec.getOutputs().size() != 1) {
            throw new IllegalStateException("Only 1 output supported");
        }
        String invoice = spec.getOutputs().get(0).getAddress();

        long validTillMillis = System.currentTimeMillis() + (spec.getValidInSeconds() * 1000);

        PaymentRequest request = new PaymentRequest(spec.getCryptoCurrency(), spec.getDescription(), validTillMillis,
            invoice, spec.getTotal(), BigDecimal.ZERO, spec.getRemoveAfterNumberOfConfirmationsOfIncomingTransaction(), spec.getRemoveAfterNumberOfConfirmationsOfOutgoingTransaction(), wallet, spec.getTimeoutRefundAddress(),
            spec.getOutputs(), spec.isDoNotForward(), null);

        registerPaymentRequest(request);
        return request;
    }

    @Override
    public void registerPaymentRequest(PaymentRequest request) {
        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(() -> {
            try {
                if (request.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
                    return;
                }

                BigDecimal receivedAmount = ((ILightningWallet) request.getWallet()).getReceivedAmount(request.getAddress(), request.getCryptoCurrency());
                if (receivedAmount != null && receivedAmount.compareTo(BigDecimal.ZERO) > 0) {
                    log.info("Received: {}, Requested: {}, {}", receivedAmount, request.getAmount(), request);
                    if (request.getAmount().compareTo(receivedAmount) != 0 && request.getState() != PaymentRequest.STATE_TRANSACTION_INVALID) {
                        // This should not happen, receiving node should not accept a payment when amount does not match the invoice.
                        log.info("Received amount does not match the requested amount");
                        setState(request, PaymentRequest.STATE_TRANSACTION_INVALID);
                        return;
                    }

                    log.info("Amounts matches");
                    request.setTxValue(receivedAmount);
                    setState(request, PaymentRequest.STATE_SEEN_TRANSACTION); // go through both states so the listener can react to both of them
                    setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                    fireNumberOfConfirmationsChanged(request, 999);
                }

            } catch (Throwable t) {
                log.error("", t);
            }
        }, 3, 1, TimeUnit.SECONDS);

        executorService.schedule(() -> {
            try {
                scheduledFuture.cancel(false);
                if (request.getState() != PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
                    log.info("Cancelling {}", request);
                    setState(request, PaymentRequest.STATE_TRANSACTION_TIMED_OUT);
                }
            } catch (Throwable t) {
                log.error("", t);
            }
        }, request.getValidTill() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

        requests.entrySet().removeIf(e -> e.getValue().getValidTill() < System.currentTimeMillis());
        requests.put(request.getAddress(), request);
    }

    @Override
    public boolean isPaymentReceived(String paymentAddress) {
        PaymentRequest paymentRequest = requests.get(paymentAddress);
        return paymentRequest != null && paymentRequest.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN;
    }

    @Override
    public PaymentReceipt getPaymentReceipt(String paymentAddress) {
        PaymentReceipt result = new PaymentReceipt(CryptoCurrency.LBTC.getCode(), paymentAddress);
        PaymentRequest paymentRequest = requests.get(paymentAddress);
        if (paymentRequest != null && paymentRequest.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
            result.setStatus(PaymentReceipt.STATUS_PAID);
            result.setConfidence(PaymentReceipt.CONFIDENCE_SURE);
            result.setAmount(paymentRequest.getAmount());
            result.setTransactionId(paymentRequest.getIncomingTransactionHash());
        }
        return result;
    }

    private void fireNumberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations) {
        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.numberOfConfirmationsChanged(request, numberOfConfirmations, IPaymentRequestListener.Direction.INCOMING);
        }
    }

    private void setState(PaymentRequest request, int newState) {
        int previousState = request.getState();
        request.setState(newState);
        log.debug("Transaction state changed: {} -> {} {}", previousState, newState, request);

        IPaymentRequestListener listener = request.getListener();
        if (listener != null) {
            listener.stateChanged(request, previousState, request.getState());
        }
    }
}
