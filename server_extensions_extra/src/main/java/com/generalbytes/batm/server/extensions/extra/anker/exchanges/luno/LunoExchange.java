package com.generalbytes.batm.server.extensions.extra.anker.exchanges.luno;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.ClientConfigUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LunoExchange implements IExchange {

    private String preferredFiatCurrency = Currencies.ZAR;
    private String clientKey;
    private String clientSecret;
    private String typeorder;
    private LunoExchangeAPI api;
    private final Logger log;

    public LunoExchange(String clientKey, String clientSecret, String preferredFiatCurrency, String typeorder) {
        this.preferredFiatCurrency = Currencies.ZAR;
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
        this.typeorder = typeorder;
        log = LoggerFactory.getLogger("batm.master.exchange.luno");
        final ClientConfig config = new ClientConfig();
        ClientConfigUtil.addBasicAuthCredentials(config, clientKey, clientSecret);
        api = RestProxyFactory.createProxy(LunoExchangeAPI.class, "https://api.mybitx.com", config);
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
        final BigDecimal cryptoballance = balance.getBalance("XBT");
        log.debug("{} exbalance = {}", cryptoCurrency, cryptoballance);
        return cryptoballance;
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
        final BigDecimal fiatballance = balance.getBalance("ZAR");
        log.debug("{} exbalance = {}", fiatCurrency, fiatballance);
        return fiatballance;
    }
    
    
    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        String type = "BUY";
        String pair = "XBTZAR";
        if (this.typeorder == "limit") {
            final LunoTickerData btcZar = api.getTicker("XBTZAR");
            BigDecimal pricebid  = btcZar.getBid();
            BigDecimal one       = new BigDecimal(1);
            BigDecimal price     = pricebid.add(one).setScale(0, BigDecimal.ROUND_CEILING);
            BigDecimal amountbtc = amount.divide(price, 4, BigDecimal.ROUND_CEILING);
            final LunoOrderData result = api.createLimitBuyOrder(pair, "BID", amountbtc, price);
            return result.getResult();
        } else {
            final LunoOrderData result = api.createBuyOrder(pair, type, amount);
            return result.getResult();
        }
    }
    
    
    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        String type = "SELL";
        String pair = "XBTZAR";
        if (this.typeorder == "limit") {
            final LunoTickerData btcZar = api.getTicker("XBTZAR");
            BigDecimal priceask  = btcZar.getAsk();
            BigDecimal one       = new BigDecimal(1);
            BigDecimal price     = priceask.subtract(one).setScale(0, BigDecimal.ROUND_CEILING);
            final LunoOrderData result = api.createLimitSellOrder(pair, "ASK", cryptoAmount, price);
            return result.getResult();
        } else {
            final LunoOrderData result = api.createSellOrder(pair, type, cryptoAmount);
            return result.getResult();
        }
    }
    
    
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        final LunoRequestData result = api.getAddress(destinationAddress, amount, cryptoCurrency, description);
        return result.getResult();
    }
    

}