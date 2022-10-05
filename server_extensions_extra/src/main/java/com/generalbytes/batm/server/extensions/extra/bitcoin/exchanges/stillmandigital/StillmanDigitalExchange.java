package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.RowBalanceByAssetResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.Ticker;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.WithdrawAck;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.WithdrawRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.WithdrawalAddress;
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.List;
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
                    // crypto is interesting in terms on how much client can withdraw
                    return assetData.total.add(assetData.netOpenPosition);
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
                    // fiat is interesting in terms on how much client can spent to buy crypto, due this just FREE
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
        try {
            List<WithdrawalAddress> withdrawalAddresses = api.getWithdrawalAddresses(cryptoCurrency);
            for (WithdrawalAddress wa : withdrawalAddresses) {
                if (wa.approved && Objects.equals(wa.walletAddress, destinationAddress)) {
                    WithdrawRequest withdrawRequest = new WithdrawRequest();
                    withdrawRequest.destinationId = wa.id;
                    withdrawRequest.amount = amount;
                    withdrawRequest.asset = cryptoCurrency;
                    List<WithdrawAck> withdrawAcks = api.initiateWithdraw(withdrawRequest);
                    if (withdrawAcks == null || withdrawAcks.isEmpty())
                        return null;
                    return withdrawAcks.get(0).id;
                }
            }
        } catch (IOException e) {
            log.error("Error during withdraw", e);
        }
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
        try {
            Ticker ticker = api.getTicker(cryptoCurrency + fiatCurrency);
            if (ticker != null) {
                return ticker.ap;
            }
        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        try {
            Ticker ticker = api.getTicker(cryptoCurrency + fiatCurrency);
            if (ticker != null) {
                return ticker.bp;
            }
        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            Ticker ticker = api.getTicker(cryptoCurrency + fiatCurrency);
            if (ticker != null && ticker.as.compareTo(cryptoAmount) >= 0) {
                return ticker.ap;
            }
        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        try {
            Ticker ticker = api.getTicker(cryptoCurrency + fiatCurrency);
            if (ticker != null && ticker.bs.compareTo(cryptoAmount) >= 0) {
                return ticker.bp;
            }
        } catch (IOException e) {
            log.error("Error", e);
        }
        return null;
    }
}
