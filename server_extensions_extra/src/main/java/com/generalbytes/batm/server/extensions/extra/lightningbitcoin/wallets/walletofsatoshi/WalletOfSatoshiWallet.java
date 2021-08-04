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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ILightningChannel;
import com.generalbytes.batm.server.extensions.ThrowingSupplier;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.AbstractLightningWallet;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.InvoiceRequest;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.Payment;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class WalletOfSatoshiWallet extends AbstractLightningWallet {

    private static final Logger log = LoggerFactory.getLogger(WalletOfSatoshiWallet.class);

    private final WalletOfSatoshiAPI api;

    public WalletOfSatoshiWallet(String apiToken, String apiSecret) throws GeneralSecurityException {
        api = WalletOfSatoshiAPI.create(apiToken, apiSecret);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {

        log.info("Paying {} to invoice {}", amount, destinationAddress);

        PaymentRequest request = new PaymentRequest();
        request.amount = amount;
        request.address = destinationAddress; // invoice
        request.description = description;
        request.currency = "LIGHTNING";
        Payment payment = callChecked(cryptoCurrency, () -> payInvoice(request));

        if (payment == null) {
            log.warn("SendPayment failed");
            return null;
        }

        if (payment.status != Payment.Status.PAID) {
            log.warn("SendPayment failed: {}", payment);
            return null;
        }
        return payment.id;
    }

    private Payment payInvoice(PaymentRequest request) throws IOException {
        Payment payment = api.createPayment(request);
        int retries = 3;
        while (retries-- > 0 && isPending(payment)) {
            payment = api.getPaymentById(payment.id);
        }
        return payment;
    }

    private boolean isPending(Payment payment) {
        return payment != null && (payment.status == Payment.Status.PENDING || payment.status == Payment.Status.QUEUED);
    }

    @Override
    public String getInvoice(BigDecimal cryptoAmount, String cryptoCurrency, Long paymentValidityInSec, String description) {
        InvoiceRequest request = new InvoiceRequest();
        request.amount = cryptoAmount;
        request.description = description;
        //paymentValidityInSec not supported
        return callChecked(cryptoCurrency, () -> api.createInvoice(request).invoice);
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> api.getBalances().lightning);
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        return getInvoice(BigDecimal.ZERO, CryptoCurrency.LBTC.getCode(), null, "Wallet of Satoshi deposit");
    }

    @Override
    public String getPubKey() {
        return null;
    }


    @Override
    public BigDecimal getReceivedAmount(String destinationAddress, String cryptoCurrency) {
        return callChecked(cryptoCurrency, () ->
            api.getPayments(null, null).stream() // gets just 100 last payments
                .filter(payment -> destinationAddress.equals(payment.address))
                .filter(payment -> "CREDIT".equals(payment.type))
                .findFirst()
                .map(payment -> payment.amount)
                .orElse(BigDecimal.ZERO)
        );
    }

    @Override
    public List<? extends ILightningChannel> getChannels() {
        return Collections.emptyList(); // this is a custodial wallet, the node's channels does not belong to the user's account
    }

    @Override
    protected <T> T callChecked(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (HttpStatusIOException e) {
            log.error("HTTP status code was not OK: {}, body: {}", e.getHttpStatusCode(), e.getHttpBody());
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}

