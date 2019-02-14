package com.generalbytes.batm.server.extensions.extra.anker.exchanges.luno;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import si.mazi.rescu.RestProxyFactory;


public class LunoExchange extends AbstractExtension implements IExchange {

    private String preferredFiatCurrency = Currencies.ZAR;
    private String clientKey;
    private String clientSecret;

    public LunoExchange(String clientKey, String clientSecret, String preferredFiatCurrency) {
        this.preferredFiatCurrency = Currencies.ZAR;
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
    }


    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(Currencies.BTC);
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(Currencies.ZAR);
        return fiatCurrencies;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return true;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 1;
    }


}