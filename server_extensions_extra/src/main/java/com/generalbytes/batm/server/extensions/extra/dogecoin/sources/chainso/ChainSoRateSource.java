package com.generalbytes.batm.server.extensions.extra.dogecoin.sources.chainso;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by b00lean on 8/11/14.
 */
public class ChainSoRateSource implements IRateSource{
    private static final Logger log = LoggerFactory.getLogger(ChainSoRateSource.class);

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    private IChainSo api;


    public ChainSoRateSource() {
        api = RestProxyFactory.createProxy(IChainSo.class, "https://chain.so");
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.DOGE.equalsIgnoreCase(cryptoCurrency)) {
            return null;
        }
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrency)) {
            return null;
        }

        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called chain.so exchange for rate: " + key + " = " + result);
                rateAmounts.put(key,result);
                rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                return result;
            }else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                }else{
                    //do the job;
                    BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                    log.debug("Called chain.so exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.DOGE.equalsIgnoreCase(cryptoCurrency)) {
            return null;
        }
        if (!ICurrencies.USD.equalsIgnoreCase(fiatCurrency)) {
            return null;
        }

        ChainSoResponse response = api.getPrices(cryptoCurrency,fiatCurrency);
        if (response != null && response.getData() != null && response.getData().getPrices() != null) {
            ChainSoPrice[] prices = response.getData().getPrices();
            for (int i = 0; i < prices.length; i++) {
                ChainSoPrice price = prices[i];
                if ("cryptsy".equalsIgnoreCase(price.getExchange()) && fiatCurrency.equalsIgnoreCase(price.getPrice_base())) {
                    return new BigDecimal(price.getPrice());
                }
            }
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.DOGE);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.USD);
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return ICurrencies.USD;
    }
}
