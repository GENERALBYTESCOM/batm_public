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
 * Author   :  pawel.nowacki@teleit.pl / +48.600100825 - wanda.exchange
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.satangpro;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bitkub.BitKubRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.satangpro.dto.SatangProRateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SatangProRateSource implements IRateSourceAdvanced {

    private static final Logger log = LoggerFactory.getLogger(BitKubRateSource.class);

    private final SatangPro api;
    private final String preferredFiatCurrency;

    private static final String SATANG_PRO_BASE_URL = "http://api.tdax.com";

    private SatangProRateInfo satangProRateInfo;

    public SatangProRateSource(String preferredFiatCurrency) {
        this.api = RestProxyFactory.createProxy(SatangPro.class, SATANG_PRO_BASE_URL);
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    private String makeProductCode(String cryptoCurrency, String fiatCurrency) {
        log.debug("{}_{} pair has been calculated", cryptoCurrency, fiatCurrency);
        return  cryptoCurrency.toUpperCase() + "_" + fiatCurrency.toUpperCase();
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.BCH.getCode());
        result.add(CryptoCurrency.DOGE.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.USDT.getCode());
        result.add(CryptoCurrency.XRP.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(FiatCurrency.THB.getCode());
        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return this.preferredFiatCurrency;
    }

    private boolean isExchangeRateExist(String cryptoCurrency, String fiatCurrency)
    {
        return !getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency);
    }

    private void setTicker(String cryptoCurrency, String fiatCurrency)
    {
        try {
            this.satangProRateInfo = api.getTicker().get(this.makeProductCode(cryptoCurrency, fiatCurrency));
        } catch (HttpStatusIOException e) {
            log.warn(e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("", e);
        }
    }


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (isExchangeRateExist(cryptoCurrency, fiatCurrency)) {
            log.warn("{}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }
        try {
            log.info("{}-{} pair SET getExchangeRateLast", cryptoCurrency, fiatCurrency);

            setTicker(cryptoCurrency, fiatCurrency);
            BigDecimal ret = satangProRateInfo.getBid().getPrice().add(satangProRateInfo.getAsk().getPrice())
                .divide(new BigDecimal(2), BigDecimal.ROUND_UNNECESSARY);
            log.info("{} = getBid, {} = getASK, {} = getRate(AVG)",
                satangProRateInfo.getBid().getPrice(),
                satangProRateInfo.getAsk().getPrice(), ret);

            return ret;

        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        if (isExchangeRateExist(cryptoCurrency, fiatCurrency)) {
            log.warn("{}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }
        try {
            log.info("{}-{} pair SET getExchangeRateLast", cryptoCurrency, fiatCurrency);
            setTicker(cryptoCurrency, fiatCurrency);
            return satangProRateInfo.getBid().getPrice();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency)) {
            log.warn("{}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }
        try {
            log.info("{}-{} pair SET getExchangeRateLast", cryptoCurrency, fiatCurrency);
            setTicker(cryptoCurrency, fiatCurrency);
            return satangProRateInfo.getAsk().getPrice();
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }


    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal rate = getExchangeRateForBuy(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            return rate.multiply(cryptoAmount);
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        final BigDecimal rate = getExchangeRateForSell(cryptoCurrency, fiatCurrency);
        if (rate != null) {
            return rate.multiply(cryptoAmount);
        }
        return null;
    }

}
