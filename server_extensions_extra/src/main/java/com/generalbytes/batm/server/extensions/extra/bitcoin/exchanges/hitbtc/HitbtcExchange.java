package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.hitbtc;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.math.BigDecimal;
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
        cryptoCurrencies.add(Currencies.BCH);
        cryptoCurrencies.add(Currencies.DOGE);
        cryptoCurrencies.add(Currencies.XMR);
        cryptoCurrencies.add(Currencies.DASH);
        cryptoCurrencies.add(Currencies.NXT);
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

    @Override
    public Wallet getWallet(AccountInfo accountInfo, String fiatCurrency) {
        return accountInfo.getWallet(null); // HitBTC creates only one wallet with key=null
    }

//    public static void main(String[] args) {
//        HitbtcExchange xch = new HitbtcExchange("", "", "USD");
//        log.info(xch.getDepositAddress("XMR"));
//        log.info(xch.getExchangeRateForSell("XMR", "USD"));
//        //log.info(xch.sellCoins(BigDecimal.TEN, "XMR", "USD", ""));
//        log.info(xch.sendCoins("86didNu7QQdJvm1CAxpUCy9rJr7AcRLdz1xzSMEFio8DVknAu3PoLkY7VNoDBFdM2ZZ4kzfKyrHEUHrjRauXwSZGJ7SA7Ki", BigDecimal.TEN, "XMR", ""));
//    }
}