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
package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.server.extensions.extra.nano.NanoExtension;
import com.generalbytes.batm.server.extensions.extra.nano.NanoPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.nano.NanoCurrencySpecification;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoRPCClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoNodeWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoWSClient;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import uk.oczadly.karl.jnano.rpc.RpcQueryNode;
import uk.oczadly.karl.jnano.rpc.request.node.RequestBlockCount;
import uk.oczadly.karl.jnano.rpc.request.node.RequestVersion;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;

/**
 * Tests NanoRPCWallet with payments
 */
class TestPaymentSupport {

    public static void main(String[] args) {
        try {
            /*
             * You need to have node running: i.e.: nano_node --daemon with rpc enabled
             * WebSocket host may be null if using RPC polling only.
             * A value totalling paymentValue should be sent to paymentAccount.
             */
            String rpcHost = "http://[::1]:7076", wsHost = "ws://[::1]:7078";
            String paymentAccount = "nano_3zkzc8cf5jmgdtecxdd4jjajydcf988kswdhd3hymamk45773r6a5xen36kg";
            BigDecimal paymentValue = new BigDecimal("0.000017");


            System.out.println("Running TestPaymentSupport");
            NanoCurrencySpecification addrSpec = NanoExtension.CURRENCY_SPEC;
            NanoRPCClient rpcClient = new NanoRPCClient(new URL(rpcHost));
            NanoWSClient wsClient = wsHost == null ? null : new NanoWSClient(addrSpec, URI.create(wsHost));
            NanoNodeWallet wallet = new NanoNodeWallet(addrSpec, rpcClient, wsClient, "", paymentAccount);
            NanoPaymentSupport ps = new NanoPaymentSupport(NanoExtension.CURRENCY_SPEC);
            ps.init(null);
            String description = "Test txn";
            long requestPaymentValidSeconds = 60 * 15; // 15 mins
            PRS spec = new PRS(NanoExtension.CURRENCY_SPEC.getCurrencyCode(), description, requestPaymentValidSeconds,
                    false, wallet);
            spec.addOutput(paymentAccount, paymentValue);
            PaymentRequest pr = ps.createPaymentRequest(spec);

            pr.setListener(new IPaymentRequestListener() {
                @Override
                public void stateChanged(PaymentRequest request, int previousState, int newState) {
                    System.out.printf("stateChanged | received: %.6f / %.6f, previousState: %d, newState: %d%n",
                        request.getTxValue(), request.getAmount(), previousState, newState);
                    if (newState == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN) {
                        System.out.println("--------------------------------------");
                        System.out.printf("TRANSACTION COMPLETED | Received %.6f%n", request.getTxValue());
                        System.out.println("--------------------------------------");
                    }
                }

                @Override
                public void numberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations,
                                                         IPaymentRequestListener.Direction direction) {
                    System.out.println("numberOfConfirmationsChanged | numberOfConfirmations: "
                        + numberOfConfirmations + " direction: " + direction);
                }

                @Override
                public void refundSent(PaymentRequest request, String toAddress, String cryptoCurrency, BigDecimal amount) {
                    // Not supported
                    System.err.println("refundSent");
                }
            });

            System.out.println("Waiting for transfer to " + paymentAccount);
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
