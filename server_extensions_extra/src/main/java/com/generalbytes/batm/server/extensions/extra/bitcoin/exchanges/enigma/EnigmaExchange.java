package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.enigma;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.enigma.service.EnigmaAccountService;

import java.io.IOException;
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
        org.knowm.xchange.enigma.EnigmaExchange enigmaExchange = new org.knowm.xchange.enigma.EnigmaExchange();
        ExchangeSpecification exchangeSpec = enigmaExchange.getDefaultExchangeSpecification();
        exchangeSpec.setExchangeSpecificParametersItem("infra", "prod");
        exchangeSpec.setUserName(username);
        exchangeSpec.setPassword(password);
        enigmaExchange.applySpecification(exchangeSpec);
        try {
            ((EnigmaAccountService) enigmaExchange.getAccountService()).login();
        } catch (IOException e) {
            throw new RuntimeException("Login exception", e);
        }
        return exchangeSpec;
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
        EnigmaExchange xch = new EnigmaExchange("", "", "EUR");
        // System.out.println(xch.getExchangeRateLast("BTC", "USD"));

        System.out.println("getExchangeRateForSell" + xch.getExchangeRateForSell("BTC", "USD"));
        // System.out.println("getExchangeRateLast" + xch.getExchangeRateLast("BTC",
        // "USD"));
        // System.out.println("getDepositAddress: " + xch.getDepositAddress("BTC"));
        // System.out.println("calculateSellPrice: " + xch.calculateSellPrice("BTC",
        // "USD", new BigDecimal(1)).toString());
    }
}