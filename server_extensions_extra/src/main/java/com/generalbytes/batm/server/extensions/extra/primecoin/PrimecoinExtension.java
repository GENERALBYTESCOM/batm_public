package com.generalbytes.batm.server.extensions.extra.primecoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.exceptions.helper.ExceptionHelper;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coingecko.CoinGeckoRateSource;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;
import com.generalbytes.batm.server.extensions.extra.primecoin.wallets.primecoind.PrimecoinRPCWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class PrimecoinExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(PrimecoinExtension.class);
    @Override
    public String getName() {
        return "BATM Primecoin extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            String walletType = null;
            try {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                walletType = st.nextToken();

                if ("primecoind".equalsIgnoreCase(walletType)) {
                    //"primecoind:protocol:user:password:ip:port:accountname"

                    String protocol = st.nextToken();
                    String username = st.nextToken();
                    String password = st.nextToken();
                    String hostname = st.nextToken();
                    String port = st.nextToken();
                    String accountName = "";
                    if (st.hasMoreTokens()) {
                        accountName = st.nextToken();
                    }


                    if (protocol != null && username != null && password != null && hostname != null && port != null && accountName != null) {
                        String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                        return new PrimecoinRPCWallet(rpcURL, accountName);
                    }
                }
                if ("primedemo".equalsIgnoreCase(walletType)) {

                    String fiatCurrency = st.nextToken();
                    String walletAddress = "";
                    if (st.hasMoreTokens()) {
                        walletAddress = st.nextToken();
                    }

                    if (fiatCurrency != null && walletAddress != null) {
                        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.XPM.getCode(), walletAddress);
                    }
                }
            } catch (Exception e) {
                String serialNumber = ExceptionHelper.findSerialNumberInStackTrace();
                log.warn("createWallet failed for prefix: {}, on terminal with serial number: {}", walletType, serialNumber);
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.XPM.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new PrimecoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            String rsType = null;
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                rsType = st.nextToken();

                if ("coingecko".equalsIgnoreCase(rsType)) {
                    String preferredFiatCurrency = st.hasMoreTokens() ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                    return new CoinGeckoRateSource(preferredFiatCurrency);
                } else if ("coinmarketcap".equalsIgnoreCase(rsType)) {
                    String preferredFiatCurrency = FiatCurrency.USD.getCode();
                    String apiKey = null;
                    if (st.hasMoreTokens()) {
                        preferredFiatCurrency = st.nextToken().toUpperCase();
                    }
                    if (st.hasMoreTokens()) {
                        apiKey = st.nextToken();
                    }
                    return new CoinmarketcapRateSource(apiKey, preferredFiatCurrency);
                }
            } catch (Exception e) {
                String serialNumber = ExceptionHelper.findSerialNumberInStackTrace();
                log.warn("createRateSource failed for prefix: {}, on terminal with serial number: {}", rsType, serialNumber);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.XPM.getCode());
        return result;
    }
}
