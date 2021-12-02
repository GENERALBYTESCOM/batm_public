package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bittrex;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bittrex.dto.BittrexTickerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BittrexRateSource implements IRateSourceAdvanced {

    private static final Logger log = LoggerFactory.getLogger(BittrexRateSource.class);

    private static final String BITTREX_BASE_URL = "https://api.bittrex.com";

    private static final Set<String> SUPPORTED_CRYPTO_CURRENCIES = new HashSet<>();
    private static final Set<String> SUPPORTED_FIAT_CURRENCIES = new HashSet<>();

    private final Bittrex api;
    private final String preferredCryptoCurrency;

    static {
        SUPPORTED_CRYPTO_CURRENCIES.add(CryptoCurrency.BTC.getCode());
        SUPPORTED_CRYPTO_CURRENCIES.add(CryptoCurrency.BCH.getCode());
        SUPPORTED_CRYPTO_CURRENCIES.add(CryptoCurrency.ETH.getCode());
        SUPPORTED_CRYPTO_CURRENCIES.add(CryptoCurrency.LTC.getCode());
        SUPPORTED_CRYPTO_CURRENCIES.add(CryptoCurrency.BAY.getCode());
        SUPPORTED_CRYPTO_CURRENCIES.add(CryptoCurrency.BTBS.getCode());

        SUPPORTED_FIAT_CURRENCIES.add(CryptoCurrency.USDT.getCode());
    }

    public BittrexRateSource(String preferredCryptoCurrency) {
        this.preferredCryptoCurrency = preferredCryptoCurrency;
        this.api = RestProxyFactory.createProxy(Bittrex.class, BITTREX_BASE_URL);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return SUPPORTED_CRYPTO_CURRENCIES;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return SUPPORTED_FIAT_CURRENCIES;
    }


    @Override
    public String getPreferredFiatCurrency() {
        return preferredCryptoCurrency;
    }

    @Override
    public BigDecimal getExchangeRateForBuy(String cryptoCurrency, String fiatCurrency) {
        if (areCurrenciesSupported(cryptoCurrency, fiatCurrency)) {
            BittrexTickerDto ticker = getTicker(cryptoCurrency, fiatCurrency);
            return ticker.getAskRate();
        }
        return null;
    }

    @Override
    public BigDecimal getExchangeRateForSell(String cryptoCurrency, String fiatCurrency) {
        BittrexTickerDto ticker = getTicker(cryptoCurrency, fiatCurrency);
        return ticker.getBidRate();
    }

    @Override
    public BigDecimal calculateBuyPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        BigDecimal exchangeRateForBuy = getExchangeRateForBuy(cryptoCurrency, fiatCurrency);
        log.debug("Called Bittrex exchange for BUY rate: {}{} = {}", cryptoCurrency, fiatCurrency, exchangeRateForBuy);
        return exchangeRateForBuy != null ? exchangeRateForBuy.multiply(cryptoAmount) : null;
    }

    @Override
    public BigDecimal calculateSellPrice(String cryptoCurrency, String fiatCurrency, BigDecimal cryptoAmount) {
        BigDecimal exchangeRateForBuy = getExchangeRateForSell(cryptoCurrency, fiatCurrency);
        log.debug("Called Bittrex exchange for SELL rate: {}-{} = {}", cryptoCurrency, fiatCurrency, exchangeRateForBuy);
        return exchangeRateForBuy != null ? exchangeRateForBuy.multiply(cryptoAmount) : null;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        BittrexTickerDto ticker = getTicker(cryptoCurrency, fiatCurrency);
        BigDecimal lastTradeRate = ticker.getLastTradeRate();
        log.debug("Called bittrex exchange for rate: {}-{} = {} ", cryptoCurrency, fiatCurrency, lastTradeRate);
        return lastTradeRate;
    }

    private BittrexTickerDto getTicker(String cryptoCurrency, String fiatCurrency) {
        String marketSymbol = cryptoCurrency + "-" + fiatCurrency;
        return api.getTicker(marketSymbol);
    }

    private boolean areCurrenciesSupported(String cryptoCurrency, String fiatCurrency) {
        if (getCryptoCurrencies().contains(cryptoCurrency) && getFiatCurrencies().contains(fiatCurrency)) {
            return true;
        }
        log.warn("Bittrex support only {} crypto currencies and {} fiat currencies", getCryptoCurrencies(), getFiatCurrencies());
        return false;
    }
}
