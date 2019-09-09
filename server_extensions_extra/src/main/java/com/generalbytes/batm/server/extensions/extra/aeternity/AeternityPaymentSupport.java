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
package com.generalbytes.batm.server.extensions.extra.aeternity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.extra.aeternity.rpc.AbstractAternityRPCPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.aeternity.rpc.AeternityRPCClient;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;

import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;

public class AeternityPaymentSupport extends AbstractAternityRPCPaymentSupport {
    private AeternityAddressValidator addressValidator = new AeternityAddressValidator();

    private static final long MAXIMUM_WAIT_FOR_POSSIBLE_REFUND_MILLIS = TimeUnit.DAYS.toMillis(3); // 3 days
    private static final long MAXIMUM_WATCHING_TIME_MILLIS = TimeUnit.DAYS.toMillis(3); // 3 days (exactly plus Sell Offer Expiration 5-120 minutes)
    private static final BigDecimal TOLERANCE = new BigDecimal("0.0002"); // Received amount should be  cryptoTotalToSend +- tolerance

    @Override
    public String getCurrency() {
        return CryptoCurrency.AE.getCode();
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
        return TOLERANCE;
    }

    @Override
    public ICryptoAddressValidator getAddressValidator() {
        return addressValidator;
    }

    
  
    public BigDecimal calculateTxFee(String senderAddress, String destinationAddress, BigDecimal amount,  RPCClient client) {
    	return ((AeternityRPCClient)client).calculateTxFee(senderAddress, destinationAddress, amount, client);
    }
//    public static void main(String[] args) {
//        //You need to have node running: i.e.:  bitcoind -rpcuser=rpcuser -rpcpassword=rpcpassword -rpcport=8332
//
//        BitcoinCashRPCWallet wallet = new BitcoinCashRPCWallet("http://rpcuser:rpcpassword@localhost:8332", "");
//        BitcoinCashPaymentSupport ps = new BitcoinCashPaymentSupport();
//        ps.init(null);
//        PRS spec = new PRS(
//            ps.getCurrency(),
//            "Just a test",
//            60 * 15, //15 min
//            3,
//            false,
//            false,
//            new BigDecimal("7"),
//            new BigDecimal("10"),
//            wallet
//        );
//        spec.addOutput("qpqkqq2uy6v044yjqsec0cprunecwcf9dqtc5ler86", new BigDecimal("0.0017"));
//
//        PaymentRequest pr = ps.createPaymentRequest(spec);
//        System.out.println(pr);
//        pr.setListener(new IPaymentRequestListener() {
//            @Override
//            public void stateChanged(PaymentRequest request, int previousState, int newState) {
//                System.out.println("stateChanged = " + request + " previousState: " + previousState + " newState: " + newState);
//            }
//
//            @Override
//            public void numberOfConfirmationsChanged(PaymentRequest request, int numberOfConfirmations, Direction direction) {
//                System.out.println("numberOfConfirmationsChanged = " + request + " numberOfConfirmations: " + numberOfConfirmations + " direction: " + direction);
//            }
//
//            @Override
//            public void refundSent(PaymentRequest request, String toAddress, String cryptoCurrency, BigDecimal amount) {
//                System.out.println("refundSent = " + request + " toAddress: " + toAddress +" cryptoCurrency: " + cryptoCurrency + " " + amount);
//            }
//        });
//        System.out.println("Waiting for transfer");
//        try {
//            Thread.sleep(20 * 60 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public String getSigHashType() {//This is BitcoinCash specific. No need to override this method for other currencies
        return "ALL|FORKID";
    }

}
