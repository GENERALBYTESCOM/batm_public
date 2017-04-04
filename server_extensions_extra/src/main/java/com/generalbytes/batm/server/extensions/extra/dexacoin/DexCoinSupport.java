package com.generalbytes.batm.server.extensions.extra.dexacoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class DexCoinSupport implements IExchange, IWallet, IRateSource, IExtension, ICryptoAddressValidator {

    private static final String CRYPTO_CURRENCY = ICurrencies.DEX;
    private static final BigDecimal WALLET_BALANCE = new BigDecimal("1000000");
    private static final BigDecimal EXCHANGE_BALANCE = new BigDecimal("2000000");
    private static final String WALLET_ADDRESS = CRYPTO_CURRENCY.substring(1) + "GnubsaWBQf6J2TTvNLF5xLkMydhTjWsQi";

    private String preferredFiatCurrency;
    private BigDecimal rate = BigDecimal.ONE;

    public DexCoinSupport() {
    }

    public DexCoinSupport(String preferredFiatCurrency, BigDecimal rate) {
        this.rate = rate;
        this.preferredFiatCurrency = preferredFiatCurrency;
        if (ICurrencies.EUR.equalsIgnoreCase(preferredFiatCurrency)) {
            this.preferredFiatCurrency = ICurrencies.EUR;
        }
        if (ICurrencies.USD.equalsIgnoreCase(preferredFiatCurrency)) {
            this.preferredFiatCurrency = ICurrencies.USD;
        }
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String cashCurrency) {
        if (CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return rate;
        }
        return null;
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency.equalsIgnoreCase(CRYPTO_CURRENCY) && fiatCurrencyToUse.equalsIgnoreCase(preferredFiatCurrency)) {
            return "true";
        }else{
            return null;
        }
    }

    @Override
    public String getCryptoAddress( String cryptoCurrency) {
        if (CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return WALLET_ADDRESS;
        }else{
            return null;
        }
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CRYPTO_CURRENCY);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(preferredFiatCurrency);
        return result;
    }

    @Override
    public String getPreferredFiatCurrency() {
        return preferredFiatCurrency;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CRYPTO_CURRENCY;
    }


    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return WALLET_BALANCE;
        }else{
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getFiatBalance(String cashCurrency) {
        if (preferredFiatCurrency.equalsIgnoreCase(cashCurrency)) {
            return EXCHANGE_BALANCE;
        }else{
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return "txt_id";
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return WALLET_ADDRESS;
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        return "tx_sell_id";
    }

    @Override
    public String getName() {
        return "BATM " + CRYPTO_CURRENCY + " extension";
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        if (exchangeLogin !=null && !exchangeLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(exchangeLogin,":");
            String exchangeType = st.nextToken();

            if ((CRYPTO_CURRENCY.toLowerCase()+ "_exchange").equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                String preferredFiatCurrency = ICurrencies.USD;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new DexCoinSupport(preferredFiatCurrency,rate);
            }
        }
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ((CRYPTO_CURRENCY.toLowerCase()+ "_wallet").equalsIgnoreCase(walletType)) {
                return new DexCoinSupport();
            }
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ((CRYPTO_CURRENCY.toLowerCase() + "_fix").equalsIgnoreCase(exchangeType)) {
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
                return new DexCoinSupport(preferedFiatCurrency,rate);
            }
        }
        return null;
    }


    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return new DexCoinSupport();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return null;
    }


    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        return null; //no payment processors available
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CRYPTO_CURRENCY);
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

    @Override
    public boolean isAddressValid(String address) {
        boolean result = isCryptoAddressValid(address);
        if (!result) {
            result = isPaperWalletSupported() && ExtensionsUtil.isValidEmailAddress(address);
        }
        return result;
    }

    private boolean isCryptoAddressValid(String address) {
        if (address.startsWith(CRYPTO_CURRENCY.substring(1))) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }


}
