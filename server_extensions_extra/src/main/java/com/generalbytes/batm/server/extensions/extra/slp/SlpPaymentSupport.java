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
package com.generalbytes.batm.server.extensions.extra.slp;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.common.PollingPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class SlpPaymentSupport extends PollingPaymentSupport {
    private static final Logger log = LoggerFactory.getLogger(SlpPaymentSupport.class);

    private final String cryptoCurrency;
    private final int decimals;

    public SlpPaymentSupport(String cryptoCurrency, int decimals) {
        this.cryptoCurrency = cryptoCurrency;
        this.decimals = decimals;
    }

    @Override
    protected long getPollingPeriodMillis() {
        return TimeUnit.SECONDS.toMillis(60);
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return TimeUnit.SECONDS.toMillis(60);
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (!(spec.getWallet() instanceof IGeneratesNewDepositCryptoAddress)) {
            throw new IllegalArgumentException("Wallet '" + spec.getWallet().getClass() + "' does not implement " + IGeneratesNewDepositCryptoAddress.class.getSimpleName());
        }
        if (!(spec.getWallet() instanceof IQueryableWallet)) {
            throw new IllegalArgumentException("Wallet '" + spec.getWallet().getClass() + "' does not implement " + IQueryableWallet.class.getSimpleName());
        }
        if (spec.getTotal().stripTrailingZeros().scale() > decimals) {
            throw new IllegalArgumentException(cryptoCurrency + " has " + decimals + " decimals");
        }
        return super.createPaymentRequest(spec);
    }

    @Override
    protected String getCryptoCurrency() {
        return cryptoCurrency;
    }

    @Override
    protected void poll(PaymentRequest request) {
        try {
            IQueryableWallet wallet = (IQueryableWallet) request.getWallet();
            ReceivedAmount receivedAmount = wallet.getReceivedAmount(request.getAddress(), request.getCryptoCurrency());
            BigDecimal totalReceived = receivedAmount.getTotalAmountReceived();
            int confirmations = receivedAmount.getConfirmations();

            if (totalReceived.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }

            if (totalReceived.compareTo(request.getAmount()) != 0) {
                log.info("Received amount ({}) does not match the requested amount ({}), {}", totalReceived, request.getAmount(), request);
                // stop future polling
                setState(request, PaymentRequest.STATE_TRANSACTION_INVALID);
                return;
            }

            // correct amount received

            if (request.getState() == PaymentRequest.STATE_NEW) {
                log.info("Received: {}, amounts matches. {}", totalReceived, request);
                request.setTxValue(totalReceived);
                setState(request, PaymentRequest.STATE_SEEN_TRANSACTION);
            }

            if (confirmations > 0) {
                if (request.getState() == PaymentRequest.STATE_SEEN_TRANSACTION) {
                    log.info("Transaction confirmed. {}", request);
                    setState(request, PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN);
                }
                updateNumberOfConfirmations(request, confirmations);
            }

        } catch (Exception e) {
            log.error("", e);
        }
    }
}
