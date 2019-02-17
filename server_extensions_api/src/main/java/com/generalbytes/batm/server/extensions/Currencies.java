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

    public static final String ANT      = "ANT";    // Aragon Network Token
    public static final String ANON     = "ANON";   // ANON (formerly Anonymous Bitcoin)
    public static final String BAT      = "BAT";    // Basic Attention Token
    public static final String BCH      = "BCH";    // Bitcoin Cash
    public static final String BSD      = "BSD";    // BitSend
    public static final String BTC      = "BTC";    // Bitcoin
    public static final String BTCP     = "BTCP";   // Bitcoin Private
    public static final String BTDX     = "BTDX";   // Bitcloud
    public static final String BTX      = "BTX";    // Bitcore
    public static final String BURST    = "BURST";  // Burst
    public static final String CLOAK    = "CLOAK";  // CloakCoin
    public static final String DAI      = "DAI";    // Dai
    public static final String DASH     = "DASH";   // Dash
    public static final String DEX      = "DEX";    // DEX
    public static final String DGB      = "DGB";    // DigiByte
    public static final String DOGE     = "DOGE";   // DOGE
    public static final String ECA      = "ECA";    // Electra
    public static final String EFL      = "EFL";    // e-Gulden
    public static final String ETH      = "ETH";    // Ethereum
    public static final String FLASH    = "FLASH";  // FLASH Coin
    public static final String FTO      = "FTO";    // FuturoCoin
    public static final String GRS      = "GRS";    // Groestlcoin
    public static final String LEO      = "LEO";    // LEOcoin
    public static final String LINDA    = "LINDA";  // Linda
    public static final String LTC      = "LTC";    // Litecoin
    public static final String LSK      = "LSK";    // Lisk
    public static final String MAX      = "MAX";    // Maxcoin
    public static final String MEC      = "MEC";    // Megacoin
    public static final String MKR      = "MEC";    // Maker
    public static final String NBT      = "NBT";    // NuBits
    public static final String NLG      = "NLG";    // NLG
    public static final String NXT      = "NXT";    // Nxt
    public static final String PAC      = "$PAC";   // PACcoin
    public static final String POT      = "POT";    // PotCoin
    public static final String REP      = "REP";    // Augur
    public static final String SMART    = "SMART";  // SmartCash
    public static final String START    = "START";  // Startcoin
    public static final String SUM      = "SUM";    // SumCoin
    public static final String SYS      = "SYS";    // Syscoin
    public static final String TKN      = "TKN";    // TokenCard
    public static final String USDT     = "USDT";   // Tether
    public static final String VIA      = "VIA";    // Viacoin
    public static final String WDC      = "WDC";    // WorldCoin
    public static final String XMR      = "XMR";    // Monero
    public static final String XZC      = "XZC";    // Zcoin
    public static final String NULS      = "NULS";    // NULS

    public static final String AED      = "AED";    // United Arab Emirates dirham
    public static final String AMD      = "AMD";    // Armenian dram
    public static final String AUD      = "AUD";    // Australian dollar
    public static final String BGN      = "BGN";    // Bulgarian lev
    public static final String BHD      = "BHD";    // Bahraini dinar
    public static final String CAD      = "CAD";    // Canadian dollar
    public static final String CHF      = "CHF";    // Swiss franc
    public static final String CNY      = "CNY";    // Chinese yuan renminbi
    public static final String COP      = "COP";    // Colombian peso
    public static final String CRC      = "CRC";    // Costa Rican Colón
    public static final String CZK      = "CZK";    // Czech koruna
    public static final String DKK      = "DKK";    // Danish krone
    public static final String DOP      = "DOP";    // Dominican peso
    public static final String ECO      = "ECO";    // Scottish pound
    public static final String EUR      = "EUR";    // Euro
    public static final String GEL      = "GEL";    // Georgian lari
    public static final String GBP      = "GBP";    // British pound
    public static final String GIP      = "GIP";    // Gibraltar pound
    public static final String GTQ      = "GTQ";    // Guatemalan quetzal
    public static final String HKD      = "HKD";    // Hong Kong dollar
    public static final String HRK      = "HRK";    // Croatian kuna
    public static final String HUF      = "HUF";    // Hungarian forint
    public static final String ILS      = "ILS";    // Israeli new shekel
    public static final String ISK      = "ISK";    // Icelandic króna
    public static final String INR      = "INR";    // Indian rupee
    public static final String JEP      = "JEP";    // Jersey pound
    public static final String JPY      = "JPY";    // Japanese yen
    public static final String KES      = "KES";    // Kenyan shilling
    public static final String KRW      = "KRW";    // South Korean won
    public static final String KWD      = "KWD";    // Kuwaiti dinar
    public static final String KZT      = "KZT";    // Kazakhstani tenge
    public static final String MKD      = "MKD";    // Macedonian denar
    public static final String MXN      = "MXN";    // Mexican peso
    public static final String MYR      = "MYR";    // Malaysian ringgit
    public static final String NOK      = "NOK";    // Norwegian krone
    public static final String NZD      = "NZD";    // New Zealand dollar
    public static final String PEN      = "PEN";    // Peruvian sol
    public static final String PHP      = "PHP";    // Philippine piso
    public static final String PLN      = "PLN";    // Polish złoty
    public static final String RON      = "RON";    // Romanian leu
    public static final String RSD      = "RSD";    // Serbian dinar
    public static final String RUB      = "RUB";    // Russian ruble
    public static final String SAR      = "SAR";    // Saudi riyal
    public static final String SGD      = "SGD";    // Singapore dollar
    public static final String THB      = "THB";    // Thai baht
    public static final String TWD      = "TWD";    // New Taiwan dollar
    public static final String TRY      = "TRY";    // Turkish lira
    public static final String UAH      = "UAH";    // Ukrainian hryvnia
    public static final String USD      = "USD";    // United States dollar
    public static final String UYU      = "UYU";    // Uruguayan peso
    public static final String VND      = "VND";    // Vietnamese dong
    public static final String XAF      = "XAF";    // Central African CFA franc
    public static final String ZAR      = "ZAR";    // South African rand

    public static final String XAU      = "XAU";    // gold

    public static final String TBTC     = "TBTC";   // test BTC
    public static final String TXRP     = "TXRP";   // test XRP
    public static final String TBCH     = "TBCH";   // test BCH
    public static final String TRMG     = "TRMG";   // test RMG
    public static final String TLTC     = "TLTC";   // test LTC
    public static final String TETH     = "TETH";   // test ETH

    public static final String NONE     = "";

    public static final List<String> FIAT_CURRENCIES;
    public static final List<String> CRYPTO_CURRENCIES;

    static {
        List<String> crypto = new ArrayList<>();
        crypto.add(ANON);
        crypto.add(ANT);
        crypto.add(BAT);
        crypto.add(BCH);
        crypto.add(BSD);
        crypto.add(BTC);
        crypto.add(BTCP);
        crypto.add(BTDX);
        crypto.add(BTX);
        crypto.add(BURST);
        crypto.add(CLOAK);
        crypto.add(DAI);
        crypto.add(DASH);
        crypto.add(DEX);
        crypto.add(DGB);
        crypto.add(DOGE);
        crypto.add(ECA);
        crypto.add(EFL);
        crypto.add(ETH);
        crypto.add(FLASH);
        crypto.add(FTO);
        crypto.add(GRS);
        crypto.add(LEO);
        crypto.add(LINDA);
        crypto.add(LTC);
        crypto.add(LSK);
        crypto.add(MAX);
        crypto.add(MEC);
        crypto.add(MKR);
        crypto.add(NBT);
        crypto.add(NLG);
        crypto.add(NXT);
        crypto.add(PAC);
        crypto.add(POT);
        crypto.add(REP);
        crypto.add(SMART);
        crypto.add(START);
        crypto.add(SUM);
        crypto.add(SYS);
        crypto.add(TKN);
        crypto.add(USDT);
        crypto.add(VIA);
        crypto.add(WDC);
        crypto.add(XMR);
        crypto.add(XZC);
        crypto.add(NULS);

        CRYPTO_CURRENCIES = Collections.unmodifiableList(crypto);

        List<String> fiat = new ArrayList<>();
        fiat.add(AED);
        fiat.add(AMD);
        fiat.add(AUD);
        fiat.add(BGN);
        fiat.add(BHD);
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
        fiat.add(KWD);
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
