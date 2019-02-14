package com.generalbytes.batm.server.extensions.extra.anker.exchanges.luno;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import si.mazi.rescu.RestProxyFactory;


public class LunoExchange implements IExchange {

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
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return 1.0;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return "";
    }

    @Override
    public String getPreferredFiatCurrency() {
        return "";
    }
    
    
    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        return 1.0;
    }
    
    
    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return "";
    }
    
    
    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return "";
    }
    
    
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return "";
    }
    

}