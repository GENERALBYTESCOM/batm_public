package com.generalbytes.batm.server.extensions.extra.zcoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;
import com.generalbytes.batm.server.extensions.extra.zcoin.sources.FixPriceRateSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class ZcoinExtension extends AbstractExtension {
    @Override
    public String getName() { return "BATM Zcoin extension"; }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();

            if("zcoind".equalsIgnoreCase(walletType)) {
                String protocol     = st.nextToken();
                String username     = st.nextToken();
                String password     = st.nextToken();
                String hostname     = st.nextToken();
                String port         = st.nextToken();
                String accountName  = "";
                if (st.hasMoreTokens()) {
                    accountName = st.nextToken();
                }

                if (protocol != null &&
                    username != null &&
                    password != null &&
                    hostname !=null &&
                    port != null &&
                    accountName != null) {
                    String rpcURL = protocol + "://" + username + ":" + password
                        + "@" + hostname + ":" + port;
                }
            }
        }
        return  null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (Currencies.XZC.equalsIgnoreCase(cryptoCurrency)) {
            return new ZcoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String exchangeType = st.nextToken();
            if ("coinmarketcap".equalsIgnoreCase(exchangeType)) {
                String preferredFiatCurrency = Currencies.USD;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new CoinmarketcapRateSource(preferredFiatCurrency);
            }else if ("zcoinfix".equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    }catch (Throwable e) {

                    }
                }
                String preferredFiatCurrency = Currencies.USD;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate, preferredFiatCurrency);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(Currencies.XZC);
        return result;
    }
}
