package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.dto.account.AssetDetail;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.Filter;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.Symbol;
import org.knowm.xchange.binance.service.BinanceAccountService;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.WalletHealth;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.utils.AuthUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

//TODO: BATM-2471 Remove this class and use only org.knowm.xchange.binance.BinanceExchange
public class CustomBinanceExchange extends BinanceExchange {
    private BinanceExchangeInfo exchangeInfo;

    @Override
    public ExchangeSpecification getDefaultExchangeSpecification() {

        ExchangeSpecification spec = new ExchangeSpecification(this.getClass());
        spec.setSslUri("https://api.binance.com");
        spec.setHost("www.binance.com");
        spec.setPort(80);
        spec.setExchangeName("Binance");
        spec.setExchangeDescription("Binance Exchange.");
        AuthUtils.setApiAndSecretKey(spec, "binance");
        return spec;
    }

    @Override
    public void remoteInit() {

        try {
            // populate currency pair keys only, exchange does not provide any other metadata for download
            Map<CurrencyPair, CurrencyPairMetaData> currencyPairs = exchangeMetaData.getCurrencyPairs();
            Map<Currency, CurrencyMetaData> currencies = exchangeMetaData.getCurrencies();

            BinanceMarketDataService marketDataService =
                (BinanceMarketDataService) this.marketDataService;
            exchangeInfo = marketDataService.getExchangeInfo();
            Symbol[] symbols = exchangeInfo.getSymbols();

            BinanceAccountService accountService = (BinanceAccountService) getAccountService();
            Map<String, AssetDetail> assetDetailMap = null;
            if (!usingSandbox() && isAuthenticated()) {
                assetDetailMap = accountService.getAssetDetails(); // not available in sndbox
            }
            // Clear all hardcoded currencies when loading dynamically from exchange.
            if (assetDetailMap != null) {
                currencies.clear();
            }
            for (Symbol symbol : symbols) {
                if (symbol.getStatus().equals("TRADING")) { // Symbols which are trading
                    int basePrecision = Integer.parseInt(symbol.getBaseAssetPrecision());
                    int counterPrecision = Integer.parseInt(symbol.getQuotePrecision());
                    int pairPrecision = 8;
                    int amountPrecision = 8;

                    BigDecimal minQty = null;
                    BigDecimal maxQty = null;
                    BigDecimal stepSize = null;

                    BigDecimal counterMinQty = null;
                    BigDecimal counterMaxQty = null;

                    Filter[] filters = symbol.getFilters();

                    CurrencyPair currentCurrencyPair =
                        new CurrencyPair(symbol.getBaseAsset(), symbol.getQuoteAsset());

                    for (Filter filter : filters) {
                        if (filter.getFilterType().equals("PRICE_FILTER")) {
                            pairPrecision = Math.min(pairPrecision, numberOfDecimals(filter.getTickSize()));
                            counterMaxQty = new BigDecimal(filter.getMaxPrice()).stripTrailingZeros();
                        } else if (filter.getFilterType().equals("LOT_SIZE")) {
                            amountPrecision = Math.min(amountPrecision, numberOfDecimals(filter.getStepSize()));
                            minQty = new BigDecimal(filter.getMinQty()).stripTrailingZeros();
                            maxQty = new BigDecimal(filter.getMaxQty()).stripTrailingZeros();
                            stepSize = new BigDecimal(filter.getStepSize()).stripTrailingZeros();
                        } else if (filter.getFilterType().equals("MIN_NOTIONAL")) {
                            counterMinQty = new BigDecimal(filter.getMinNotional()).stripTrailingZeros();
                        }
                    }

                    boolean marketOrderAllowed = Arrays.asList(symbol.getOrderTypes()).contains("MARKET");
                    currencyPairs.put(
                        currentCurrencyPair,
                        new CurrencyPairMetaData(
                            new BigDecimal("0.1"), // Trading fee at Binance is 0.1 %
                            minQty, // Min amount
                            maxQty, // Max amount
                            counterMinQty,
                            counterMaxQty,
                            amountPrecision, // base precision
                            pairPrecision, // counter precision
                            null,
                            null, /* TODO get fee tiers, although this is not necessary now
                        because their API returns current fee directly */
                            stepSize,
                            null,
                            marketOrderAllowed));

                    Currency baseCurrency = currentCurrencyPair.base;
                    CurrencyMetaData baseCurrencyMetaData =
                        adaptCurrencyMetaData(
                            currencies, baseCurrency, assetDetailMap, basePrecision);
                    currencies.put(baseCurrency, baseCurrencyMetaData);

                    Currency counterCurrency = currentCurrencyPair.counter;
                    CurrencyMetaData counterCurrencyMetaData =
                        adaptCurrencyMetaData(
                            currencies, counterCurrency, assetDetailMap, counterPrecision);
                    currencies.put(counterCurrency, counterCurrencyMetaData);
                }
            }
        } catch (Exception e) {
            throw new ExchangeException("Failed to initialize: " + e.getMessage(), e);
        }
    }

    //actual fix of the error
    private boolean isAuthenticated() {
        return exchangeSpecification != null
            && exchangeSpecification.getApiKey() != null
            && exchangeSpecification.getSecretKey() != null;
    }

    private int numberOfDecimals(String value) {
        return new BigDecimal(value).stripTrailingZeros().scale();
    }

    //Same implementation as: org.knowm.xchange.binance.BinanceAdapters.adaptCurrencyMetaData
    private CurrencyMetaData adaptCurrencyMetaData(
        Map<Currency, CurrencyMetaData> currencies,
        Currency currency,
        Map<String, AssetDetail> assetDetailMap,
        int precision) {
        if (assetDetailMap != null) {
            AssetDetail asset = assetDetailMap.get(currency.getCurrencyCode());
            if (asset != null) {
                BigDecimal withdrawalFee = asset.getWithdrawFee().stripTrailingZeros();
                BigDecimal minWithdrawalAmount =
                    new BigDecimal(asset.getMinWithdrawAmount()).stripTrailingZeros();
                WalletHealth walletHealth =
                    getWalletHealth(asset.isDepositStatus(), asset.isWithdrawStatus());
                return new CurrencyMetaData(precision, withdrawalFee, minWithdrawalAmount, walletHealth);
            }
        }

        BigDecimal withdrawalFee = null;
        BigDecimal minWithdrawalAmount = null;
        if (currencies.containsKey(currency)) {
            CurrencyMetaData currencyMetaData = currencies.get(currency);
            withdrawalFee = currencyMetaData.getWithdrawalFee();
            minWithdrawalAmount = currencyMetaData.getMinWithdrawalAmount();
        }
        return new CurrencyMetaData(precision, withdrawalFee, minWithdrawalAmount);
    }

    private static WalletHealth getWalletHealth(boolean depositEnabled, boolean withdrawEnabled) {
        if (depositEnabled && withdrawEnabled) {
            return WalletHealth.ONLINE;
        }
        if (!depositEnabled && withdrawEnabled) {
            return WalletHealth.DEPOSITS_DISABLED;
        }
        if (depositEnabled) {
            return WalletHealth.WITHDRAWALS_DISABLED;
        }
        return WalletHealth.OFFLINE;
    }
}
