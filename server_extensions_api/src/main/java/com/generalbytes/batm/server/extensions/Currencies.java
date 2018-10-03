/* ***********************************************************************************
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

@SuppressWarnings("WeakerAccess")
public class Currencies {

    public static final String BTC = "BTC";
    public static final String BCH = "BCH"; //Bitcoin Cash
    public static final String BTCP = "BTCP"; //Bitcoin Private
    public static final String BTX = "BTX"; //BitCore
    public static final String BSD = "BSD"; //BitSend
    public static final String BTDX = "BTDX"; //Bitcloud
    public static final String BURST = "BURST"; //Burstcoin
    public static final String ECA = "ECA"; //Electra
    public static final String ETH = "ETH";
    public static final String EFL = "EFL";
    public static final String LTC = "LTC";
    public static final String LSK = "LSK"; //lisk
    public static final String VIA = "VIA";
    public static final String DEX = "DEX";
    public static final String DASH = "DASH";
    public static final String DGB = "DGB";
    public static final String DOGE = "DOGE";
    public static final String FLASH = "FLASH";// FLASH Coin
    public static final String FTO = "FTO";
    public static final String MAX = "MAX";
    public static final String MEC = "MEC"; // Megacoin
    public static final String LEO = "LEO";
    public static final String LINDA = "LINDA";
    public static final String NLG = "NLG";
    public static final String GRS = "GRS";
    public static final String ICG = "ICG";
    public static final String NBT = "NBT";
    public static final String NXT = "NXT";
    public static final String PAC = "$PAC";
    public static final String POT = "POT";
    public static final String SMART = "SMART";
    public static final String START = "START";
    public static final String SUM = "SUM"; // sumcoin
    public static final String SYS = "SYS"; //  Syscoin 3
    public static final String TKN = "TKN";
    public static final String WDC = "WDC";
    public static final String XMR = "XMR";
    public static final String ANON = "ANON"; // ANON, formerly Anonymous Bitcoin
    public static final String USDT = "USDT";
  
    public static final String XZC = "XZC"; //Zcoin

    public static final String AED = "AED";
    public static final String AMD = "AMD";
    public static final String AUD = "AUD";
    public static final String BGN = "BGN";
    public static final String CAD = "CAD";
    public static final String CHF = "CHF";
    public static final String CNY = "CNY";
    public static final String COP = "COP";
    public static final String CRC = "CRC";
    public static final String CZK = "CZK";
    public static final String DKK = "DKK";
    public static final String DOP = "DOP";
    public static final String ECO = "ECO"; // Scottish pound
    public static final String EUR = "EUR";
    public static final String GEL = "GEL";
    public static final String GBP = "GBP";
    public static final String GIP = "GIP";
    public static final String GTQ = "GTQ";
    public static final String HKD = "HKD";
    public static final String HRK = "HRK";
    public static final String HUF = "HUF";
    public static final String ILS = "ILS";
    public static final String ISK = "ISK";
    public static final String INR = "INR";
    public static final String JEP = "JEP";
    public static final String JPY = "JPY";
    public static final String KES = "KES";
    public static final String KRW = "KRW";
    public static final String KZT = "KZT";
    public static final String MKD = "MKD";
    public static final String MXN = "MXN";
    public static final String MYR = "MYR";
    public static final String NOK = "NOK";
    public static final String NZD = "NZD";
    public static final String PEN = "PEN";
    public static final String PHP = "PHP";
    public static final String PLN = "PLN";
    public static final String RON = "RON";
    public static final String RSD = "RSD";
    public static final String RUB = "RUB";
    public static final String SAR = "SAR";
    public static final String SGD = "SGD";
    public static final String THB = "THB";
    public static final String TWD = "TWD";
    public static final String TRY = "TRY"; // Turkish lira
    public static final String UAH = "UAH";
    public static final String USD = "USD";
    public static final String UYU = "UYU";
    public static final String VND = "VND";
    public static final String XAF = "XAF";
    public static final String ZAR = "ZAR";

    public static final String XAU = "XAU"; // gold

    public static final String TBTC = "TBTC"; //test btc
    public static final String TXRP = "TXRP"; //test xrp
    public static final String TBCH = "TBCH"; //test bch
    public static final String TRMG = "TRMG"; //test rmg
    public static final String TLTC = "TLTC"; //test ltc
    public static final String TETH = "TETH"; //test eth
    public static final String NONE = "";

    public static final List<String> FIAT_CURRENCIES;
    public static final List<String> CRYPTO_CURRENCIES;

    static {
        List<String> crypto = new ArrayList<>();
        crypto.add(BTC);
        crypto.add(BCH);
        crypto.add(BTCP);
        crypto.add(BTX);
        crypto.add(BSD);
        crypto.add(BTDX);
        crypto.add(BURST);
        crypto.add(ECA);
        crypto.add(EFL);
        crypto.add(ETH);
        crypto.add(LTC);
        crypto.add(VIA);
        crypto.add(DEX);
        crypto.add(DASH);
        crypto.add(DGB);
        crypto.add(DOGE);
        crypto.add(FLASH);
        crypto.add(MAX);
        crypto.add(MEC);
        crypto.add(LEO);
        crypto.add(LINDA);
        crypto.add(LSK);
        crypto.add(NLG);
        crypto.add(GRS);
        crypto.add(ICG);
        crypto.add(NBT);
        crypto.add(NXT);
        crypto.add(PAC);
        crypto.add(POT);
        crypto.add(SMART);
        crypto.add(START);
        crypto.add(SYS);
        crypto.add(TKN);
        crypto.add(WDC);
        crypto.add(XMR);
        crypto.add(ANON);
        crypto.add(USDT);        
        crypto.add(XZC);

        CRYPTO_CURRENCIES = Collections.unmodifiableList(crypto);

        List<String> fiat = new ArrayList<>();
        fiat.add(AED);
        fiat.add(AMD);
        fiat.add(AUD);
        fiat.add(BGN);
        fiat.add(CAD);
        fiat.add(CHF);
        fiat.add(CNY);
        fiat.add(COP);
        fiat.add(CRC);
        fiat.add(CZK);
        fiat.add(DKK);
        fiat.add(DOP);
        fiat.add(EUR);
        fiat.add(GEL);
        fiat.add(GBP);
        fiat.add(GIP);
        fiat.add(GTQ);
        fiat.add(HKD);
        fiat.add(HRK);
        fiat.add(HUF);
        fiat.add(ILS);
        fiat.add(ISK);
        fiat.add(INR);
        fiat.add(JEP);
        fiat.add(JPY);
        fiat.add(KES);
        fiat.add(KRW);
        fiat.add(KZT);
        fiat.add(MKD);
        fiat.add(MXN);
        fiat.add(MYR);
        fiat.add(NOK);
        fiat.add(NZD);
        fiat.add(PEN);
        fiat.add(PHP);
        fiat.add(PLN);
        fiat.add(RON);
        fiat.add(RSD);
        fiat.add(RUB);
        fiat.add(SAR);
        fiat.add(SGD);
        fiat.add(THB);
        fiat.add(TWD);
        fiat.add(TRY);
        fiat.add(UAH);
        fiat.add(USD);
        fiat.add(UYU);
        fiat.add(VND);
        fiat.add(XAF);
        fiat.add(ZAR);

        fiat.add(XAU);

        FIAT_CURRENCIES = Collections.unmodifiableList(fiat);
    }
}