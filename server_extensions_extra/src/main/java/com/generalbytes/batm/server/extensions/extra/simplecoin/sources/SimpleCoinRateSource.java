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
package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.util.net.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

public class SimpleCoinRateSource implements IRateSourceAdvanced {
    private static final Logger log = LoggerFactory.getLogger(SimpleCoinRateSource.class);
    private String preferedFiatCurrency;
    private ISimpleCoinApi api;
    private static HashMap<String, BigDecimal> rateAmounts = new HashMap<>();
    private static HashMap<String, Long> rateTimes = new HashMap<>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec
    private SupportedCurrencies supportedCurrencies;

    public SimpleCoinRateSource(SupportedCurrencies currencies) {
        this.supportedCurrencies = currencies;
        api = RestProxyFactory.createProxy(ISimpleCoinApi.class, "https://server.simplecoin.eu");
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        return calculateBuyPrice(cryptoCurrency, fiatCurrency, BigDecimal.ONE);
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            RateLimiter.waitForPossibleCall(getClass());
            FiatCryptoResponse ticker = api.returnRate(fiatCurrency, cryptoCurrency); // sequence for buying crypto (e.g. from=CZK to=BTC)
            BigDecimal price = ticker.getResponse().getRate();// for buying attribute rate
            log.warn("Called buy rate: {}{} = {}", cryptoCurrency, fiatCurrency, price);
            return price;

        } catch (Throwable e) {
            log.warn("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        return calculateSellPrice(cryptoCurrency, fiatCurrency, BigDecimal.ONE);
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            RateLimiter.waitForPossibleCall(getClass());
            FiatCryptoResponse ticker = api.returnRate(cryptoCurrency, fiatCurrency); // sequence for selling crypto (e.g. from=BTC to=CZK)
            BigDecimal price = ticker.getResponse().getRate_inverse(); // for selling rate_inverse
            log.warn("Called sell rate: {}{} = {}", cryptoCurrency, fiatCurrency, price);
            return price;

        } catch (Throwable e) {
            log.warn("Error", e);
        }
        return null;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return supportedCurrencies.getSupportedFiatCurrency();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return supportedCurrencies.getPreferredFiatCurrency();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return supportedCurrencies.getSupportedCryptoCurrency();
    }

    private boolean isCurrencySupported(String cryptoCurrency, String fiatCurrency) {
        if (supportedCurrencies.isCryptoSupported(cryptoCurrency)
            && supportedCurrencies.isFiatSupported(fiatCurrency)) {
            return true;
        }
        log.debug("Unsupported currency");
        return false;
    }

    @Override
    public synchronized BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!isCurrencySupported(cryptoCurrency, fiatCurrency)) {
            return null;
        }
        synchronized (rateAmounts) {
            long now = System.currentTimeMillis();
            String key = cryptoCurrency + "_" + fiatCurrency;
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                return prepareExchangeRate(cryptoCurrency, fiatCurrency, key, now);
            } else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                } else {
                    return prepareExchangeRate(cryptoCurrency, fiatCurrency, key, now);
                }
            }
        }
    }

    private BigDecimal prepareExchangeRate(String cryptoCurrency, String fiatCurrency, String key, long now) {
        BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
        log.warn("Called simplecoin.eu exchange for rate: {} = {}", key, result);
        rateAmounts.put(key, result);
        rateTimes.put(key, now + MAXIMUM_ALLOWED_TIME_OFFSET);
        return result;
    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        FiatCryptoResponse fiatCryptoResponse = api.returnRate(fiatCurrency, cryptoCurrency);
        if (fiatCryptoResponse != null && fiatCryptoResponse.getError() == null && "ok".equalsIgnoreCase(fiatCryptoResponse.getStatus())) {
            SimpleCoinResponse response = fiatCryptoResponse.getResponse();
            return response.getRate();
        } else {
            if (fiatCryptoResponse.getError() != null) {
                log.warn("API SimpleCoin error: " + fiatCryptoResponse.getError());
            }
        }
        return null;
    }
}