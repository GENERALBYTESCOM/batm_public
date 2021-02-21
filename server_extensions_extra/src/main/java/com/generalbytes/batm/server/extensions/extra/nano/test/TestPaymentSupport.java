package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.server.extensions.extra.nano.NanoExtension;
import com.generalbytes.batm.server.extensions.extra.nano.NanoPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.nano.NanoCurrencySpecification;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoRPCClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoNodeWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoWSClient;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;

/**
 * Tests NanoRPCWallet with payments
 */
class TestPaymentSupport {

    private static final NanoCurrencySpecification ADDR_SPEC = NanoExtension.CURRENCY_SPEC;


    public static void main(String[] args) {
        try {
            // You need to have node running: i.e.: nano_node --daemon with rpc enabled
            NanoRPCClient rpcClient = new NanoRPCClient(new URL("http://[::1]:7076"));          // RPC
            NanoWSClient wsClient = new NanoWSClient(ADDR_SPEC, URI.create("ws://[::1]:7078")); // Websocket
            String testAccount = "nano_3zkzc8cf5jmgdtecxdd4jjajydcf988kswdhd3hymamk45773r6a5xen36kg";
            BigDecimal testAmount = new BigDecimal("0.000003");


            System.out.println("Running TestPaymentSupport");
            NanoNodeWallet wallet = new NanoNodeWallet(ADDR_SPEC, rpcClient, wsClient, "", testAccount);
            NanoPaymentSupport ps = new NanoPaymentSupport(NanoExtension.CURRENCY_SPEC);
            ps.init(null);
            String description = "Test txn";
            long requestPaymentValidSeconds = 60 * 15; // 15 mins
            PRS spec = new PRS(NanoExtension.CURRENCY_SPEC.getCurrencyCode(), description, requestPaymentValidSeconds,
                    false, wallet);
            spec.addOutput(testAccount, testAmount);
            PaymentRequest pr = ps.createPaymentRequest(spec);

            pr.setListener(new IPaymentRequestListener() {
                @Override
                public void stateChanged(PaymentRequest request, int previousState, int newState) {
                    System.out.printf("stateChanged | received: %.6f, previousState: %d, newState: %d%n",
                        request.getTxValue(), previousState, newState);
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

            System.out.println("Waiting for transfer to " + testAccount);
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
