package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.enigma;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class EnigmaExchange extends XChangeExchange {

    public EnigmaExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public EnigmaExchange(String username, String password, String preferredFiatCurrency) {
        super(getSpecification(username, password), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.enigma.EnigmaExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String username, String password) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setUserName(username);
        spec.setPassword(password);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.GBP.getCode());
        fiatCurrencies.add(FiatCurrency.EUR.getCode());
        fiatCurrencies.add(FiatCurrency.USD.getCode());
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

    public static void main(String[] args) {
        EnigmaExchange rs = new EnigmaExchange("EUR");
        System.out.println(rs.getExchangeRateLast("BTC", "USD"));

        // EnigmaExchange xch = new EnigmaExchange("", "", "USD");
        // System.out.println("getExchangeRateForSell" + xch.getExchangeRateLast("BTC", "USD"));
        // System.out.println("calculateSellPrice: " + xch.calculateSellPrice("BTC", "USD", new BigDecimal(1)).toString());
        // System.out.println("getDepositAddress: " + xch.getDepositAddress("BTC"));
    }
}