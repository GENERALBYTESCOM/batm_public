package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.hitbtc;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.util.HashSet;
import java.util.Set;

public class HitbtcExchange extends XChangeExchange {

    public HitbtcExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public HitbtcExchange(String clientKey, String clientSecret, String preferredFiatCurrency) {
        super(getSpecification(clientKey, clientSecret), preferredFiatCurrency);
    }


    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.hitbtc.v2.HitbtcExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String clientKey, String clientSecret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setApiKey(clientKey);
        spec.setSecretKey(clientSecret);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(Currencies.BTC);
        cryptoCurrencies.add(Currencies.ETH);
        cryptoCurrencies.add(Currencies.LTC);
        cryptoCurrencies.add(Currencies.SMART);
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(Currencies.USD);
        return fiatCurrencies;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return true;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

}