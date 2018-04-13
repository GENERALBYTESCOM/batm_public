package com.generalbytes.batm.server.extensions.extra.flashcoin.sources.coinexchange;

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
 * Created by vsobalski on 15/02/18.
 */

public class CoinexchangeRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(CoinexchangeRateSource.class);

    private IcoinexchangeAPI api;
    private String preferedFiatCurrency;

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    public CoinexchangeRateSource(String preferedFiatCurrency) {
        if (!getFiatCurrencies().contains(preferedFiatCurrency)) {
            preferedFiatCurrency = ICurrencies.BTC;
        }
        this.preferedFiatCurrency = preferedFiatCurrency;


        api = RestProxyFactory.createProxy(IcoinexchangeAPI.class, "https://www.coinexchange.io/api/v1/");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.FLASH);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
       // result.add(ICurrencies.LTC);

        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return this.preferedFiatCurrency;
    }


    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }

        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called bitcoinaverage exchange for rate: " + key + " = " + result);
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
                    log.debug("Called bitcoinaverage exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key,result);
                    rateTimes.put(key,now+MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }

    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.FLASH.equalsIgnoreCase(cryptoCurrency)) {
            return null; //unsupported currency
        }
        String key = null;

        switch(fiatCurrency){
            case ICurrencies.BTC:
                key = "684";
                break;
            default:
                return null;
        }

        OrderBookResponse orderBookResponse = api.returnOrderBook(key);
        if (orderBookResponse != null && orderBookResponse.success==1) {
            return  orderBookResponse.result.AskPrice;
        }

        return null;
    }
   public static void main(String[] args) {
       CoinexchangeRateSource rs = new CoinexchangeRateSource(ICurrencies.BTC);
        BigDecimal exchangeRateLast = rs.getExchangeRateLast(ICurrencies.FLASH, ICurrencies.BTC);
    }
}
