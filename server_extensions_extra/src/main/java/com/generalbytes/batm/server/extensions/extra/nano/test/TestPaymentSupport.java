package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.server.extensions.extra.nano.NanoExtension;
import com.generalbytes.batm.server.extensions.extra.nano.NanoPaymentSupport;
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

    public static void main(String[] args) throws Exception {
        try {
            // You need to have node running: i.e.: nano_node --daemon with rpc enabled
            NanoRPCClient rpcClient = new NanoRPCClient(new URL("http://[::1]:7076"));      // RPC
            NanoWSClient wsClient = new NanoWSClient(URI.create("ws://[::1]:7078"));        // Websocket
            String testAccount = "nano_1rom5kz795itb5tb8t7ogygqjppsg8656uc9oatrsjc6zr6fmsjeodntq8z8";
            BigDecimal testAmount = new BigDecimal("0.000003");


            System.out.println("Running TestPaymentSupport");
            NanoNodeWallet wallet = new NanoNodeWallet(rpcClient, wsClient, "", testAccount);
            NanoPaymentSupport ps = new NanoPaymentSupport();
            ps.init(null);
            String description = "Test txn";
            long requestPaymentValidSeconds = 60 * 15; // 15 mins
            PRS spec = new PRS(NanoExtension.CURRENCY_CODE, description, requestPaymentValidSeconds, false, wallet);
            spec.addOutput(testAccount, testAmount);
            PaymentRequest pr = ps.createPaymentRequest(spec);

            pr.setListener(new IPaymentRequestListener() {
                @Override
                public void stateChanged(PaymentRequest request, int previousState, int newState) {
                    System.out.println("stateChanged | previousState: " + previousState + " newState: " + newState);
                    System.out.println(request);
                    if (newState == PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN)
                        System.out.println("---------------\nTRANSACTION COMPLETED\n---------------");
                }

                @Override
                public void numberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations,
                                                         IPaymentRequestListener.Direction direction) {
                    System.out.println("numberOfConfirmationsChanged | numberOfConfirmations: "
                        + numberOfConfirmations + " direction: " + direction);
                    System.out.println(request);
                }

                @Override
                public void refundSent(PaymentRequest request, String toAddress, String cryptoCurrency, BigDecimal amount) {
                    // Not supported
                    System.err.println("refundSent");
                }
            });

            System.out.println("Waiting for transfer...");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
