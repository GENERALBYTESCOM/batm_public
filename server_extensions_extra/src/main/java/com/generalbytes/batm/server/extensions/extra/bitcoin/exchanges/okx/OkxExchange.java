package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.okx;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.util.Set;

/**
 * Represents an implementation of the XChangeExchange for the OKX cryptocurrency exchange.
 * Provides functionality related to crypto and fiat currencies supported by OKX,
 * along with API key management and exchange specification handling.
 * <p>
 * API documentation: <a href="https://www.okx.com/docs-v5/en/#overview">API documentation</a>, using
 * <a href="https://github.com/knowm/XChange/tree/xchange-5.0.14/xchange-okex">XChange - OKEX</a> to connect to the API.
 */
public class OkxExchange extends XChangeExchange {

    private static final Set<String> CRYPTO_CURRENCIES = Set.of(
        CryptoCurrency.BTC.getCode(),
        CryptoCurrency.DOGE.getCode(),
        CryptoCurrency.ETH.getCode(),
        CryptoCurrency.LTC.getCode(),
        CryptoCurrency.USDT.getCode()
    );

    // based on https://www.okx.com/help/introducing-cash-deposits-and-withdrawals
    private static final Set<String> FIAT_CURRENCIES = Set.of(
        FiatCurrency.BRL.getCode(),
        FiatCurrency.EUR.getCode(),
        FiatCurrency.AUD.getCode(),
        FiatCurrency.AED.getCode(),
        FiatCurrency.SGD.getCode(),
        FiatCurrency.USD.getCode()
    );

    public OkxExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public OkxExchange(String preferredFiatCurrency, String key, String secret, String passphrase) {
        super(getSpecification(key, secret, passphrase), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.okex.OkexExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String key, String secret, String passphrase) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setApiKey(key);
        spec.setSecretKey(secret);
        spec.setExchangeSpecificParametersItem("passphrase", passphrase);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return CRYPTO_CURRENCIES;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return FIAT_CURRENCIES;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return result != null;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    public Wallet getWallet(AccountInfo accountInfo, String currency) {
        return accountInfo.getWallet("trading");
    }
}
