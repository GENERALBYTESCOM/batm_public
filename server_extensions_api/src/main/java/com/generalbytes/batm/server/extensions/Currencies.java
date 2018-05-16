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

package com.generalbytes.batm.server.extensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Currencies {
    public static final String BTC = "BTC";
    public static final String BCH = "BCH"; //Bitcoin Cash
    public static final String BTX = "BTX"; //BitCore
    public static final String ETH = "ETH";
    public static final String LTC = "LTC";
    public static final String VIA = "VIA";
    public static final String DEX = "DEX";
    public static final String DASH = "DASH";
    public static final String DGB = "DGB";
    public static final String DOGE = "DOGE";
    public static final String FLASH = "FLASH";// FLASH Coin
    public static final String MAX = "MAX";
    public static final String LEO = "LEO";
    public static final String NLG = "NLG";
    public static final String GRS = "GRS";
    public static final String ICG = "ICG";
    public static final String NBT = "NBT";
    public static final String NXT = "NXT";
    public static final String POT = "POT";
    public static final String SMART = "SMART";
    public static final String START = "START";
    public static final String TKN = "TKN";
    public static final String WDC = "WDC";
    public static final String XMR = "XMR";

    public static final String CAD = "CAD";
    public static final String CHF = "CHF";
    public static final String CNY = "CNY";
    public static final String CZK = "CZK";
    public static final String EUR = "EUR";
    public static final String GBP = "GBP";
    public static final String HUF = "HUF";
    public static final String JPY = "JPY";
    public static final String SGD = "SGD";
    public static final String USD = "USD";
    public static final String XAF = "XAF";
    public static final String XAU = "XAU"; //gold
    public static final String NONE = "";

    public static final List<String> FIAT_CURRENCIES;
    public static final List<String> CRYPTO_CURRENCIES;

    static {
        List<String> crypto = new ArrayList<>();
        crypto.add(BTC);
        crypto.add(BCH);
        crypto.add(BTX);
        crypto.add(ETH);
        crypto.add(LTC);
        crypto.add(VIA);
        crypto.add(DEX);
        crypto.add(DASH);
        crypto.add(DGB);
        crypto.add(DOGE);
        crypto.add(FLASH);
        crypto.add(MAX);
        crypto.add(LEO);
        crypto.add(NLG);
        crypto.add(GRS);
        crypto.add(ICG);
        crypto.add(NBT);
        crypto.add(NXT);
        crypto.add(POT);
        crypto.add(SMART);
        crypto.add(START);
        crypto.add(TKN);
        crypto.add(WDC);
        crypto.add(XMR);
        CRYPTO_CURRENCIES = Collections.unmodifiableList(crypto);

        List<String> fiat = new ArrayList<>();
        fiat.add(CAD);
        fiat.add(CHF);
        fiat.add(CNY);
        fiat.add(CZK);
        fiat.add(EUR);
        fiat.add(GBP);
        fiat.add(HUF);
        fiat.add(JPY);
        fiat.add(SGD);
        fiat.add(USD);
        fiat.add(XAF);
        fiat.add(XAU);
        FIAT_CURRENCIES = Collections.unmodifiableList(fiat);
    }
}
