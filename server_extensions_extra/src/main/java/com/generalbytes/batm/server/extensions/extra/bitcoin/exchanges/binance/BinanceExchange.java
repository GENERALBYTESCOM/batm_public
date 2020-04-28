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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.account.WithdrawRequest;
import org.knowm.xchange.binance.service.BinanceAccountService;
import org.knowm.xchange.binance.service.BinanceBaseService;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;

public abstract class BinanceExchange extends XChangeExchange {


    /* ***************** HOTFIX ******************
    REMOVE the interface and the overridden method below
    after https://github.com/knowm/XChange/pull/3500 is merged and released so we can use it instead
    */

    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public interface HotfixBinanceAuthenticated extends BinanceAuthenticated {

        @POST
        @Path("wapi/v3/withdraw.html")
        WithdrawRequest withdraw(
            @QueryParam("asset") String asset,
            @QueryParam("address") String address,
            @QueryParam("addressTag") String addressTag,
            @QueryParam("amount") BigDecimal amount,
            @QueryParam("name") String name,
            @QueryParam("recvWindow") Long recvWindow,
            @QueryParam("timestamp") long timestamp,
            @HeaderParam(X_MBX_APIKEY) String apiKey,
            @QueryParam(SIGNATURE) ParamsDigest signature)
            throws IOException, BinanceException;
    }

    @Override
    protected synchronized Exchange getExchange() {
        Exchange exchange = super.getExchange();
        try {
            Field accountServiceField = BaseExchange.class.getDeclaredField("accountService");
            accountServiceField.setAccessible(true);
            BinanceAccountService accountService = (BinanceAccountService) accountServiceField.get(exchange);
            Field binanceField = BinanceBaseService.class.getDeclaredField("binance");
            binanceField.setAccessible(true);
            binanceField.set(accountService, RestProxyFactory.createProxy(
                HotfixBinanceAuthenticated.class,
                exchange.getExchangeSpecification().getSslUri(),
                accountService.getClientConfig()));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error applying hotfix");
        }
        return exchange;
    }
    //////////// HOTFIX END ///////////////

    public BinanceExchange(String preferredFiatCurrency, String sslUri) {
        super(getDefaultSpecification(sslUri), preferredFiatCurrency);
    }

    public BinanceExchange(String key, String secret, String preferredFiatCurrency, String sslUri) {
        super(getSpecification(key, secret, sslUri), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification(String sslUri) {
        ExchangeSpecification spec = new org.knowm.xchange.binance.BinanceExchange().getDefaultExchangeSpecification();
        spec.setSslUri(sslUri);
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

    protected BigDecimal getAmountRoundedToMinStep(BigDecimal cryptoAmount, BigDecimal minStep) {
        return cryptoAmount.divideToIntegralValue(minStep).multiply(minStep);
    }
}