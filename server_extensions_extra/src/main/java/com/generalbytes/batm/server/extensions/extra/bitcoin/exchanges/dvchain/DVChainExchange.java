/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
 * <p/>
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 * <p/>
 * Contact information
 * -------------------
 * <p/>
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 * <p/>
 * Other information:
 * <p/>
 * This implementation was created in cooperation with Sumbits http://www.getsumbits.com/
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.trade.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;
import java.math.BigDecimal;

public class DVChainExchange extends XChangeExchange {
    private final Logger log;

    public DVChainExchange(String apiSecret, boolean useSandbox, String preferredFiatCurrency) {
        super(getSpecification(apiSecret, useSandbox), preferredFiatCurrency);
        log = LoggerFactory.getLogger("batm.master.exchange.dvchain");
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.dvchain.DVChainExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String apiSecret, boolean useSandbox) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setSecretKey(apiSecret);
        if( useSandbox ) {
            System.out.println("Using Sandbox");
            spec.setSslUri("https://sandbox.trade.dvchain.co");
            spec.setHost("sandbox.trade.dvchain.co");
        }
        return spec;
    }


    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    protected boolean isWithdrawSuccessful(String string) {
        return true;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.USD.getCode());
        return fiatCurrencies;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.XMR.getCode());
        return cryptoCurrencies;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency){
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency){
        return BigDecimal.valueOf(10000000);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency){
        return null;
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {

        TradeService tradeService = super.getExchange().getTradeService();

        try {
            CurrencyPair currencyPair = new CurrencyPair(translateCryptoCurrencySymbolToExchangeSpecificSymbol(cryptoCurrency), fiatCurrencyToUse);

            MarketOrder order = new MarketOrder(
                Order.OrderType.BID,
                amount,
                currencyPair,
                "",
                null);
            log.debug("marketOrder = {}", order);
            String orderId = tradeService.placeMarketOrder(order);

            return orderId;
        } catch (IOException e) {
            log.error(String.format("%s exchange purchase coins failed dvchain"), e);
        }
        return null;
    }
}
