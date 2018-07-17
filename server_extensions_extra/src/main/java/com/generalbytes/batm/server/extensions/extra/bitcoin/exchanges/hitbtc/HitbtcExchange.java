package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.hitbtc;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.util.HashSet;
import java.util.Set;

public class HitbtcExchange extends XChangeExchange {

    private String apiKey;
    private String apiSecret;

    public HitbtcExchange(String apiKey, String apiSecret) {
        ExchangeSpecification hitBtc = new org.knowm.xchange.hitbtc.v2.HitbtcExchange().getDefaultExchangeSpecification();
        hitBtc.setApiKey(apiKey);
        hitBtc.setSecretKey(apiSecret);
    }


    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(Currencies.BTC);
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

  //  @Override
  //  protected String translateCryptoCurrencySymbolToExchangeSpecificSymbol(String from) {
   //     if (Currencies.BTC.equalsIgnoreCase(from)) {
   //         return "XBT";
  //      }
   //     return from;
   // }
}

