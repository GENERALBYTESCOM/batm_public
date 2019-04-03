package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.luno;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
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

    private String preferredFiatCurrency = FiatCurrency.ZAR.getCode();
    private String clientKey;
    private String clientSecret;
    private String typeorder;
    private LunoExchangeAPI api;
    private final Logger log;

    public LunoExchange(String clientKey, String clientSecret, String preferredFiatCurrency, String typeorder) {
        this.preferredFiatCurrency = FiatCurrency.ZAR.getCode();
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
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.ZAR.getCode());
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
        if (cryptoCurrency.equals("BTC")) {
            final LunoAddressData address = api.getAddress("XBT");
            return address.getAddress();
        } else {
            final LunoAddressData address = api.getAddress(cryptoCurrency);
            return address.getAddress();
        }
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
        final LunoTickerData btcZar = api.getTicker("XBTZAR");
        BigDecimal pricebid  = btcZar.getBid();
        BigDecimal one       = new BigDecimal(1);
        BigDecimal onepr     = new BigDecimal(1.01);
        BigDecimal btcfee    = new BigDecimal(0.000052);
        amount               = amount.multiply(onepr);
        amount               = amount.add(btcfee).setScale(6, BigDecimal.ROUND_CEILING);
        BigDecimal price     = pricebid.add(one).setScale(0, BigDecimal.ROUND_CEILING);
        BigDecimal amountbtc = price.multiply(amount).setScale(2, BigDecimal.ROUND_CEILING);
        if (this.typeorder.equals("limit")) {
            log.debug("limit pair {} type {} amount {} price {}", pair, "BID", amount.toString(), price.toString());
            final LunoOrderData result = api.createLimitBuyOrder(pair, "BID", amount.toString(), price.toString());
            return result.getResult();
        } else {
            log.debug("market pair {} type {} amount   {}  ", pair, type, amountbtc.toString());
            final LunoOrderData result = api.createBuyOrder(pair, type, amountbtc.toString());
            return result.getResult();
        }
    }
    
    
    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        String type = "SELL";
        String pair = "XBTZAR";
        if (this.typeorder.equals("limit")) {
            final LunoTickerData btcZar = api.getTicker("XBTZAR");
            BigDecimal priceask  = btcZar.getAsk();
            BigDecimal one       = new BigDecimal(1);
            BigDecimal price     = priceask.subtract(one).setScale(0, BigDecimal.ROUND_CEILING);
            final LunoOrderData result = api.createLimitSellOrder(pair, "ASK", cryptoAmount.toString(), price.toString());
            log.debug("limit pair {} type {} amount {} price {} result {}", pair, "ASK", cryptoAmount.toString(), price.toString(), result.getResult());
            return result.getResult();
        } else {
            final LunoOrderData result = api.createSellOrder(pair, type, cryptoAmount.toString());
            log.debug("market pair {} type {} amount   {}   result {}", pair, type, cryptoAmount.toString(), result.getResult());
            return result.getResult();
        }
    }
    
    
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            Thread.sleep(2000); //give exchange 2 seconds to reflect open order in order book
        } catch (InterruptedException e) {
            log.error("Error", e);
        }
        if (cryptoCurrency.equals("BTC")) {
            final LunoRequestData result = api.sendMoney(destinationAddress, amount.toString(), "XBT", description);
            return result.getResult();
        } else {
            final LunoRequestData result = api.sendMoney(destinationAddress, amount.toString(), cryptoCurrency, description);
            return result.getResult();
        }
    }
    

}
