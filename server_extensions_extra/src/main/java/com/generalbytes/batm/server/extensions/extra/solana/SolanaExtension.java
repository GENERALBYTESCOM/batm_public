package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Extension for support Solana.
 *
 * @see <a href="https://solana.com">Solana official web</a>
 */
public class SolanaExtension extends AbstractExtension {

    @Override
    public String getName() {
        return "BATM Solana extension";
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.SOL.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new SolanaAddressValidator();
        }

        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (StringUtils.trimToNull(walletLogin) == null) {
            return null;
        }

        try {
            StringTokenizer tokenizer = new StringTokenizer(walletLogin, ":");
            if (tokenizer.countTokens() != 3) {
                return null;
            }

            String walletType = tokenizer.nextToken();
            if ("soldemo".equalsIgnoreCase(walletType)) {
                String fiatCurrency = tokenizer.nextToken();
                String walletAddress = tokenizer.nextToken();

                return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.SOL.getCode(), walletAddress);
            }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createWallet", getClass().getSimpleName(), walletLogin, e);
        }

        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (StringUtils.trimToNull(sourceLogin) == null) {
            return null;
        }

        try {
            StringTokenizer tokenizer = new StringTokenizer(sourceLogin, ":");
            if (tokenizer.countTokens() == 0) {
                return null;
            }

            String rateSourceType = tokenizer.nextToken();
            if ("solfix".equalsIgnoreCase(rateSourceType)) {
                BigDecimal rate = getRate(tokenizer);
                String preferredFiatCurrency = getPreferredFiatCurrency(tokenizer);

                return new FixPriceRateSource(rate, preferredFiatCurrency);
            }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createRateSource", getClass().getSimpleName(), sourceLogin, e);
        }

        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return Set.of(CryptoCurrency.SOL.getCode());
    }

    private BigDecimal getRate(StringTokenizer tokenizer) {
        try {
            return new BigDecimal(tokenizer.nextToken());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String getPreferredFiatCurrency(StringTokenizer tokenizer) {
        if (tokenizer.hasMoreTokens()) {
            return tokenizer.nextToken().toUpperCase();
        }

        return FiatCurrency.USD.getCode();
    }

}
