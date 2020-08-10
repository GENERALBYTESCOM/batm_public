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
package com.generalbytes.batm.server.extensions.extra.digibyte.sources.livecoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import si.mazi.rescu.RestProxyFactory;

public class LiveCoinRateSource implements IRateSource {

  private ILiveCoinAPI api;

  private String preferredFiatCurrency = FiatCurrency.USD.getCode();

  public LiveCoinRateSource(String preferedFiatCurrency) {
    if (FiatCurrency.EUR.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
      this.preferredFiatCurrency = FiatCurrency.EUR.getCode();
    }
    if (FiatCurrency.USD.getCode().equalsIgnoreCase(preferedFiatCurrency)) {
      this.preferredFiatCurrency = FiatCurrency.USD.getCode();
    }
    api = RestProxyFactory.createProxy(ILiveCoinAPI.class, "https://api.livecoin.net");
  }


  @Override
  public Set<String> getCryptoCurrencies() {
    Set<String> result = new HashSet<String>();
    result.add(CryptoCurrency.DGB.getCode());
    return result;
  }

  @Override
  public Set<String> getFiatCurrencies() {
    Set<String> result = new HashSet<String>();
    result.add(FiatCurrency.USD.getCode());
    result.add(FiatCurrency.EUR.getCode());
    return result;
  }

  @Override
  public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
    if (!getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency)) {
      return null;
    }
    //Grab the last dgb rate in btc
    LiveCoinTicker dgbBtc = api.getTicker(CryptoCurrency.DGB.getCode() + "/" + CryptoCurrency.BTC.getCode());

    //Grab the last btc rate in the selected fiat currency
    LiveCoinTicker btcFiat = api.getTicker(CryptoCurrency.BTC.getCode() + "/" + fiatCurrency);

    BigDecimal lastDgbPriceInBtc = dgbBtc.getLast();
    BigDecimal lastBtcPriceInFiat = btcFiat.getLast();

    //Multiply the last dgb/btc rate by the last btc/fiat rate to get the last dgb/fiat rate
    BigDecimal lastDgbPriceInFiat = lastDgbPriceInBtc.multiply(lastBtcPriceInFiat);

    return lastDgbPriceInFiat;
  }

  @Override
  public String getPreferredFiatCurrency() {
    return preferredFiatCurrency;
  }
}