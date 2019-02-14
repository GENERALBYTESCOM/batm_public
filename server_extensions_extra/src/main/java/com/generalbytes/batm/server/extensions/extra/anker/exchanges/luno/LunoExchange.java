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
    private LunoExchangeAPI api;

    public LunoExchange(String clientKey, String clientSecret, String preferredFiatCurrency) {
        this.preferredFiatCurrency = Currencies.ZAR;
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
        final ClientConfig config = new ClientConfig();
        ClientConfigUtil.addBasicAuthCredentials(config, clientKey, clientSecret);
        api = RestProxyFactory.createProxy(LunoExchangeAPI.class, "https://api.mybitx.com", config)
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
        final LunoBalanceData balance = api.getBalance();
        return balance.getBalance(cryptoCurrency);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        final LunoAddressData address = api.getAddress(cryptoCurrency);
        return address.getAddress();
    }

    @Override
    public String getPreferredFiatCurrency() {
        return this.preferredFiatCurrency;
    }
    
    
    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        final LunoBalanceData balance = api.getBalance();
        return balance.getBalance(fiatCurrency);
    }
    
    
    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        String type = "BUY";
        String pair = "XBTZAR";
        final LunoOrderData result = api.createBuyOrder(pair, type, amount);
        return result.getResult();
    }
    
    
    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        String type = "SELL";
        String pair = "XBTZAR";
        final LunoOrderData result = api.createSellOrder(pair, type, cryptoAmount);
        return result.getResult();
    }
    
    
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        final LunoRequestData result = api.getAddress(destinationAddress, amount, cryptoCurrency, description);
        return result.getResult();
    }
    

}