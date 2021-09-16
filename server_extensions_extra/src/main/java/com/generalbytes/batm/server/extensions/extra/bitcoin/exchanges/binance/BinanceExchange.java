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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BinanceExchange extends XChangeExchange {

    public BinanceExchange(String preferredFiatCurrency, String sslUri) {
        super(getDefaultSpecification(sslUri), preferredFiatCurrency);
    }

    public BinanceExchange(String key, String secret, String preferredFiatCurrency, String sslUri) {
        super(getSpecification(key, secret, sslUri), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification(String sslUri) {
        //TODO: BATM-2471 Use original commented code
//        ExchangeSpecification spec = new org.knowm.xchange.binance.BinanceExchange().getDefaultExchangeSpecification();
        ExchangeSpecification spec = new CustomBinanceExchange().getDefaultExchangeSpecification();
        spec.setSslUri(sslUri);
        spec.setShouldLoadRemoteMetaData(true); // true is default, but make sure it loads current withdraw fees
        return spec;
    }

    private static ExchangeSpecification getSpecification(String key, String secret, String sslUri) {
        ExchangeSpecification spec = getDefaultSpecification(sslUri);
        spec.setApiKey(key);
        spec.setSecretKey(secret);
        return spec;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return true;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    public Wallet getWallet(AccountInfo accountInfo, String currency) {
        return accountInfo.getWallet();
    }

    @Override
    protected BigDecimal getTradableAmount(BigDecimal cryptoAmount, CurrencyPair currencyPair) {
        try {
            BigDecimal minStep = getExchange().getExchangeMetaData().getCurrencyPairs().get(currencyPair).getAmountStepSize();
            return minStep == null ? cryptoAmount : getAmountRoundedToMinStep(cryptoAmount, minStep);
        } catch (Exception e) {
            log.error("error adjusting the amount", e);
            return cryptoAmount;
        }
    }

    @Override
    protected BigDecimal getWithdrawAmount(BigDecimal cryptoAmount, String cryptoCurrency) {
        BigDecimal minWithdrawStep = getWithdrawalMinStep(cryptoCurrency);
        return minWithdrawStep != null ? getAmountRoundedToMinStep(cryptoAmount, minWithdrawStep) : cryptoAmount;
    }

    protected BigDecimal getAmountRoundedToMinStep(BigDecimal cryptoAmount, BigDecimal minStep) {
        return cryptoAmount.divideToIntegralValue(minStep).multiply(minStep);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        BigDecimal withdrawalFee = getWithdrawalFee(cryptoCurrency);
        BigDecimal withdrawalAmount = amount.add(withdrawalFee);
        if (CryptoCurrency.XRP.getCode().equals(cryptoCurrency)) {
            amount = amount.setScale(6, RoundingMode.FLOOR);
        }
        log.info("Withdrawing {} + {} withdrawal fee = {} {}", amount, withdrawalFee, withdrawalAmount, cryptoCurrency);

        return super.sendCoins(destinationAddress, withdrawalAmount, cryptoCurrency, description);
    }

    protected abstract Set<SupportedCryptoCurrency> getSupportedCryptoCurrencies();

    @Override
    public Set<String> getCryptoCurrencies() {
        return getSupportedCryptoCurrencies().stream().map(SupportedCryptoCurrency::getCryptoCurrency).collect(Collectors.toSet());
    }

    protected BigDecimal getWithdrawalMinStep(String cryptoCurrency) {
        return getSupportedCryptoCurrencies().stream()
            .filter(c -> c.getCryptoCurrency().equals(cryptoCurrency))
            .findFirst()
            .map(SupportedCryptoCurrency::getWithdrawalMinStep)
            .orElse(null);
    }

    protected static class SupportedCryptoCurrency {
        private String cryptoCurrency;
        private BigDecimal withdrawalMinStep;

        public SupportedCryptoCurrency(String cryptoCurrency) {
            this(cryptoCurrency, new BigDecimal("0.00000001"));
        }

        public SupportedCryptoCurrency(String cryptoCurrency, BigDecimal withdrawalMinStep) {
            this.cryptoCurrency = cryptoCurrency;
            this.withdrawalMinStep = withdrawalMinStep;
        }

        public String getCryptoCurrency() {
            return cryptoCurrency;
        }

        public BigDecimal getWithdrawalMinStep() {
            return withdrawalMinStep;
        }
    }
}