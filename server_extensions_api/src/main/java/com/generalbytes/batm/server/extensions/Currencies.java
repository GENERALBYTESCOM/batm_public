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

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;

/**
 * @deprecated Use FiatCurrency and CryptoCurrency instead
 */
@SuppressWarnings("WeakerAccess")
@Deprecated
public class Currencies {

    //
    // DO NOT ADD NEW CURRENCIES HERE !!!
    //

    public static final String ANT      = CryptoCurrency.ANT.getCode();    // Aragon Network Token
    public static final String ANON     = CryptoCurrency.ANON.getCode();   // ANON (formerly Anonymous Bitcoin)
    public static final String BAT      = CryptoCurrency.BAT.getCode();    // Basic Attention Token
    public static final String BCH      = CryptoCurrency.BCH.getCode();    // Bitcoin Cash
    public static final String BSD      = CryptoCurrency.BSD.getCode();    // BitSend
    public static final String BTC      = CryptoCurrency.BTC.getCode();    // Bitcoin
    public static final String BTCP     = CryptoCurrency.BTCP.getCode();   // Bitcoin Private
    public static final String BTDX     = CryptoCurrency.BTDX.getCode();   // Bitcloud
    public static final String BTX      = CryptoCurrency.BTX.getCode();    // Bitcore
    public static final String BURST    = CryptoCurrency.BURST.getCode();  // Burst
    public static final String CLOAK    = CryptoCurrency.CLOAK.getCode();  // CloakCoin
    public static final String DAI      = CryptoCurrency.DAI.getCode();    // Dai
    public static final String DASH     = CryptoCurrency.DASH.getCode();   // Dash
    public static final String DEX      = CryptoCurrency.DEX.getCode();    // DEX
    public static final String DGB      = CryptoCurrency.DGB.getCode();    // DigiByte
    public static final String DOGE     = CryptoCurrency.DOGE.getCode();   // DOGE
    public static final String ECA      = CryptoCurrency.ECA.getCode();    // Electra
    public static final String EFL      = CryptoCurrency.EFL.getCode();    // e-Gulden
    public static final String ETH      = CryptoCurrency.ETH.getCode();    // Ethereum
    public static final String FLASH    = CryptoCurrency.FLASH.getCode();  // FLASH Coin
    public static final String FTO      = CryptoCurrency.FTO.getCode();    // FuturoCoin
    public static final String GRS      = CryptoCurrency.GRS.getCode();    // Groestlcoin
    public static final String LEO      = CryptoCurrency.LEO.getCode();    // LEOcoin
    public static final String LINDA    = CryptoCurrency.LINDA.getCode();  // Linda
    public static final String LTC      = CryptoCurrency.LTC.getCode();    // Litecoin
    public static final String LSK      = CryptoCurrency.LSK.getCode();    // Lisk
    public static final String MAX      = CryptoCurrency.MAX.getCode();    // Maxcoin
    public static final String MEC      = CryptoCurrency.MEC.getCode();    // Megacoin
    public static final String MKR      = CryptoCurrency.MEC.getCode();    // Maker
    public static final String NBT      = CryptoCurrency.NBT.getCode();    // NuBits
    public static final String NLG      = CryptoCurrency.NLG.getCode();    // NLG
    public static final String NXT      = CryptoCurrency.NXT.getCode();    // Nxt
    public static final String PAC      = "$PAC";   // PACcoin
    public static final String POT      = CryptoCurrency.POT.getCode();    // PotCoin
    public static final String REP      = CryptoCurrency.REP.getCode();    // Augur
    public static final String SMART    = CryptoCurrency.SMART.getCode();  // SmartCash
    public static final String START    = CryptoCurrency.START.getCode();  // Startcoin
    public static final String SUM      = CryptoCurrency.SUM.getCode();    // SumCoin
    public static final String SYS      = CryptoCurrency.SYS.getCode();    // Syscoin
    public static final String TKN      = CryptoCurrency.TKN.getCode();    // TokenCard
    public static final String USDT     = CryptoCurrency.USDT.getCode();   // Tether
    public static final String VIA      = CryptoCurrency.VIA.getCode();    // Viacoin
    public static final String WDC      = CryptoCurrency.WDC.getCode();    // WorldCoin
    public static final String XMR      = CryptoCurrency.XMR.getCode();    // Monero
    public static final String XZC      = CryptoCurrency.XZC.getCode();    // Zcoin

