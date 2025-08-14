package com.generalbytes.batm.server.extensions.extra.stellar.source.bpventure;

import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.util.net.RateLimitingInterceptor;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.Interceptor;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPVentureRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(BPVentureRateSource.class);
    private static final Map<String, String> CRYPTOCURRENCIES = new HashMap<>();

    static {
        // Example cryptocurrency mappings, adjust as needed
        CRYPTOCURRENCIES.put(CryptoCurrency.USDCXLM.getCode(), "USDC");
        CRYPTOCURRENCIES.put(CryptoCurrency.XLM.getCode(), "USDC");
    }

    private final FXFeedAPI api;
    private final String preferredFiatCurrency;

    public BPVentureRateSource(String preferredFiatCurrency) {
        this.preferredFiatCurrency = preferredFiatCurrency;
        Interceptor interceptor = new RateLimitingInterceptor(FXFeedAPI.class, 50 / 60.0, 5_000);
        
        // Create the API proxy using the RestProxyFactory
        api = RestProxyFactory.createProxy(FXFeedAPI.class, "https://fx-feed.bpventures.us/api", null, interceptor);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTOCURRENCIES.keySet();
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(FiatCurrency.CAD.getCode());
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency) || !CRYPTOCURRENCIES.containsKey(cryptoCurrency)) {
            log.warn("BPVentureRateSource: {}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }

        try {
            String crypto = CRYPTOCURRENCIES.get(cryptoCurrency);
            String fiat = fiatCurrency.toUpperCase();

            // Print the API call
            log.info("BPVentureRateSource: Calling API with crypto:", crypto);

            FXFeedResponse response = api.getPrice(crypto);

            // Print the raw response
            log.info("BPVentureRateSource: Received response: ", response);


            if (response != null && response.getData() != null) {
                Map<String, String> rates = response.getData().getRates();
                String rate = rates.get(fiat);

                if (rate != null) {
                    return new BigDecimal(rate);
                } else {
                    log.info("Rate for " + cryptoCurrency + "-" + fiatCurrency + " not found in response");
                    return null;
                }
            } else {
                log.info("Invalid response received for " + cryptoCurrency + "-" + fiatCurrency);
                return null;
            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP status error: ", e.getHttpBody());

            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error getting exchange rate");
            log.error("Error getting exchange rate");

            e.printStackTrace();
        }
        return null;
    }

    // public static void main(String[] args) {
    //     System.out.println(new BPVentureRateSource("CAD").getExchangeRateLast("USDC", "CAD"));
    // }
}
