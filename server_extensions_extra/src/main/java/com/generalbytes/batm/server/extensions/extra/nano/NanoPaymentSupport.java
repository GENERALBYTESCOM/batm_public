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

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.PollingPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.nano_node.NanoRPCWallet;
import com.generalbytes.batm.server.extensions.extra.nano.test.PRS;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NanoPaymentSupport extends PollingPaymentSupport {
    private static final Logger log = LoggerFactory.getLogger(NanoPaymentSupport.class);

    @Override
    protected long getPollingPeriodMillis() {
        return TimeUnit.MILLISECONDS.toMillis(200);
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return TimeUnit.MILLISECONDS.toMillis(500);
    }

    @Override
    protected String getCryptoCurrency() {
        return CryptoCurrency.NANO.getCode();
    }

    /**
     * Server calls this method to create request for payment. Payment When this
     * method is called your {@link IPaymentSupport} implementation should start
     * listening for payments on address. This method creates and registers the
     * payment request.
     * {@link IPaymentSupport#registerPaymentRequest(PaymentRequest)} is called as
     * part of this method.
     *
     * @param spec
     * @return
     */
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
        if (spec.getTotal().stripTrailingZeros().scale() > 30) {
            throw new IllegalArgumentException("NANO has maximum of 30 decimals");
        }
        return super.createPaymentRequest(spec);
    }

    @Override
    protected void poll(PaymentRequest request) {
        try {
            IQueryableWallet wallet = (IQueryableWallet) request.getWallet();
            ReceivedAmount receivedAmount = wallet.getReceivedAmount(request.getAddress(), request.getCryptoCurrency());
            System.out.print(receivedAmount.getTotalAmountReceived());
            BigDecimal totalReceived = receivedAmount.getTotalAmountReceived();
            int confirmations = receivedAmount.getConfirmations();
            if (totalReceived.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }

            if (totalReceived.compareTo(request.getAmount()) < 0) {
                log.info("Received amount ({}) does not match the requested amount ({}), {}", totalReceived,
                        request.getAmount(), request);
                return;
            }

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

    public static void main(String[] args) {
        // You need to have node running: i.e.: nano_node --daemon with rpc enabled

        try {
            NanoRPCWallet wallet = new NanoRPCWallet("http://[::1]:7076", "", "");
            NanoPaymentSupport ps = new NanoPaymentSupport();
            ps.init(null);
            String description = "Just a test";
            long requestPaymentValidSeconds = 60 * 15; // 15 mins
            PRS spec = new PRS(CryptoCurrency.NANO.getCode(), description, requestPaymentValidSeconds, false, wallet);
            // Enter a new address which hasn't had any nano sent to it
            spec.addOutput("nano_3pszk9xf4mogtf8yebwurjcyhbtscun4hq596sxym7roxwt9gy8ieou1jj9i",
                    new BigDecimal("0.00001"));

            PaymentRequest pr = ps.createPaymentRequest(spec);
            System.out.println(pr);
            pr.setListener(new IPaymentRequestListener() {
                @Override
                public void stateChanged(PaymentRequest request, int previousState, int newState) {
                    System.out.println("stateChanged = " + request + " previousState: " + previousState + " newState: "
                            + newState);
                }

                @Override
                public void numberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations,
                        IPaymentRequestListener.Direction direction) {
                    System.out.println("numberOfConfirmationsChanged = " + request + " numberOfConfirmations: "
                            + numberOfConfirmations + " direction: " + direction);
                }

                @Override
                public void refundSent(PaymentRequest request, String toAddress, String cryptoCurrency,
                        BigDecimal amount) {
                    // Not supported
                }
            });
            System.out.println("Waiting for transfer");

            Thread.sleep(20 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}