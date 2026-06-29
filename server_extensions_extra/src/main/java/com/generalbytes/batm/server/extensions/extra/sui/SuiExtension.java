package com.generalbytes.batm.server.extensions.extra.sui;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;

import java.math.BigDecimal;
import java.util.Set;
import java.util.StringTokenizer;

public class SuiExtension extends AbstractExtension {

    private static final Set<String> SUPPORTED = Set.of(CryptoCurrency.SUI.getCode());
    private static final Set<ICryptoCurrencyDefinition> DEFINITIONS = Set.of(new SuiDefinition());

    @Override
    public String getName() {
        return "BATM SUI extension";
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return SUPPORTED;
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return DEFINITIONS;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.SUI.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new SuiAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CryptoCurrency.SUI.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new SuiWalletGenerator(ctx);
        }
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin == null || walletLogin.isBlank()) {
            return null;
        }
        try {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();

            if ("suirpc".equalsIgnoreCase(walletType)) {
                // suirpc:http://HOST:9000:BASE64_PRIVATE_KEY
                String rpcUrl = st.nextToken();
                String privateKey = st.nextToken();
                return new SuiWallet(rpcUrl, privateKey);
            } else if ("suidemo".equalsIgnoreCase(walletType)) {
                // suidemo:USD:0xADDRESS
                String fiatCurrency = st.nextToken();
                String walletAddress = st.nextToken();
                return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.SUI.getCode(), walletAddress);
            }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createWallet", getClass().getSimpleName(), walletLogin, e);
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin == null || sourceLogin.isBlank()) {
            return null;
        }
        try {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String rsType = st.nextToken();
            if ("suifix".equalsIgnoreCase(rsType)) {
                BigDecimal rate = st.hasMoreTokens() ? new BigDecimal(st.nextToken()) : BigDecimal.ZERO;
                String fiat = st.hasMoreTokens() ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                return new FixPriceRateSource(rate, fiat);
            }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createRateSource", getClass().getSimpleName(), sourceLogin, e);
        }
        return null;
    }
}
