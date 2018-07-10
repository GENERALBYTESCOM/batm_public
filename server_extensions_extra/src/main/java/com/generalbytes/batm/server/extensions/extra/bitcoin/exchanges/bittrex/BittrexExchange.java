package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.dash.sources.cddash.CompatSSLSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BittrexExchange implements IRateSourceAdvanced, IExchangeAdvanced {

    private static final Logger log = LoggerFactory.getLogger("batm.master.BittrexExchange");

    private static final Set<String> FIAT_CURRENCIES = new HashSet<>();
    private static final Set<String> CRYPTO_CURRENCIES = new HashSet<>();

    private IBittrexAPI api;

    private String apiKey;
    private String apiSecret;
    private String preferedFiatCurrency;
    private static final String nonce = "123456";

    static {
        initConstants();
    }

    private static void initConstants() {
        FIAT_CURRENCIES.add(Currencies.USD);
        CRYPTO_CURRENCIES.add(Currencies.BTC);
    }

    public BittrexExchange() {
        try {
            ClientConfig config = new ClientConfig();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(true);
            api = RestProxyFactory.createProxy(IBittrexAPI.class, "https://bittrex.com/api", config);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error(ValueFormatUtils.logMsg("constructor", "Cannot create instance."), e);
        }
    }

    public BittrexExchange(String apiKey, String apiSecret, String preferedFiatCurrency) {
        this();
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.preferedFiatCurrency = preferedFiatCurrency;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        try {
            String url = "https://bittrex.com/api/v1.1/account/getbalance?apikey=" + apiKey + "&currency=" + cryptoCurrency + "&nonce=" + nonce;
            String apisign = HmacSha512.HMACSHA512(url, apiSecret);
            Map<String, Object> balance = api.getBalance(apisign, apiKey, cryptoCurrency, nonce);
            log.info("" + balance);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getPreferredFiatCurrency() {
        if(StringUtils.isNotEmpty(preferedFiatCurrency)) {
            return preferedFiatCurrency;
        }

        return Currencies.USD;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            String quantity = amount.toEngineeringString();
            String url = "https://bittrex.com/api/v1.1/account/getbalance?apikey=" + apiKey + "&currency=" + cryptoCurrency + "&quantity=" + quantity + "&address=" + destinationAddress + "&nonce=" + nonce;
            String apisign = HmacSha512.HMACSHA512(url, apiSecret);
            Object response = api.withdraw(apisign, apiKey, cryptoCurrency, Integer.valueOf(quantity), destinationAddress, nonce);
            log.info("" + response);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        return null;
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return null;
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {

        return null;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTO_CURRENCIES;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return FIAT_CURRENCIES;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        return null;
    }



    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return null;
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return null;
    }
}
