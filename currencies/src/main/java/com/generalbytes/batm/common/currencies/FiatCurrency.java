/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
import java.util.HashSet;
import java.util.Set;

/**
 * Fiat Currencies
 *
 * Usage e.g.:
 *      FiatCurrency.USD.getCurrencyName()
 *      FiatCurrency.valueOf("USD").getCurrencyName()
 */
public enum FiatCurrency {

    // see also com.generalbytes.batm.server.extensions.Currencies

    AED("United Arab Emirates dirham"),
    ALL("Albanian lek"),
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

    private static final Set<String> codes;

    static {
        final Set<String> tmp = new HashSet<>();
        for (FiatCurrency fc : FiatCurrency.values()) {
            tmp.add(fc.name());
        }
        codes = Collections.unmodifiableSet(tmp);
    }

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

    public static Set<String> getCodes() {
        return codes;
    }
}
