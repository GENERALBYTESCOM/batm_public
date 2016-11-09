/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.yahoo;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


public class YahooFinanceRateSource implements IRateSource{
    private static final String[] FIAT_CURRENCIES={"USD","EUR","CNY","CAD","RON","XAF","AUD","GBP","CZK","CHF","JPY"};
    private static final Logger log = LoggerFactory.getLogger(YahooFinanceRateSource.class);


    private static final HashMap<String,BigDecimal> rateAmounts = new HashMap<String, BigDecimal>();
    private static HashMap<String,Long> rateTimes = new HashMap<String, Long>();
    private static final long MAXIMUM_ALLOWED_TIME_OFFSET = 10 * 60 * 1000; //10 min


    private String preferedFiatCurrency = ICurrencies.USD;

    public YahooFinanceRateSource(String preferedFiatCurrency) {
        if (ICurrencies.EUR.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.EUR;
        }else if (ICurrencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferedFiatCurrency = ICurrencies.USD;
        }else{
            this.preferedFiatCurrency = preferedFiatCurrency;
        }

    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return null;
        }
        String key = cryptoCurrency +"_" + fiatCurrency;
        synchronized (rateAmounts) {
            long now  = System.currentTimeMillis();
            BigDecimal amount = rateAmounts.get(key);
            if (amount == null) {
                final Map<String, BigDecimal> rates = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                BigDecimal result = null;
                if (rates != null) {
                    for (String toCurrency : rates.keySet()) {
                        String key2 = cryptoCurrency + "_" + toCurrency;
                        if (key.equalsIgnoreCase(key2)) {
                            result = rates.get(toCurrency);
                        }
                        rateAmounts.put(key2, rates.get(toCurrency));
                        rateTimes.put(key2, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                    }
                }
                log.debug("Called YahooFinance for rate: " + key + " = " + result);
                return result;
            }else {
                Long expirationTime = rateTimes.get(key);
                if (expirationTime > now) {
                    return rateAmounts.get(key);
                }else{
                    //do the job;
                    final Map<String, BigDecimal> rates = getExchangeRateLastSync(cryptoCurrency, fiatCurrency);
                    BigDecimal result = null;
                    if (rates != null) {
                        for (String toCurrency : rates.keySet()) {
                            String key2 = cryptoCurrency + "_" + toCurrency;
                            if (key.equalsIgnoreCase(key2)) {
                                result = rates.get(toCurrency);
                            }
                            rateAmounts.put(key2, rates.get(toCurrency));
                            rateTimes.put(key2, now + MAXIMUM_ALLOWED_TIME_OFFSET);
                        }
                    }
                    log.debug("Called YahooFinance for rate: " + key + " = " + result);
                    return result;

                }
            }
        }

    }

    private Map<String, BigDecimal> getExchangeRateLastSync(String cryptoCurrency, String fiatCurrency) {
        if (!ICurrencies.BTC.equalsIgnoreCase(cryptoCurrency)) {
            return null; //unsupported currency
        }
        final Set<String> fiatCurrencies = getFiatCurrencies();
        fiatCurrencies.add(fiatCurrency);
        final Map<String, BigDecimal> exchangeRates = getExchangeRates(cryptoCurrency, fiatCurrencies);
        return exchangeRates;
    }


    private static Map<String,BigDecimal> getExchangeRates(String fromCurrency, Set<String> toCurrencies) {
        try {
            StringBuilder sb = new StringBuilder();
            for (String toCurrency : toCurrencies) {
                sb.append(fromCurrency.toUpperCase()+toCurrency+"=X");
                sb.append("+");
            }

            Map<String,BigDecimal> results = new HashMap<String, BigDecimal>();
            final String symbols = sb.toString();
            String url = "https://download.finance.yahoo.com/d/quotes.csv?e=.csv&f=sl1d1t1&s=" + symbols;
            String result = getHTML(url);
            if (result != null) {
                result = result.replace("\"","");
                StringTokenizer lines = new StringTokenizer(result,"\n");
                while (lines.hasMoreElements()) {
                    String line = lines.nextToken();
                    StringTokenizer st = new StringTokenizer(line,",");
                    String symbol = st.nextToken() ; //symbol
                    symbol = symbol.substring(fromCurrency.length(),symbol.length()-"=X".length());
                    BigDecimal rate = new BigDecimal(st.nextToken());
                    results.put(symbol,rate);
                    log.info("Obtained exchange rate " + fromCurrency +">" + symbol + " = " + rate );
                }
            }
            return results;

        }catch (Throwable t) {
            t.printStackTrace();
        }
        log.error("ERROR: Error obtaining exchange rate for " + fromCurrency + ">" + Arrays.toString(toCurrencies.toArray()));
        return null;
    }

    private static String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            while ((line = rd.readLine()) != null) {
                result += line +"\n";
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<String>();
        fiatCurrencies.add(preferedFiatCurrency.toUpperCase());
        fiatCurrencies.addAll(Arrays.asList(FIAT_CURRENCIES));
        return fiatCurrencies;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferedFiatCurrency;
    }

//    public static void main(String[] args) {
//        YahooFinanceRateSource yf = new YahooFinanceRateSource("CZK");
//        BigDecimal exchangeRateLast = yf.getExchangeRateLast(ICurrencies.BTC, ICurrencies.CZK);
//        System.out.println("exchangeRateLast = " + exchangeRateLast);
//        exchangeRateLast = yf.getExchangeRateLast(ICurrencies.BTC, ICurrencies.USD);
//        System.out.println("exchangeRateLast = " + exchangeRateLast);
//    }
}

