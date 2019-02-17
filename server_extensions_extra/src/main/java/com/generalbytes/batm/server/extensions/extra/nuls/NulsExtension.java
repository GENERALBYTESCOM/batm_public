package com.generalbytes.batm.server.extensions.extra.nuls;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.nuls.source.binance.BinanceRateSource;
import com.generalbytes.batm.server.extensions.extra.nuls.wallet.binance.NulsWallet;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author naveen
 */
public class NulsExtension extends AbstractExtension {

    @Override
    public String getName() {
        return "BATM NULS extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("nulsBinance".equalsIgnoreCase(walletType)) {
                String address = st.nextToken();
                String binanceApiKey = st.nextToken();
                String binanceApiSecret = st.nextToken();
                if (address != null && binanceApiKey !=null && binanceApiSecret != null ) {
                    return new NulsWallet(address,binanceApiKey,binanceApiSecret);
                }
            }
            if ("nulsDemo".equalsIgnoreCase(walletType)) {
                String fiatCurrency = st.nextToken();
                String walletAddress = "";
                if (st.hasMoreTokens()) {
                    walletAddress = st.nextToken();
                }
                if (fiatCurrency != null && walletAddress != null) {
                    return new DummyExchangeAndWalletAndSource(fiatCurrency, Currencies.NULS, walletAddress);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (Currencies.NULS.equalsIgnoreCase(cryptoCurrency)) {
            return new NulsAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String exchangeType = st.nextToken();
            if ("nulsFix".equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                String preferredFiatCurrency = Currencies.USD;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate, preferredFiatCurrency);
            }
            else if ("binanceRateSource".equalsIgnoreCase(exchangeType)) {
                String preferredFiatCurrency = Currencies.USD;
                String coinMarketCapApiKey = null;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                if (st.hasMoreTokens()) {
                    coinMarketCapApiKey = st.nextToken();
                }
                return new BinanceRateSource(preferredFiatCurrency, coinMarketCapApiKey);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(Currencies.NULS);
        return result;
    }
}
