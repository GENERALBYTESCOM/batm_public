package com.generalbytes.batm.server.extensions.extra.viacoin.sources;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
* Classes implementing this interface are used by the server to obtain price of specified cryptocurrency in fiat currency
*/
public class FixPriceRateSource implements IRateSource {
    private BigDecimal rate = BigDecimal.ZERO;

    private String preferedFiatCurrency = ICurrencies.USD;

    public FixPriceRateSource(BigDecimal rate, String preferedFiatCurrency)
    {
        this.rate = rate;
        if(ICurrencies.EUR.equalsIgnoreCase(preferedFiatCurrency)){
            this.preferedFiatCurrency = ICurrencies.EUR;
        }
        if(ICurrencies.USD.equalsIgnoreCase(preferedFiatCurrency)){
            this.preferedFiatCurrency = ICurrencies.USD;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.VIA);
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency){
        if(ICurrencies.VIA.equalsIgnoreCase(cryptoCurrency)){
            return rate;
        }
        return null;
    }

    @Override
    public Set<String> getFiatCurrencies(){
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.USD);
        result.add(ICurrencies.EUR);
        return result;
    }

    @Override
    public String getPreferredFiatCurrency(){
        return preferedFiatCurrency;
    }
}