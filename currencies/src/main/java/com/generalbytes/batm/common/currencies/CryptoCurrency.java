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
package com.generalbytes.batm.common.currencies;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cryptocurrencies
 *
 * Usage e.g.:
 *      CryptoCurrency.USD.getCurrencyName()
 *      CryptoCurrency.valueOfCode("USD").getCurrencyName()
 *
 * Important note:
 *      CryptoCurrency.valueOf("$PAC") returns IllegalArgumentException.
 */
public enum CryptoCurrency {

    ADA("Cardano Ada"),
    ANON("ANON"),
    AQUA("Aquachain"),
    ANT("Aragon Network Token"),
    BAT("Basic Attention Token"),
    BCH("Bitcoin Cash"),
    BNB("BinanceCoin"),
    BSD("BitSend"),
    BTC("Bitcoin"),
    BTCP("Bitcoin Private"),
    BTDX("Bitcloud"),
    BTX("Bitcore"),
    BURST("Burst"),
    BUSD("Binance USD"),
    CLOAK("CloakCoin"),
    DAI("Dai"),
    BIZZ("BIZZCOIN"),
    DASH("Dash"),
    DDB("Discount Distributors Bahamas"),
    DEX("DEX"),
    DGB("DigiByte"),
    DOGE("DOGE"),
    ECA("Electra"),
    EFL("e-Gulden"),
    EGG("NestEgg Coin"),
    ETC("Ethereum Classic"),
    ETH("Ethereum"),
    EURS("STASIS EURO"),
    FLASH("FLASH"),
    FTO("FuturoCoin"),
    GRS("Groestlcoin"),
    HATCH("Hatch"),
    HBX("HBX"),
    HT("Huobi Token"),
    ILC("ILCoin"),
    KMD("Komodo"),
    JOB("Jobchain"),
    LMY("Lunch Money"),
    LBTC("Lightning Network Bitcoin"),
    LEO("LEOcoin"),
    LINDA("Linda"),
    LSK("Lisk"),
    LTC("Litecoin"),
    MAX("Maxcoin"),
    MEC("Megacoin"),
    MKR("Maker"),
    MUSD("MovexUSD"),
    NANO("Nano"),
    NBT("NuBits"),
    NLG("NLG"),
    NXT("Nxt"),
    NYC("NewYorkCoin"),
    PAC("$PAC", "PACcoin"),
    PAXG("Paxos Gold"),
    POT("PotCoin"),
    REP("Augur"),
    SMART("SmartCash"),
    SPICE("Spice"),
    START("Startcoin"),
    SUM("Sumcoin"),
    SUNCASH("SunCash"),
    SYS("Syscoin"),
    THBX("DigitalThaiBaht"),
    TKN("TokenCard"),
    TRTL("TurtleCoin"),
    TRX("Tron"),
    USDS("StableUSD"),
    USDC("USD Coin"),
    USDT("Tether"),
    USDTTRON("Tether USD TRON"), // TRC20 based USDT stablecoin issued by Tether on the TRON network
    VIA("Viacoin"),
    VOLTZ("VOLTZ"),
    WDC("WorldCoin"),
    WILC("Wrapped ILCOIN"),
    XMR("Monero"),
    XORI("CHFToken"),
    TENT("Tent"),
    XRP("Ripple"),
    XZC("Zcoin"),
    BAY("BitBayCoin"),
    NULS("NULS"),
    MUE("MonetaryUnit"),
    XPM("Primecoin"),
    ZPAE("ZelaaPayAE"),

    TBCH("test BCH"),
    TBTC("test BTC"),
    TETH("test ETH"),
    TLTC("test LTC"),
    TRMG("test RMG"),
    TXRP("test XRP"),
    ;

    private static Map<String, CryptoCurrency> cryptoCurrenciesUpperCase = new HashMap<>();
    private static Map<String, CryptoCurrency> cryptoCurrencies = new LinkedHashMap<>();

    static {
        for (CryptoCurrency cc : CryptoCurrency.values()) {
            cryptoCurrenciesUpperCase.put(cc.code.toUpperCase(), cc);
            cryptoCurrencies.put(cc.code, cc);
        }
        cryptoCurrencies = Collections.unmodifiableMap(cryptoCurrencies);
    }

    public final String code;
    public final String currencyName;

    CryptoCurrency(String code, String currencyName) {
        this.currencyName = currencyName;
        this.code = (code == null) ? name() : code;
    }

    CryptoCurrency(String currencyName) {
        this(null, currencyName);
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCode() {
        return code;
    }

    /**
     * Use this method instead of Enum.valueOf.
     */
    public static CryptoCurrency valueOfCode(String code) {
        CryptoCurrency cc = cryptoCurrenciesUpperCase.get(code.toUpperCase());
        if (cc == null) {
            throw new IllegalArgumentException(code + " not found");
        }
        return cc;
    }

    public static Set<String> getCodes() {
        return cryptoCurrencies.keySet();
    }
}
