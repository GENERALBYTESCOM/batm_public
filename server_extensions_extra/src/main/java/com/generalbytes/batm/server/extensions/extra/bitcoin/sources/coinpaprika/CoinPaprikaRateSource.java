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
package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coinpaprika;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.HeaderParam;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoinPaprikaRateSource implements IRateSource {
    private static final Logger log = LoggerFactory.getLogger(CoinPaprikaRateSource.class);
    private static final Map<String, String> CRYPTOCURRENCIES = new HashMap<>();

    static {
        // from https://api.coinpaprika.com/v1/coins
        CRYPTOCURRENCIES.put(CryptoCurrency.ANON.getCode(), "anon-anon");
        CRYPTOCURRENCIES.put(CryptoCurrency.ANT.getCode(), "ant-aragon");
        CRYPTOCURRENCIES.put(CryptoCurrency.BAT.getCode(), "bat-basic-attention-token");
        CRYPTOCURRENCIES.put(CryptoCurrency.BAY.getCode(), "bay-bitbay");
        CRYPTOCURRENCIES.put(CryptoCurrency.BCH.getCode(), "bch-bitcoin-cash");
        CRYPTOCURRENCIES.put(CryptoCurrency.BNB.getCode(), "bnb-binance-coin");
        CRYPTOCURRENCIES.put(CryptoCurrency.BSD.getCode(), "bsd-bitsend");
        CRYPTOCURRENCIES.put(CryptoCurrency.BTC.getCode(), "btc-bitcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.BTCP.getCode(), "btcp-bitcoin-private");
        CRYPTOCURRENCIES.put(CryptoCurrency.BTDX.getCode(), "btdx-bitcloud");
        CRYPTOCURRENCIES.put(CryptoCurrency.BTX.getCode(), "btx-bitcore");
        CRYPTOCURRENCIES.put(CryptoCurrency.BURST.getCode(), "burst-burst");
        CRYPTOCURRENCIES.put(CryptoCurrency.CLOAK.getCode(), "cloak-cloakcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.DAI.getCode(), "dai-dai");
        CRYPTOCURRENCIES.put(CryptoCurrency.BIZZ.getCode(), "bizz-bizzcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.DASH.getCode(), "dash-dash");
        CRYPTOCURRENCIES.put(CryptoCurrency.DEX.getCode(), "dex-dex");
        CRYPTOCURRENCIES.put(CryptoCurrency.DGB.getCode(), "dgb-digibyte");
        CRYPTOCURRENCIES.put(CryptoCurrency.DOGE.getCode(), "doge-dogecoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.ECA.getCode(), "eca-electra");
        CRYPTOCURRENCIES.put(CryptoCurrency.EFL.getCode(), "efl-e-gulden");
        CRYPTOCURRENCIES.put(CryptoCurrency.ETH.getCode(), "eth-ethereum");
        CRYPTOCURRENCIES.put(CryptoCurrency.FLASH.getCode(), "flash-flash");
        CRYPTOCURRENCIES.put(CryptoCurrency.FTO.getCode(), "fto-futurocoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.GRS.getCode(), "grs-groestlcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.HATCH.getCode(), "hatch-hatch");
        CRYPTOCURRENCIES.put(CryptoCurrency.HBX.getCode(), "hbx-hashbx");
        CRYPTOCURRENCIES.put(CryptoCurrency.KMD.getCode(), "kmd-komodo");
        CRYPTOCURRENCIES.put(CryptoCurrency.LEO.getCode(), "leo-leocoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.LINDA.getCode(), "linda-linda");
        CRYPTOCURRENCIES.put(CryptoCurrency.LSK.getCode(), "lsk-lisk");
        CRYPTOCURRENCIES.put(CryptoCurrency.LTC.getCode(), "ltc-litecoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.MAX.getCode(), "max-maxcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.MEC.getCode(), "mec-megacoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.MKR.getCode(), "mkr-maker");
        CRYPTOCURRENCIES.put(CryptoCurrency.MUE.getCode(), "mue-monetaryunit");
        CRYPTOCURRENCIES.put(CryptoCurrency.NANO.getCode(), "nano-nano");
        CRYPTOCURRENCIES.put(CryptoCurrency.NLG.getCode(), "nlg-gulden");
        CRYPTOCURRENCIES.put(CryptoCurrency.NULS.getCode(), "nuls-nuls");
        CRYPTOCURRENCIES.put(CryptoCurrency.NXT.getCode(), "nxt-nxt");
        CRYPTOCURRENCIES.put(CryptoCurrency.POT.getCode(), "pot-potcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.REP.getCode(), "rep-augur");
        CRYPTOCURRENCIES.put(CryptoCurrency.SMART.getCode(), "smart-smartcash");
        CRYPTOCURRENCIES.put(CryptoCurrency.SPICE.getCode(), "spice-spice");
        CRYPTOCURRENCIES.put(CryptoCurrency.START.getCode(), "start-startcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.SYS.getCode(), "sys-syscoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.TBTC.getCode(), "tbtc-tidbit-coin");
        CRYPTOCURRENCIES.put(CryptoCurrency.TRTL.getCode(), "trtl-turtlecoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.TKN.getCode(), "tkn-tokencard");
        CRYPTOCURRENCIES.put(CryptoCurrency.TRX.getCode(), "trx-tron");
        CRYPTOCURRENCIES.put(CryptoCurrency.USDT.getCode(), "usdt-tether");
        CRYPTOCURRENCIES.put(CryptoCurrency.VIA.getCode(), "via-viacoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.VOLTZ.getCode(), "voltz-voltz");
        CRYPTOCURRENCIES.put(CryptoCurrency.WDC.getCode(), "wdc-worldcoin");
        CRYPTOCURRENCIES.put(CryptoCurrency.XMR.getCode(), "xmr-monero");
        CRYPTOCURRENCIES.put(CryptoCurrency.XRP.getCode(), "xrp-xrp");
        CRYPTOCURRENCIES.put(CryptoCurrency.TENT.getCode(), "tent-tent");
        CRYPTOCURRENCIES.put(CryptoCurrency.XZC.getCode(), "xzc-zcoin");
    }

    private final CoinPaprikaV1API api;
    private final String preferredFiatCurrency;

    public CoinPaprikaRateSource(String preferredFiatCurrency) {
        this.preferredFiatCurrency = preferredFiatCurrency;
        final ClientConfig config = new ClientConfig().addDefaultParam(HeaderParam.class, "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
        api = RestProxyFactory.createProxy(CoinPaprikaV1API.class, "https://api.coinpaprika.com", config);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTOCURRENCIES.keySet();
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(FiatCurrency.AUD.getCode());
        result.add(FiatCurrency.BRL.getCode());
        result.add(FiatCurrency.CAD.getCode());
        result.add(FiatCurrency.CHF.getCode());
        result.add(FiatCurrency.CNY.getCode());
        result.add(FiatCurrency.COP.getCode());
        result.add(FiatCurrency.CZK.getCode());
        result.add(FiatCurrency.DKK.getCode());
        result.add(FiatCurrency.EUR.getCode());
        result.add(FiatCurrency.GBP.getCode());
        result.add(FiatCurrency.HKD.getCode());
        result.add(FiatCurrency.HUF.getCode());
        result.add(FiatCurrency.ILS.getCode());
        result.add(FiatCurrency.INR.getCode());
        result.add(FiatCurrency.ISK.getCode());
        result.add(FiatCurrency.JPY.getCode());
        result.add(FiatCurrency.KRW.getCode());
        result.add(FiatCurrency.MXN.getCode());
        result.add(FiatCurrency.MYR.getCode());
        result.add(FiatCurrency.NOK.getCode());
        result.add(FiatCurrency.NZD.getCode());
        result.add(FiatCurrency.PEN.getCode());
        result.add(FiatCurrency.PHP.getCode());
        result.add(FiatCurrency.PLN.getCode());
        result.add(FiatCurrency.RUB.getCode());
        result.add(FiatCurrency.SGD.getCode());
        result.add(FiatCurrency.THB.getCode());
        result.add(FiatCurrency.TRY.getCode());
        result.add(FiatCurrency.TWD.getCode());
        result.add(FiatCurrency.UAH.getCode());
        result.add(FiatCurrency.USD.getCode());
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
            return api.getTicker(crypto, fiatCurrency).quotes.get(fiatCurrency).price;
        } catch (HttpStatusIOException e) {
            log.warn(e.getHttpBody(), e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

//    public static void main(String[] args) {
//        System.out.println(new CoinPaprikaRateSource("USD").getExchangeRateLast("BTC", "CZK"));
//    }
}
