package com.generalbytes.batm.common.currencies;

import java.util.ArrayList;
import java.util.List;

public enum FiatCurrency {

    AED("United Arab Emirates dirham"),
    AMD("Armenian dram"),
    AUD("Australian dollar"),
    BGN("Bulgarian lev"),
    BHD("Bahraini dinar"),
    BRL("Brazilian real"),
    CAD("Canadian dollar"),
    CHF("Swiss franc"),
    CNY("Chinese yuan renminbi"),
    COP("Colombian peso"),
    CRC("Costa Rican Colón"),
    CZK("Czech koruna"),
    DKK("Danish krone"),
    DOP("Dominican peso"),
    ECO("Scottish pound"),
    EUR("Euro"),
    GBP("British pound"),
    GEL("Georgian lari"),
    GIP("Gibraltar pound"),
    GTQ("Guatemalan quetzal"),
    HKD("Hong Kong dollar"),
    HRK("Croatian kuna"),
    HUF("Hungarian forint"),
    ILS("Israeli new shekel"),
    INR("Indian rupee"),
    ISK("Icelandic króna"),
    JEP("Jersey pound"),
    JPY("Japanese yen"),
    KES("Kenyan shilling"),
    KRW("South Korean won"),
    KWD("Kuwaiti dinar"),
    KZT("Kazakhstani tenge"),
    MKD("Macedonian denar"),
    MXN("Mexican peso"),
    MYR("Malaysian ringgit"),
    NOK("Norwegian krone"),
    NZD("New Zealand dollar"),
    PEN("Peruvian sol"),
    PHP("Philippine piso"),
    PLN("Polish złoty"),
    RON("Romanian leu"),
    RSD("Serbian dinar"),
    RUB("Russian ruble"),
    SAR("Saudi riyal"),
    SGD("Singapore dollar"),
    THB("Thai baht"),
    TRY("Turkish lira"),
    TWD("New Taiwan dollar"),
    UAH("Ukrainian hryvnia"),
    USD("United States dollar"),
    UYU("Uruguayan peso"),
    VND("Vietnamese dong"),
    XAF("Central African CFA franc"),
    ZAR("South African rand"),

    XAU("Gold"),
    ;

    private final String currencyName;

    FiatCurrency(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCode() {
        return name();
    }

    public static List<String> getCodes() {
        FiatCurrency[] values = values();
        List<String> res = new ArrayList<>(values.length);
        for(FiatCurrency c : values){
            res.add(c.getCode());
        }
        return res;
    }
}
