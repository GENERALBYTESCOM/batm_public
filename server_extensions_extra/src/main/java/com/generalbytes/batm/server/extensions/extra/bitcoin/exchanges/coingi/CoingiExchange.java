package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coingi;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class CoingiExchange extends XChangeExchange {

    public CoingiExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public CoingiExchange(String key, String privateKey, String preferredFiatCurrency) {
        super(getSpecification(key, privateKey), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.coingi.CoingiExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String key, String secret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setApiKey(key);
        spec.setSecretKey(secret);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.DOGE.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.CZK.getCode());
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

    @Override
    public Wallet getWallet(AccountInfo accountInfo, String currency) {
        return accountInfo.getWallet();
    }

    @Override
    protected BigDecimal getRateSourceCryptoVolume(String cryptoCurrency) {
        return BigDecimal.ONE;
    }

    public static void main(String[] args) {
//        CoingiExchange rs = new CoingiExchange("EUR");
//        System.out.println(rs.getExchangeRateLast("BTC", "USD"));
//        System.out.println(rs.getExchangeRateLast("LTC", "EUR"));
//        System.out.println(rs.getExchangeRateLast("DOGE", "USD"));
//        System.out.println(rs.getExchangeRateLast("BTC", "CZK"));
//        System.out.println(rs.calculateBuyPrice("BTC", "USD", new BigDecimal("1")));
//        System.out.println(rs.calculateSellPrice("BTC", "USD", new BigDecimal("1")));

//        CoingiExchange xch = new CoingiExchange("", "", "EUR");
//        CoingiExchange xch = new CoingiExchange("", "", "EUR");
//        System.out.println(xch.getCryptoBalance("BTC"));
//        System.out.println(xch.getCryptoBalance("LTC"));
//        System.out.println(xch.getFiatBalance("USD"));
//        System.out.println(xch.getFiatBalance("EUR"));
//        System.out.println(xch.getFiatBalance("CZK"));

//        System.out.println(xch.getDepositAddress("LTC"));
//        System.out.println(xch.getDepositAddress("DOGE"));
//        System.out.println(xch.getDepositAddress("BTC"));

//        System.out.println(xch.sellCoins(new BigDecimal("1"), "LTC", "EUR", ""));
//        System.out.println(xch.purchaseCoins(new BigDecimal("1"), "LTC", "EUR", ""));
//        System.out.println(xch.sendCoins("", new BigDecimal("0.01"), "LTC", ""));
    }
}