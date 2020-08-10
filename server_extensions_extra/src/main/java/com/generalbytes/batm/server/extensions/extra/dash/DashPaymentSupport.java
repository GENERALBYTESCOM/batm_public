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
package com.generalbytes.batm.server.extensions.extra.dash;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.extra.common.AbstractRPCPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;
import com.generalbytes.batm.server.extensions.payment.PaymentReceipt;
import com.generalbytes.batm.server.extensions.extra.dash.wallets.DashRPCClient;
import com.generalbytes.batm.server.extensions.extra.dash.wallets.DashTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashPaymentSupport extends AbstractRPCPaymentSupport {
    private static final Logger log = LoggerFactory.getLogger(DashPaymentSupport.class);

    private DashAddressValidator addressValidator = new DashAddressValidator();

    private static final long MAXIMUM_WAIT_FOR_POSSIBLE_REFUND_MILLIS = TimeUnit.DAYS.toMillis(3); // 3 days
    private static final long MAXIMUM_WATCHING_TIME_MILLIS = TimeUnit.DAYS.toMillis(3); // 3 days (exactly plus Sell Offer Expiration 5-120 minutes)
    private static final BigDecimal TOLERANCE = new BigDecimal("0.0002"); // Received amount should be  cryptoTotalToSend +- tolerance

    @Override
    public String getCurrency() {
        return CryptoCurrency.DASH.getCode();
    }

    @Override
    public long getMaximumWatchingTimeMillis() {
        return MAXIMUM_WATCHING_TIME_MILLIS;
    }

    @Override
    public long getMaximumWaitForPossibleRefundInMillis() {
        return MAXIMUM_WAIT_FOR_POSSIBLE_REFUND_MILLIS;
    }

    @Override
    public BigDecimal getTolerance() {
        return TOLERANCE;
    }

    @Override
    public BigDecimal getMinimumNetworkFee(RPCClient client) {
        return client.getNetworkInfo().relayFee();
    }

    @Override
    public ICryptoAddressValidator getAddressValidator() {
        return addressValidator;
    }

    @Override
    public int calculateTransactionSize(int numberOfInputs, int numberOfOutputs) {
        return (numberOfInputs * 149) + (numberOfOutputs * 34) + 10;
    }

    @Override
    public BigDecimal calculateTxFee(int numberOfInputs, int numberOfOutputs, RPCClient client) {
        final int transactionSize = calculateTransactionSize(numberOfInputs, numberOfOutputs);
        try {
            BigDecimal estimate = new BigDecimal(client.getEstimateFee(2));
            if (BigDecimal.ZERO.compareTo(estimate) == 0 || estimate.compareTo(new BigDecimal("-1")) == 0 ) {
                return getMinimumNetworkFee(client);
            }
            return estimate.divide(new BigDecimal("1000"), RoundingMode.UP).multiply(new BigDecimal(transactionSize));
        } catch (Exception e) {
            log.error("", e);
            return getMinimumNetworkFee(client);
        }
    }

    @Override
    public PaymentReceipt getPaymentReceipt(String paymentAddress) {
        PaymentReceipt result = new PaymentReceipt(getCurrency(), paymentAddress);
        for (PaymentRequest paymentRequest : requests.keySet()) {
            if (paymentRequest.getAddress().equals(paymentAddress)) {
                switch (paymentRequest.getState()) {
                    case PaymentRequest.STATE_SEEN_IN_BLOCK_CHAIN:
                        result.setStatus(PaymentReceipt.STATUS_PAID);
                        result.setConfidence(PaymentReceipt.CONFIDENCE_SURE);
                        break;
                    case PaymentRequest.STATE_SEEN_TRANSACTION:
                        result.setStatus(PaymentReceipt.STATUS_PAID);

                        //Is it instantsend?
                        DashRPCClient client = (DashRPCClient)getClient(paymentRequest.getWallet());
                        DashTransaction dashTx = (DashTransaction)client.getTransaction(paymentRequest.getIncomingTransactionHash());
                        if(dashTx.instantlock())
                            result.setConfidence(PaymentReceipt.CONFIDENCE_SURE);
                        else
                            result.setConfidence(PaymentReceipt.CONFIDENCE_NONE);
                        break;
                }
                result.setAmount(paymentRequest.getAmount());
                result.setTransactionId(paymentRequest.getIncomingTransactionHash());
            }
        }
        return result;
    }

//   public static void main(String[] args) {
//       //You need to have node running: i.e.:  dashd -rpcuser=rpcuser -rpcpassword=rpcpassword -rpcport=8332
//
//       try {
//           DashRPCWallet wallet = new DashRPCWallet("http://rpcuser:rpcpassword@localhost:9998", "");
//           DashPaymentSupport ps = new DashPaymentSupport();
//           ps.init(null);
//           PRS spec = new PRS(
//               ps.getCurrency(),
//               "Just a test",
//               60 * 15, //15 min
//               3,
//               false,
//               false,
//               new BigDecimal("7"),
//               new BigDecimal("10"),
//               wallet
//           );
//           spec.addOutput("Xfem7sYAMm8uwfrCxqH1ABgsFBDZCWJCXK", new BigDecimal("0.0002"));
//
//           PaymentRequest pr = ps.createPaymentRequest(spec);
//           System.out.println(pr);
//           pr.setListener(new IPaymentRequestListener() {
//               @Override
//               public void stateChanged(PaymentRequest request, int previousState, int newState) {
//                   System.out.println("stateChanged = " + request + " previousState: " + previousState + " newState: " + newState);
//               }
//
//               @Override
//               public void numberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations, Direction direction) {
//                   System.out.println("numberOfConfirmationsChanged = " + request + " numberOfConfirmations: " + numberOfConfirmations + " direction: " + direction);
//               }
//
//               @Override
//               public void refundSent(PaymentRequest request, String toAddress, String cryptoCurrency, BigDecimal amount) {
//                   System.out.println("refundSent = " + request + " toAddress: " + toAddress + " cryptoCurrency: " + cryptoCurrency + " " + amount);
//               }
//           });
//           System.out.println("Waiting for transfer");
//
//           Thread.sleep(20 * 60 * 1000);
//       } catch (MalformedURLException e) {
//           e.printStackTrace();
//       } catch (InterruptedException e) {
//           e.printStackTrace();
//       }
//   }

}
