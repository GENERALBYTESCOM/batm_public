/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.burstcoin.sources.poloniex;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitfinex.BitfinexExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PoloniexRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(PoloniexRateSource.class);

    private final BitfinexExchange btcRs;
    private final IPoloniexAPI api;

    private static final HashMap<String,BigDecimal> rateAmounts = new HashMap<>();
    private static final HashMap<String,Long> rateTimes = new HashMap<>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    public PoloniexRateSource() {
        btcRs = new BitfinexExchange("***","***",FiatCurrency.USD.getCode());
        api = RestProxyFactory.createProxy(IPoloniexAPI.class, "https://poloniex.com");
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return btcRs.getFiatCurrencies();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return btcRs.getPreferredFiatCurrency();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.NXT.getCode());
        return result;
    }


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!CryptoCurrency.NXT.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return null;
        }
        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called bitcoinaverage exchange for rate: " + key + " = " + result);
                rateAmounts.put(key,result);
                rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                return result;
            } else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                } else {
                    BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                    log.debug("Called bitcoinaverage exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }
    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!CryptoCurrency.NXT.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return null; // unsupported currency
        }
        OrderBookResponse orderBookResponse = api.returnOrderBook("returnOrderBook", "BTC_BURST", 10000);
        if (orderBookResponse != null) {
            BigDecimal[][] asks = orderBookResponse.getAsks();
            BigDecimal asksTotal = BigDecimal.ZERO;
            BigDecimal targetAmount = new BigDecimal(100000); //calculate price based on this amount of BURST
            BigDecimal tradableLimit = BigDecimal.ZERO;

            for (BigDecimal[] ask : asks) {
                asksTotal = asksTotal.add(ask[1]);
                if (targetAmount.compareTo(asksTotal) <= 0) {
                    tradableLimit = ask[0];
                    break;
                }
            }

            if (tradableLimit != null) {
                BigDecimal btcRate = btcRs.getExchangeRateLast(CryptoCurrency.BTC.getCode(), fiatCurrency);
                if (btcRate != null) {
                    return btcRate.multiply(tradableLimit);
                }
            }
            return null;
        }

        return null;
    }

    public static void main(String[] args) {
        PoloniexRateSource rs = new PoloniexRateSource();
        log.info("rs = " + rs.getExchangeRateLast(CryptoCurrency.NXT.getCode(),FiatCurrency.USD.getCode()));
    }
}
