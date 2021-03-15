package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.TestExtensionContext;
import com.generalbytes.batm.server.extensions.extra.dash.test.PRS;
import com.generalbytes.batm.server.extensions.extra.nano.NanoCurrencyUtil;
import com.generalbytes.batm.server.extensions.extra.nano.NanoExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.NanoPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoWsClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.node.NanoNodeWallet;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestListener;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;

/**
 * THIS CLASS MAY BE IGNORED.
 * It's only purpose is to test and help during development.
 */
public class TestPaymentSupport {

    public static void main(String[] args) throws Exception {
        try {
            /*
             * You need to have node running: i.e.: nano_node --daemon with rpc enabled
             * WebSocket host may be null if using RPC polling only.
             * A value totalling paymentValue should be sent to the account printed in console.
             */
            String rpcHost = "http://[::1]:7076", wsHost = "ws://[::1]:7078";
            String walletId = "C6DFB1E6B2AAA97247BA5A434BB2795F5FC4D68EE0FBEDCD21C72027880596C7";
            String walletAccount = "nano_3h5r5huudbj3mrmosha84oregs3k9wgi8cwbynbiajjmto1y9sys8yykjg1m";
            BigDecimal paymentValue = new BigDecimal("0.01");


            System.out.println("Running TestPaymentSupport");
            NanoRpcClient rpcClient = new NanoRpcClient(new URL(rpcHost));
            NanoWsClient wsClient = wsHost == null ? null : new NanoWsClient(URI.create(wsHost));
            NanoExtensionContext context = new NanoExtensionContext(
                    CryptoCurrency.NANO, new TestExtensionContext(), NanoCurrencyUtil.NANO);
            NanoNodeWallet wallet = new NanoNodeWallet(context, rpcClient, wsClient, walletId, walletAccount);
            NanoPaymentSupport ps = new NanoPaymentSupport(context);
            ps.init(null);
            String paymentAccount = wallet.generateNewDepositCryptoAddress(context.getCurrencyCode(), "label");

            Thread.sleep(2500);

            PRS spec = new PRS(context.getCurrencyCode(), "Test txn", 60 * 15, 1, false, true, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, wallet);
            spec.addOutput(paymentAccount, paymentValue);
            PaymentRequest pr = ps.createPaymentRequest(spec);

            pr.setListener(new IPaymentRequestListener() {
                @Override
                public void stateChanged(PaymentRequest request, int previousState, int newState) {
                    System.out.printf("stateChanged | received: %.6f / %.6f, previousState: %d, newState: %d%n",
                        request.getTxValue(), request.getAmount(), previousState, newState);
                    if (newState == PaymentRequest.STATE_REMOVED) {
                        System.out.println("--------------------------------------");
                        System.out.printf("TRANSACTION COMPLETED | Received %.6f%n", request.getTxValue());
                        System.out.println("--------------------------------------");
                    }
                }

                @Override
                public void numberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations,
                                                         IPaymentRequestListener.Direction direction) {
                    System.out.println("numberOfConfirmationsChanged | numberOfConfirmations: "
                        + numberOfConfirmations + " | direction: " + direction);
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
