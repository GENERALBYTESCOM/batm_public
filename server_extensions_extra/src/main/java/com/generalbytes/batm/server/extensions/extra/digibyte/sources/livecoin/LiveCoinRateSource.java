package com.generalbytes.batm.server.extensions.extra.digibyte.sources.livecoin;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import si.mazi.rescu.RestProxyFactory;

public class LiveCoinRateSource implements IRateSource {

  private ILiveCoinAPI api;

  private String preferredFiatCurrency = ICurrencies.USD;

  public LiveCoinRateSource(String preferedFiatCurrency) {
    if (ICurrencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
      this.preferredFiatCurrency = ICurrencies.EUR;
    }
    if (ICurrencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
      this.preferredFiatCurrency = ICurrencies.USD;
    }
    api = RestProxyFactory.createProxy(ILiveCoinAPI.class, "https://api.livecoin.net");
  }


  @Override
  public Set<String> getCryptoCurrencies() {
    Set<String> result = new HashSet<String>();
    result.add(ICurrencies.DGB);
    return result;
  }

  @Override
  public Set<String> getFiatCurrencies() {
    Set<String> result = new HashSet<String>();
    result.add(ICurrencies.USD);
    result.add(ICurrencies.EUR);
    return result;
  }

  @Override
  public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
    if (!getFiatCurrencies().contains(fiatCurrency) || !getCryptoCurrencies().contains(cryptoCurrency)) {
      return null;
    }
    //Grab the last dgb rate in btc
    LiveCoinTicker dgbBtc = api.getTicker(ICurrencies.DGB + "/" + ICurrencies.BTC);

    //Grab the last btc rate in the selected fiat currency
    LiveCoinTicker btcFiat = api.getTicker(ICurrencies.BTC + "/" + fiatCurrency);

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