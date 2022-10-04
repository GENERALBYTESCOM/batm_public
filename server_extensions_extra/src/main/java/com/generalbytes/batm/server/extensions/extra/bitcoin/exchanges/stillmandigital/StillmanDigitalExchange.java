package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.RowBalanceByAssetResponse;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Set;

public class StillmanDigitalExchange implements IExchangeAdvanced, IRateSourceAdvanced {
    private static final Logger log = LoggerFactory.getLogger("batm.master.exchange.StillmanDigitalExchange");

    private final String preferredFiatCurrency;
    private final IStillmanDigitalAPI api;

    public StillmanDigitalExchange(String apiKey,
                                   String apiSecret,
                                   String preferredFiatCurrency) throws GeneralSecurityException {
        this.api = IStillmanDigitalAPI.create(apiKey, apiSecret);
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    // for tests only
    StillmanDigitalExchange(String apiKey,
                            String apiSecret,
                            String preferredFiatCurrency,
                            String baseUrl) throws GeneralSecurityException {
        this.api = IStillmanDigitalAPI.create(apiKey, apiSecret, baseUrl);
        this.preferredFiatCurrency = preferredFiatCurrency;
    }

    private static final Set<String> fiatCurrencies = ImmutableSet.of(
        FiatCurrency.USD.getCode());

    private static final Set<String> cryptoCurrencies = ImmutableSet.of(
        CryptoCurrency.BTC.getCode(),
        CryptoCurrency.ETH.getCode());

    @Override
    public Set<String> getCryptoCurrencies() {
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return fiatCurrencies;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        try {
            for (RowBalanceByAssetResponse assetData : api.getBalance().data) {
                if (Objects.equals(cryptoCurrency, assetData.asset)) {
                    return assetData.free;
                }
            }
        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        try {
            for (RowBalanceByAssetResponse assetData : api.getBalance().data) {
                if (Objects.equals(fiatCurrency, assetData.asset)) {
                    return assetData.free;
                }
            }
        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress,
                            BigDecimal amount, String cryptoCurrency, String description) {
        return null;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return null;
    }

    @Override
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return null;
    }

    @Override
    public ITask createSellCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        return null;
    }
}
