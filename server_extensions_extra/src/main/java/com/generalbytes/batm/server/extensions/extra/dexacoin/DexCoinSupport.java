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
package com.generalbytes.batm.server.extensions.extra.dexacoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class DexCoinSupport extends AbstractExtension implements IExchange, IWallet, ICryptoAddressValidator {
    private static final String CRYPTO_CURRENCY = CryptoCurrency.DEX.getCode();
    private static final BigDecimal WALLET_BALANCE = new BigDecimal("1000000");
    private static final BigDecimal EXCHANGE_BALANCE = new BigDecimal("2000000");
    private static final String WALLET_ADDRESS = CRYPTO_CURRENCY.substring(1) + "GnubsaWBQf6J2TTvNLF5xLkMydhTjWsQi";

    private String preferredFiatCurrency;
    private BigDecimal rate = BigDecimal.ONE;

    public DexCoinSupport() {
    }

    public DexCoinSupport(String preferredFiatCurrency, BigDecimal rate) {
        this.rate = rate;
        this.preferredFiatCurrency = preferredFiatCurrency;
        if (FiatCurrency.EUR.getCode().equalsIgnoreCase(preferredFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.EUR.getCode();
        }
        if (FiatCurrency.USD.getCode().equalsIgnoreCase(preferredFiatCurrency)) {
            this.preferredFiatCurrency = FiatCurrency.USD.getCode();
        }
    }

    @Override
    public String getName() {
        return "BATM " + CRYPTO_CURRENCY + " extension";
    }



    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency.equalsIgnoreCase(CRYPTO_CURRENCY) && fiatCurrencyToUse.equalsIgnoreCase(preferredFiatCurrency)) {
            return "true";
        }else{
            return null;
        }
    }

    @Override
    public String getCryptoAddress( String cryptoCurrency) {
        if (CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return WALLET_ADDRESS;
        }else{
            return null;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CRYPTO_CURRENCY);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(preferredFiatCurrency);
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CRYPTO_CURRENCY;
    }


    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return WALLET_BALANCE;
        }else{
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getFiatBalance(String cashCurrency) {
        if (preferredFiatCurrency.equalsIgnoreCase(cashCurrency)) {
            return EXCHANGE_BALANCE;
        }else{
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return "txt_id";
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return WALLET_ADDRESS;
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return "tx_sell_id";
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        if (exchangeLogin !=null && !exchangeLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(exchangeLogin,":");
            String exchangeType = st.nextToken();

            if ((CRYPTO_CURRENCY.toLowerCase()+ "_exchange").equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new DexCoinSupport(preferredFiatCurrency,rate);
            }
        }
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ((CRYPTO_CURRENCY.toLowerCase()+ "_wallet").equalsIgnoreCase(walletType)) {
                return new DexCoinSupport();
            }
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ((CRYPTO_CURRENCY.toLowerCase() + "_fix").equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                String preferedFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferedFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate,preferedFiatCurrency);
            }
        }
        return null;
    }


    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return new DexCoinSupport();
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CRYPTO_CURRENCY);
        return result;
    }

    @Override
    public boolean isAddressValid(String address) {
        boolean result = isCryptoAddressValid(address);
        if (!result) {
            result = isPaperWalletSupported() && ExtensionsUtil.isValidEmailAddress(address);
        }
        return result;
    }

    private boolean isCryptoAddressValid(String address) {
        if (address.startsWith(CRYPTO_CURRENCY.substring(1))) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }


}
