package com.generalbytes.batm.server.extensions.extra.flashcoin.sources.cryptopia;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.Currencies;
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

public class CryptopiaRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(CryptopiaRateSource.class);

    private IcryptopiaAPI api;
    private String preferedFiatCurrency;

    private static HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000; //30sec

    public CryptopiaRateSource(String preferedFiatCurrency) {
        if (!getFiatCurrencies().contains(preferedFiatCurrency)) {
            preferedFiatCurrency = Currencies.BTC;
        }
        this.preferedFiatCurrency = preferedFiatCurrency;


        api = RestProxyFactory.createProxy(IcryptopiaAPI.class, "https://www.cryptopia.co.nz/api");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.FLASH);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.BTC);
       // result.add(Currencies.LTC);

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
        if (!Currencies.FLASH.equalsIgnoreCase(cryptoCurrency)) {
            return null; //unsupported currency
        }
        String key = cryptoCurrency +"_" + fiatCurrency;
        OrderBookResponse orderBookResponse = api.returnOrderBook(key);
        if (orderBookResponse != null && orderBookResponse.getSuccess()) {
            return  orderBookResponse.getData().getAskPrice();
        }

        return null;
    }
   public static void main(String[] args) {
       CryptopiaRateSource rs = new CryptopiaRateSource(Currencies.BTC);
        BigDecimal exchangeRateLast = rs.getExchangeRateLast(Currencies.FLASH, Currencies.BTC);
        System.out.println("BTC exchangeRateLast = " + exchangeRateLast);
       exchangeRateLast = rs.getExchangeRateLast(Currencies.FLASH, Currencies.LTC);
       System.out.println("LTC exchangeRateLast = " + exchangeRateLast);
    }
}
