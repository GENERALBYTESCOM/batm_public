package com.generalbytes.batm.server.extensions.extra.nubits;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.nubits.NubitsAddressValidator;
import com.generalbytes.batm.server.extensions.extra.nubits.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.nubits.wallets.nud.NubitsRPCWallet;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by woolly_sammoth on 11/12/14.
 */
public class NubitsExtension implements IExtension{
    @Override
    public String getName() { return "BATM NuBits extension"; }

    @Override
    public IExchange createExchange(String exchangeLogin) { return null; }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("nud".equalsIgnoreCase(walletType)) {
                //"nud:protocol:user:password:ip:port:accountname"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                String port = st.nextToken();
                String accountName ="";
                if (st.hasMoreTokens()) {
                    accountName = st.nextToken();
                }


                if (protocol != null && username != null && password != null && hostname !=null && port != null && accountName != null) {
                    String rpcURL = protocol +"://" + username +":" + password + "@" + hostname +":" + port;
                    return new NubitsRPCWallet(rpcURL,accountName);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (ICurrencies.NBT.equalsIgnoreCase(cryptoCurrency)) {
            return new NubitsAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ("nbtfix".equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                String preferedFiatCurrency = ICurrencies.USD;
                if (st.hasMoreTokens()) {
                    preferedFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate,preferedFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        return null; //no payment processors available
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.NBT);
        return result;
    }

    @Override
    public Set<String> getSupportedWatchListsNames() {
        return null;
    }

    @Override
    public IWatchList getWatchList(String name) {
        return null;
    }
}
