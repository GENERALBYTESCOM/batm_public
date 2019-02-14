package com.generalbytes.batm.common.currencies;

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
    PAC("$PAC", "PACcoin"),
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
    XZC("Zcoin"),

    TBTC("test BTC"),
    TXRP("test XRP"),
    TBCH("test BCH"),
    TRMG("test RMG"),
    TLTC("test LTC"),
    TETH("test ETH"),
    ;

    public final String currencyName;
    public final String code;

    CryptoCurrency(String code, String currencyName) {
        this.currencyName = currencyName;
        this.code = code == null ? name() : code;
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
// TODO tests BTC, $PAC

    /**
     * @throws IllegalArgumentException
     * @param cryptoCurrency
     * @return
     */
    public static CryptoCurrency valueOfCode(String cryptoCurrency) {
        for (CryptoCurrency c : values()) {
            if (c.getCode().equalsIgnoreCase(cryptoCurrency)) {
                return c;
            }
        }
        throw new IllegalArgumentException(cryptoCurrency + " not found");
    }

    public static List<String> getCodes() {
        CryptoCurrency[] values = values();
        List<String> res = new ArrayList<>(values.length);
        for (CryptoCurrency c : values) {
            res.add(c.getCode());
        }
        return res;
    }
}
