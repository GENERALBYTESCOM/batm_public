package com.generalbytes.batm.server.extensions;

import java.util.ArrayList;
import java.util.List;

public enum CryptoCurrency {
    ANT("Aragon Network Token"),
    ANON("ANON (formerly Anonymous Bitcoin)"),
    BAT("Basic Attention Token"),
    BCH("Bitcoin Cash"),
    BSD("BitSend"),
    BTC("Bitcoin"),
    BTCP("Bitcoin Private"),
    BTDX("Bitcloud"),
    BTX("Bitcore"),
    BURST("Burst"),
    CLOAK("CloakCoin"),
    DAI("Dai"),
    DASH("Dash"),
    DEX("DEX"),
    DGB("DigiByte"),
    DOGE("DOGE"),
    ECA("Electra"),
    EFL("e-Gulden"),
    ETH("Ethereum"),
    FLASH("FLASH Coin"),
    FTO("FuturoCoin"),
    GRS("Groestlcoin"),
    LEO("LEOcoin"),
    LINDA("Linda"),
    LTC("Litecoin"),
    LSK("Lisk"),
    MAX("Maxcoin"),
    MEC("Megacoin"),
    MKR("Maker"),
    NBT("NuBits"),
    NLG("NLG"),
    NXT("Nxt"),
    PAC("PACcoin"),
    POT("PotCoin"),
    REP("Augur"),
    SMART("SmartCash"),
    START("Startcoin"),
    SUM("SumCoin"),
    SYS("Syscoin"),
    TKN("TokenCard"),
    USDT("Tether"),
    VIA("Viacoin"),
    WDC("WorldCoin"),
    XMR("Monero"),
    XZC("Zcoin");

    private final String currencyName;

    CryptoCurrency(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCode() {
        return name();
    }

    public static List<String> names() {
        CryptoCurrency[] values = values();
        List<String> res = new ArrayList<>(values.length);
        for (CryptoCurrency c : values) {
            res.add(c.name());
        }
        return res;
    }
}
