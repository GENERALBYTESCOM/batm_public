package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

/**
 * Created by b00lean on 3/3/15.
 */
public interface IRateSourceAdvanced extends IRateSource {
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency);
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency);

    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount);
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount);
}
