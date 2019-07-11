package com.generalbytes.batm.server.extensions.extra.ethereum.sources.stasis;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.coinutil.DDOSUtils;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class StasisTickerRateSource implements IRateSourceAdvanced {

    private static final Logger log = LoggerFactory.getLogger("batm.master.StasisRateSource");

    private static final HashMap<String, BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 30 * 1000;
    private static HashMap<String, Long> rateTimes = new HashMap<String, Long>();
    private IStasisTickerRateAPI api;

    public StasisTickerRateSource() {
        api = RestProxyFactory.createProxy(IStasisTickerRateAPI.class, "https://api.stasis.net/exchange");
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        return calculateBuyPrice(cryptoCurrency, fiatCurrency, BigDecimal.ONE);
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        return calculateSellPrice(cryptoCurrency, fiatCurrency, BigDecimal.ONE);
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        DDOSUtils.waitForPossibleCall(getClass());
        try {
            DDOSUtils.waitForPossibleCall(getClass());

            StasisTickerResponse ticker = api.getPrices();
            BigDecimal price = ticker.getEURS().getRate();

            log.debug("Called STASIS exchange for BUY rate: {}{} = {}", cryptoCurrency, fiatCurrency, price);
            return price;

        } catch (Throwable e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        DDOSUtils.waitForPossibleCall(getClass());
        try {
            DDOSUtils.waitForPossibleCall(getClass());

            StasisTickerResponse ticker = api.getPrices();
            BigDecimal price = ticker.getEUR().getRate();

            log.debug("Called STASIS exchange for SELL rate: {}{} = {}", cryptoCurrency, fiatCurrency, price);
            return price;

        } catch (Throwable e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.EURS.getCode());
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.EUR.getCode());
        return result;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!CryptoCurrency.EURS.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return null;
        }
        if (!FiatCurrency.EUR.getCode().equalsIgnoreCase(fiatCurrency)) {
            return null;
        }

        String key = fiatCurrency + ":" + cryptoCurrency;
        synchronized (rateAmounts) {
            long now = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                log.debug("Called STASIS exchange for rate: " + key + " = " + result);
                rateAmounts.put(key, result);
                rateTimes.put(key, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                return result;
            } else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                } else {
                    //do the job;
                    BigDecimal result = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                    log.debug("Called STASIS exchange for rate: " + key + " = " + result);
                    rateAmounts.put(key, result);
                    rateTimes.put(key, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                    return result;
                }
            }
        }
    }

    private BigDecimal getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!(CryptoCurrency.EURS.getCode().equalsIgnoreCase(cryptoCurrency))) {
            return null;
        }
        if (!(FiatCurrency.EUR.getCode().equalsIgnoreCase(fiatCurrency))) {
            return null;
        }
        StasisTickerResponse ticker = api.getPrices();
        if (ticker != null && ticker.getEUR() != null) {
            return ticker.getEUR().getRate();
        }
        return null;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return FiatCurrency.EUR.getCode();
    }
}
