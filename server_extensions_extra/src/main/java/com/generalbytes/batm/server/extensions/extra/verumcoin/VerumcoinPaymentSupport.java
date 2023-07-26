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
package com.generalbytes.batm.server.extensions.extra.verumcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.extra.common.AbstractRPCPaymentSupport;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerumcoinPaymentSupport extends AbstractRPCPaymentSupport {
    private static final Logger log = LoggerFactory.getLogger(VerumcoinPaymentSupport.class);

    private VerumcoinAddressValidator addressValidator = new VerumcoinAddressValidator();

    private static final long MAXIMUM_WAIT_FOR_POSSIBLE_REFUND_MILLIS = TimeUnit.DAYS.toMillis(3); // 3 days
    private static final long MAXIMUM_WATCHING_TIME_MILLIS = TimeUnit.DAYS.toMillis(3); // 3 days (exactly plus Sell Offer Expiration 5-120 minutes)

    @Override
    public String getCurrency() {
        return CryptoCurrency.VERUM.getCode();
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
            BigDecimal estimate = new BigDecimal(client.getEstimateFee());
            if (BigDecimal.ZERO.compareTo(estimate) == 0 || estimate.compareTo(new BigDecimal("-1")) == 0 ) {
                //bitcoind is clueless
                return getMinimumNetworkFee(client);
            }
            return estimate.divide(new BigDecimal("1000"), RoundingMode.UP).multiply(new BigDecimal(transactionSize));
        } catch (Exception e) {
            log.error("CUSTOM VERUM LOG", e);
            return getMinimumNetworkFee(client);
        }
    }

    @Override
    public String getSigHashType() {
        return "ALL";
    }

}
