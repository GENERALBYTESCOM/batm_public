package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain;


import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import com.generalbytes.batm.server.extensions.Currencies;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


public class DVChainExchange extends XChangeExchange {

    public DVChainExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }
    public DVChainExchange(String clientSecret, String preferredFiatCurrency) {
        super(getSpecification(clientSecret), preferredFiatCurrency);
    }

    private static ExchangeSpecification getSpecification(String clientSecret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setSecretKey(clientSecret);
        return spec;
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new  com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.v2.DVChainExchange().getDefaultExchangeSpecification();
    }


    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    protected boolean isWithdrawSuccessful(String string) {
        return true;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(Currencies.USD);
        return fiatCurrencies;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(Currencies.BTC);
        cryptoCurrencies.add(Currencies.BCH);
        cryptoCurrencies.add(Currencies.LTC);
        cryptoCurrencies.add(Currencies.ETH);
        cryptoCurrencies.add(Currencies.XMR);
        return cryptoCurrencies;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) throws NotYetImplementedForExchangeException {
        throw new NotYetImplementedForExchangeException();
    }
}
