/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coingecko;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoinGeckoRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(CoinGeckoRateSource.class);
    private static final Map<String, String> CRYPTOCURRENCIES = new HashMap<>();

    static {
        // from https://api.coingecko.com/api/v3/coins/list
        CRYPTOCURRENCIES.put(CryptoCurrency.ANON.getCode(), "anon");
        CRYPTOCURRENCIES.put(CryptoCurrency.ANT.getCode(), "aragon");
        CRYPTOCURRENCIES.put(CryptoCurrency.BAT.getCode(), "basic-attention-token");
        CRYPTOCURRENCIES.put(CryptoCurrency.BCH.getCode(), "bitcoin-cash");
        CRYPTOCURRENCIES.put(CryptoCurrency.BNB.getCode(), "binancecoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.BSD.getCode(), "bitsend");
        CRYPTOCURRENCIES.put(CryptoCurrency.BTC.getCode(), "bitcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.BTCP.getCode(), "bitcoin-private");
        CRYPTOCURRENCIES.put(CryptoCurrency.BTDX.getCode(), "bitcloud");
        CRYPTOCURRENCIES.put(CryptoCurrency.BTX.getCode(), "bitcore");
        CRYPTOCURRENCIES.put(CryptoCurrency.BURST.getCode(), "burst");
        CRYPTOCURRENCIES.put(CryptoCurrency.CLOAK.getCode(), "cloakcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.DAI.getCode(), "dai");
        CRYPTOCURRENCIES.put(CryptoCurrency.BIZZ.getCode(), "bizzcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.DASH.getCode(), "dash");
        CRYPTOCURRENCIES.put(CryptoCurrency.DEX.getCode(), "dex");
        CRYPTOCURRENCIES.put(CryptoCurrency.DGB.getCode(), "digibyte");
        CRYPTOCURRENCIES.put(CryptoCurrency.DOGE.getCode(), "dogecoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.ECA.getCode(), "electra");
        CRYPTOCURRENCIES.put(CryptoCurrency.EFL.getCode(), "electronicgulden");
        CRYPTOCURRENCIES.put(CryptoCurrency.ETH.getCode(), "ethereum");
        CRYPTOCURRENCIES.put(CryptoCurrency.FLASH.getCode(), "flash");
        CRYPTOCURRENCIES.put(CryptoCurrency.FTO.getCode(), "futurocoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.GRS.getCode(), "groestlcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.HATCH.getCode(), "hatch");
        CRYPTOCURRENCIES.put(CryptoCurrency.ILC.getCode(), "ilcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.KMD.getCode(), "komodo");
        CRYPTOCURRENCIES.put(CryptoCurrency.LEO.getCode(), "leocoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.LINDA.getCode(), "linda");
        CRYPTOCURRENCIES.put(CryptoCurrency.LSK.getCode(), "lisk");
        CRYPTOCURRENCIES.put(CryptoCurrency.LTC.getCode(), "litecoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.MAX.getCode(), "maxcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.MEC.getCode(), "megacoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.MKR.getCode(), "maker");
        CRYPTOCURRENCIES.put(CryptoCurrency.MUE.getCode(), "monetaryunit");
        CRYPTOCURRENCIES.put(CryptoCurrency.NYC.getCode(), "newyorkcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.NBT.getCode(), "ninsa-b-token");
        CRYPTOCURRENCIES.put(CryptoCurrency.NLG.getCode(), "gulden");
        CRYPTOCURRENCIES.put(CryptoCurrency.NANO.getCode(), "nano");
        CRYPTOCURRENCIES.put(CryptoCurrency.NULS.getCode(), "nuls");
        CRYPTOCURRENCIES.put(CryptoCurrency.NXT.getCode(), "nxt");
        CRYPTOCURRENCIES.put(CryptoCurrency.PAXG.getCode(), "pax-gold");
        CRYPTOCURRENCIES.put(CryptoCurrency.POT.getCode(), "potcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.REP.getCode(), "augur");
        CRYPTOCURRENCIES.put(CryptoCurrency.SMART.getCode(), "smartcash");
        CRYPTOCURRENCIES.put(CryptoCurrency.SPICE.getCode(), "spice");
        CRYPTOCURRENCIES.put(CryptoCurrency.START.getCode(), "startcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.SYS.getCode(), "syscoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.TKN.getCode(), "tokencard");
        CRYPTOCURRENCIES.put(CryptoCurrency.TRX.getCode(), "tron");
        CRYPTOCURRENCIES.put(CryptoCurrency.USDT.getCode(), "tether");
        CRYPTOCURRENCIES.put(CryptoCurrency.USDTTRON.getCode(), "tether"); // using USDT for rate source
        CRYPTOCURRENCIES.put(CryptoCurrency.USDS.getCode(), "stableusd");
        CRYPTOCURRENCIES.put(CryptoCurrency.VIA.getCode(), "viacoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.VOLTZ.getCode(), "voltz");
        CRYPTOCURRENCIES.put(CryptoCurrency.WDC.getCode(), "worldcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.XMR.getCode(), "monero");
        CRYPTOCURRENCIES.put(CryptoCurrency.XRP.getCode(), "ripple");
        CRYPTOCURRENCIES.put(CryptoCurrency.TENT.getCode(), "tent");
        CRYPTOCURRENCIES.put(CryptoCurrency.XZC.getCode(), "zcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.ZPAE.getCode(), "zelaapayae");
    }

    private final CoinGeckoV3API api;
    private final String preferredFiatCurrency;

    public CoinGeckoRateSource(String preferredFiatCurrency) {
        this.preferredFiatCurrency = preferredFiatCurrency;
        api = RestProxyFactory.createProxy(CoinGeckoV3API.class, "https://api.coingecko.com/api");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTOCURRENCIES.keySet();
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(FiatCurrency.USD.getCode());
        result.add(FiatCurrency.AED.getCode());
        result.add(FiatCurrency.AUD.getCode());
        result.add(FiatCurrency.BHD.getCode());
        result.add(FiatCurrency.BRL.getCode());
        result.add(FiatCurrency.CAD.getCode());
        result.add(FiatCurrency.CHF.getCode());
        result.add(FiatCurrency.CNY.getCode());
        result.add(FiatCurrency.CZK.getCode());
        result.add(FiatCurrency.DKK.getCode());
        result.add(FiatCurrency.EUR.getCode());
        result.add(FiatCurrency.GBP.getCode());
        result.add(FiatCurrency.HKD.getCode());
        result.add(FiatCurrency.HUF.getCode());
        result.add(FiatCurrency.ILS.getCode());
        result.add(FiatCurrency.INR.getCode());
        result.add(FiatCurrency.JPY.getCode());
        result.add(FiatCurrency.KRW.getCode());
        result.add(FiatCurrency.KWD.getCode());
        result.add(FiatCurrency.MXN.getCode());
        result.add(FiatCurrency.MYR.getCode());
        result.add(FiatCurrency.NOK.getCode());
        result.add(FiatCurrency.NZD.getCode());
        result.add(FiatCurrency.PHP.getCode());
        result.add(FiatCurrency.PLN.getCode());
        result.add(FiatCurrency.RUB.getCode());
        result.add(FiatCurrency.SAR.getCode());
        result.add(FiatCurrency.SGD.getCode());
        result.add(FiatCurrency.THB.getCode());
        result.add(FiatCurrency.TRY.getCode());
        result.add(FiatCurrency.TWD.getCode());
        result.add(FiatCurrency.UAH.getCode());
        result.add(FiatCurrency.VND.getCode());
        result.add(FiatCurrency.ZAR.getCode());
        return result;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        if (!getFiatCurrencies().contains(fiatCurrency) || !CRYPTOCURRENCIES.containsKey(cryptoCurrency)) {
            log.warn("{}-{} pair not supported", cryptoCurrency, fiatCurrency);
            return null;
        }

        try {
            String crypto = CRYPTOCURRENCIES.get(cryptoCurrency);
            String fiat = fiatCurrency.toLowerCase();
            return api.getPrice(crypto, fiat).get(crypto).get(fiat);
        } catch (HttpStatusIOException e) {
            log.warn(e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

//    public static void main(String[] args) {
//        System.out.println(new CoinGeckoRateSource("USD").getExchangeRateLast("XMR", "USD"));
//    }
}