    public static final String AED      = FiatCurrency.AED.getCode();    // United Arab Emirates dirham
    public static final String AMD      = FiatCurrency.AMD.getCode();    // Armenian dram
    public static final String AUD      = FiatCurrency.AUD.getCode();    // Australian dollar
    public static final String BGN      = FiatCurrency.BGN.getCode();    // Bulgarian lev
    public static final String BHD      = FiatCurrency.BHD.getCode();    // Bahraini dinar
    public static final String CAD      = FiatCurrency.CAD.getCode();    // Canadian dollar
    public static final String CHF      = FiatCurrency.CHF.getCode();    // Swiss franc
    public static final String CNY      = FiatCurrency.CNY.getCode();    // Chinese yuan renminbi
    public static final String COP      = FiatCurrency.COP.getCode();    // Colombian peso
    public static final String CRC      = FiatCurrency.CRC.getCode();    // Costa Rican Colón
    public static final String CZK      = FiatCurrency.CZK.getCode();    // Czech koruna
    public static final String DKK      = FiatCurrency.DKK.getCode();    // Danish krone
    public static final String DOP      = FiatCurrency.DOP.getCode();    // Dominican peso
    public static final String ECO      = FiatCurrency.ECO.getCode();    // Scottish pound
    public static final String EUR      = FiatCurrency.EUR.getCode();    // Euro
    public static final String GEL      = FiatCurrency.GEL.getCode();    // Georgian lari
    public static final String GBP      = FiatCurrency.GBP.getCode();    // British pound
    public static final String GIP      = FiatCurrency.GIP.getCode();    // Gibraltar pound
    public static final String GTQ      = FiatCurrency.GTQ.getCode();    // Guatemalan quetzal
    public static final String HKD      = FiatCurrency.HKD.getCode();    // Hong Kong dollar
    public static final String HRK      = FiatCurrency.HRK.getCode();    // Croatian kuna
    public static final String HUF      = FiatCurrency.HUF.getCode();    // Hungarian forint
    public static final String ILS      = FiatCurrency.ILS.getCode();    // Israeli new shekel
    public static final String ISK      = FiatCurrency.ISK.getCode();    // Icelandic króna
    public static final String INR      = FiatCurrency.INR.getCode();    // Indian rupee
    public static final String JEP      = FiatCurrency.JEP.getCode();    // Jersey pound
    public static final String JPY      = FiatCurrency.JPY.getCode();    // Japanese yen
    public static final String KES      = FiatCurrency.KES.getCode();    // Kenyan shilling
    public static final String KRW      = FiatCurrency.KRW.getCode();    // South Korean won
    public static final String KWD      = FiatCurrency.KWD.getCode();    // Kuwaiti dinar
    public static final String KZT      = FiatCurrency.KZT.getCode();    // Kazakhstani tenge
    public static final String MKD      = FiatCurrency.MKD.getCode();    // Macedonian denar
    public static final String MXN      = FiatCurrency.MXN.getCode();    // Mexican peso
    public static final String MYR      = FiatCurrency.MYR.getCode();    // Malaysian ringgit
    public static final String NOK      = FiatCurrency.NOK.getCode();    // Norwegian krone
    public static final String NZD      = FiatCurrency.NZD.getCode();    // New Zealand dollar
    public static final String PEN      = FiatCurrency.PEN.getCode();    // Peruvian sol
    public static final String PHP      = FiatCurrency.PHP.getCode();    // Philippine piso
    public static final String PLN      = FiatCurrency.PLN.getCode();    // Polish złoty
    public static final String RON      = FiatCurrency.RON.getCode();    // Romanian leu
    public static final String RSD      = FiatCurrency.RSD.getCode();    // Serbian dinar
    public static final String RUB      = FiatCurrency.RUB.getCode();    // Russian ruble
    public static final String SAR      = FiatCurrency.SAR.getCode();    // Saudi riyal
    public static final String SGD      = FiatCurrency.SGD.getCode();    // Singapore dollar
    public static final String THB      = FiatCurrency.THB.getCode();    // Thai baht
    public static final String TWD      = FiatCurrency.TWD.getCode();    // New Taiwan dollar
    public static final String TRY      = FiatCurrency.TRY.getCode();    // Turkish lira
    public static final String UAH      = FiatCurrency.UAH.getCode();    // Ukrainian hryvnia
    public static final String USD      = FiatCurrency.USD.getCode();    // United States dollar
    public static final String UYU      = FiatCurrency.UYU.getCode();    // Uruguayan peso
    public static final String VND      = FiatCurrency.VND.getCode();    // Vietnamese dong
    public static final String XAF      = FiatCurrency.XAF.getCode();    // Central African CFA franc
    public static final String ZAR      = FiatCurrency.ZAR.getCode();    // South African rand

    public static final String XAU      = FiatCurrency.XAU.getCode();    // gold

    public static final String TBTC     = CryptoCurrency.TBTC.getCode();   // test BTC
    public static final String TXRP     = CryptoCurrency.TXRP.getCode();   // test XRP
    public static final String TBCH     = CryptoCurrency.TBCH.getCode();   // test BCH
    public static final String TRMG     = CryptoCurrency.TRMG.getCode();   // test RMG
    public static final String TLTC     = CryptoCurrency.TLTC.getCode();   // test LTC
    public static final String TETH     = CryptoCurrency.TETH.getCode();   // test ETH

    public static final String NONE     = "";
}
