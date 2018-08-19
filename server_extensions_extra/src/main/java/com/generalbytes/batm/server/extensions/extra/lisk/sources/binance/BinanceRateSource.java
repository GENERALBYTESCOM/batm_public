/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.lisk.sources.binance;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbnb.Lskwallet;
 
 
import si.mazi.rescu.RestProxyFactory;
 
import java.math.BigDecimal;
import java.util.*; 

 

import java.util.List;
/**
 * Created by kkyovsky on 11/29/17.
 *
 * Modified by sidhujag on 6/3/2018
 */

public class BinanceRateSource implements IRateSource {
    /**
     * Expiry of cache in seconds
     */ 
    private BnbAPI api;
    private TetherPriceAPI api_tether;
    private String preferredFiatCurrency = Currencies.USD; 

    public BinanceRateSource(String preferedFiatCurrency) {
    	
    	api = RestProxyFactory.createProxy(BnbAPI.class, "https://api.binance.com");
        api_tether = RestProxyFactory.createProxy(TetherPriceAPI.class, "https://api.coingecko.com");
         
        if (Currencies.USD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.USD;
        } 
        
        if (Currencies.HKD.equalsIgnoreCase(preferedFiatCurrency)) {
            this.preferredFiatCurrency = Currencies.HKD;
        }
    }
 
    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.BTC);
        result.add(Currencies.BCH);
        result.add(Currencies.LTC);
        result.add(Currencies.ETH);
        result.add(Currencies.DASH);
        result.add(Currencies.XMR);
        result.add(Currencies.LSK);

        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.USD);
        result.add(Currencies.HKD);
        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return this.preferredFiatCurrency;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
    	
        if (!getFiatCurrencies().contains(fiatCurrency)) {
            return null;
        }
        
        final BnbTickerData BTC_USD_Price = api.getTicker("BTCUSDT");
		final BnbTickerData Coin_BTC_Price = api.getTicker(cryptoCurrency + "BTC");
		final List<Object> USDT_Fiat_data = api_tether.getTetherPrice(fiatCurrency.toLowerCase(), "tether");
		
		if (USDT_Fiat_data != null && BTC_USD_Price.getPrice()!=null && Coin_BTC_Price.getPrice() !=null ) {
			final Map<String, Object> USDT_Fiat_json = (Map<String, Object>) USDT_Fiat_data.get(0); 
			final Double USDT_Fiat = (Double) USDT_Fiat_json.get("current_price");
			if (USDT_Fiat != 0) {
				final Float Coin_Fiat_Price = (float) (BTC_USD_Price.getPrice() * Coin_BTC_Price.getPrice() * USDT_Fiat);
				
				return new BigDecimal(String.format( "%.3f", Coin_Fiat_Price ));
			}
			else
		    	return null;
		}
        
        
      	return null;
    }
    
 
}
