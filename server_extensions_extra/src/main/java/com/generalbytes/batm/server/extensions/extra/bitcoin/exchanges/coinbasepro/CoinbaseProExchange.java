package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbasepro;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.util.HashSet;
import java.util.Set;

public class CoinbaseProExchange extends XChangeExchange {

    public CoinbaseProExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public CoinbaseProExchange(String key, String secret, String passphrase, String preferredFiatCurrency, boolean sandbox) {
        super(getSpecification(key, secret, passphrase, sandbox), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.coinbasepro.CoinbaseProExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String key, String secret, String passphrase, boolean sandbox) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setApiKey(key);
        spec.setSecretKey(secret);
        spec.setExchangeSpecificParametersItem("passphrase", passphrase);
        spec.setExchangeSpecificParametersItem("Use_Sandbox", sandbox);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.EUR.getCode());
        fiatCurrencies.add(FiatCurrency.GBP.getCode());
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

//    public static void main(String[] args) {
//        CoinbaseProExchange rs = new CoinbaseProExchange("USD");
//        System.out.println(rs.getExchangeRateLast("BTC", "USD"));
//        System.out.println(rs.getExchangeRateForBuy("BTC", "USD"));
//        System.out.println(rs.getExchangeRateForSell("BTC", "USD"));
//        System.out.println(rs.calculateBuyPrice("BTC", "USD", new BigDecimal("100")));
//        System.out.println(rs.calculateSellPrice("BTC", "USD", new BigDecimal("100")));
//
//        CoinbaseProExchange xch = new CoinbaseProExchange("xx", "xx/xxxx==", "x", "EUR", true);
//        System.out.println(xch.getCryptoBalance("BTC"));
//        System.out.println(xch.getCryptoBalance("LTC"));
//        System.out.println(xch.getFiatBalance("USD"));
//        System.out.println(xch.getFiatBalance("EUR"));
//
//        System.out.println(xch.getDepositAddress("BTC"));
//        System.out.println(xch.getDepositAddress("ETH"));
//
//        System.out.println(xch.purchaseCoins(new BigDecimal("0.01"), "BTC", "USD", ""));
//        System.out.println(xch.sellCoins(new BigDecimal("0.01"), "BTC", "USD", ""));
//
//        System.out.println(xch.sendCoins("mswUGcPHp1YnkLCgF1TtoryqSc5E9Q8xFa", BigDecimal.ONE, "BTC", ""));
//    }
}