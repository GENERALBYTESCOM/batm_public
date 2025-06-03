/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
import com.generalbytes.batm.server.extensions.ILightningWallet;
import com.generalbytes.batm.server.extensions.extra.common.PollingPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LightningBitcoinPaymentSupport extends PollingPaymentSupport implements IPaymentSupport {

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (!(spec.getWallet() instanceof ILightningWallet)) {
            throw new IllegalArgumentException("Unsupported Wallet: " + spec.getWallet().getClass());
        }
        return super.createPaymentRequest(spec);
    }

    @Override
    public void poll(PaymentRequest request) {
        try {
            if (request.getState() == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
                return;
            }

            ILightningWallet lightningWallet = (ILightningWallet) request.getWallet();
            ReceivedAmount receivedAmount = lightningWallet.getReceivedAmount(request.getAddress());
            if (receivedAmount == null) {
                return;
            }

            BigDecimal totalAmountReceived = receivedAmount.getTotalAmountReceived();
            if (totalAmountReceived.compareTo(BigDecimal.ZERO) > 0) {
                log.info("Received: {}, Requested: {}, {}", totalAmountReceived, request.getAmount(), request);
                if (request.getAmount().compareTo(totalAmountReceived) != 0 && request.getState() != PaymentRequest.STATE_TRANSACTION_INVALID) {
                    // This should not happen, receiving node should not accept a payment when amount does not match the invoice.
                    log.info("Received amount does not match the requested amount");
                    setState(request, PaymentRequest.STATE_TRANSACTION_INVALID);
                    return;
                }

                log.info("Amounts matches");
                request.setTxValue(totalAmountReceived);
                request.setIncomingTransactionHash(getTransactionHashesAsString(receivedAmount));
                setState(request, PaymentRequest.STATE_SEEN_TRANSACTION); // go through both states so the listener can react to both of them
                setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                updateNumberOfConfirmations(request, 999);
            }
        } catch (Exception e) {
            log.error("Failed to poll payment request: {}", request, e);
        }
    }

    private String getTransactionHashesAsString(ReceivedAmount receivedAmount) {
        List<String> transactionHashes = receivedAmount.getTransactionHashes();
        if (transactionHashes != null && !transactionHashes.isEmpty()) {
            return String.join(" ", transactionHashes);
        }
        return null;
    }

    @Override
    protected String getCryptoCurrency() {
        return CryptoCurrency.LBTC.getCode();
    }

    @Override
    protected long getPollingPeriodMillis() {
        return TimeUnit.SECONDS.toMillis(1);
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return TimeUnit.SECONDS.toMillis(3);
    }

}
