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
}
