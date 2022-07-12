/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccountResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddressesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPaginatedItem;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPaginatedResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendResponse;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by b00lean on 23.7.17.
 */

public class CoinbaseWalletV2 implements IWallet {
    private static final Logger log = LoggerFactory.getLogger("batm.master.CoinbaseWallet2");
    private static final ImmutableMap<String, String> supportedCryptoCurrencyToNetworkMap = new ImmutableMap.Builder<String, String>()
            .put(CryptoCurrency.BTC.getCode(), "bitcoin")
            .put(CryptoCurrency.LTC.getCode(), "litecoin")
            .put(CryptoCurrency.ETH.getCode(), "ethereum")
            .put(CryptoCurrency.BAT.getCode(), "ethereum")
            .put(CryptoCurrency.DAI.getCode(), "ethereum")
            .put(CryptoCurrency.BET_VERSE.getCode(), "ethereum")
            .put(CryptoCurrency.BET_VERSE_ICO.getCode(), "ethereum")
            .put(CryptoCurrency.BIZZ.getCode(), "ethereum")
            .put(CryptoCurrency.USDT.getCode(), "ethereum")
            .put(CryptoCurrency.ETC.getCode(), "ethereum_classic")
            .put(CryptoCurrency.BCH.getCode(), "bitcoincash")
            .put(CryptoCurrency.DASH.getCode(), "dash")
            .put(CryptoCurrency.XRP.getCode(), "ripple")
            .build();

    protected static final String API_VERSION = "2016-07-23";
    private String preferredCryptoCurrency;
    protected String apiKey;
    protected String apiSecret;
    protected ICoinbaseV2API api;
    protected String accountName;
    protected Map<String, String> accountIds = new HashMap<>();

    public CoinbaseWalletV2(String apiKey, String apiSecret, String accountName) {
        this.accountName = accountName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        ClientConfig config = new ClientConfig();
        config.setIgnoreHttpErrorCodes(true);
        api = RestProxyFactory.createProxy(ICoinbaseV2API.class, "https://api.coinbase.com", config);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return supportedCryptoCurrencyToNetworkMap.keySet();
    }

    @Override
    public String getPreferredCryptoCurrency() {
        if (preferredCryptoCurrency == null) {
            return CryptoCurrency.BTC.getCode();
        }
        return preferredCryptoCurrency;
    }

    protected synchronized void initIfNeeded(String cryptoCurrency) {
        String accountId = accountIds.get(cryptoCurrency);
        if (accountId == null) {
            accountId = getAccountId(accountName, cryptoCurrency);
            accountIds.put(cryptoCurrency, accountId);
        }
    }

    /**
     * @return all accounts from the API using pagination
     */
    private List<CBAccount> getAccounts() {
        return paginate(startingAfter -> {
            long timeStamp = getTimestamp();
            return api.getAccounts(apiKey, API_VERSION, CBDigest.createInstance(apiSecret, timeStamp), timeStamp, 100,
                    startingAfter);
        });
    }

    private String getAccountId(String accountName, String cryptoCurrency) {
        List<CBAccount> accounts = getAccounts();
        if (accountName != null) {
            for (CBAccount cbAccount : accounts) {
                if (accountName.equalsIgnoreCase(cbAccount.getName())) {
                    if (cryptoCurrency.equalsIgnoreCase(cbAccount.getCurrency())) {
                        preferredCryptoCurrency = cbAccount.getCurrency();
                        return cbAccount.getId();
                    }
                }
            }
        } else {
            for (CBAccount cbAccount : accounts) {
                if (cbAccount.isPrimary()) {
                    if (cryptoCurrency.equalsIgnoreCase(cbAccount.getCurrency())) {
                        preferredCryptoCurrency = cbAccount.getCurrency();
                        return cbAccount.getId();
                    }
                }
            }
        }
        for (CBAccount cbAccount : accounts) {
            if (cryptoCurrency.equalsIgnoreCase(cbAccount.getCurrency())) {
                preferredCryptoCurrency = cbAccount.getCurrency();
                return cbAccount.getId();
            }
        }

        CBAccount cbAccount = accounts.get(0);
        preferredCryptoCurrency = cbAccount.getCurrency();
        return cbAccount.getId();
    }

    protected long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only " + Arrays.toString(getCryptoCurrencies().toArray()) + " not "
                    + cryptoCurrency);
            return null;
        }
        initIfNeeded(cryptoCurrency);
        long timeStamp = getTimestamp();
        CBAddressesResponse addressesResponse = api.getAccountAddresses(apiKey, API_VERSION,
                CBDigest.createInstance(apiSecret, timeStamp), timeStamp, accountIds.get(cryptoCurrency));
        if (addressesResponse != null && addressesResponse.getData() != null
                && !addressesResponse.getData().isEmpty()) {
            List<CBAddress> addresses = addressesResponse.getData();
            String network = getNetworkName(cryptoCurrency);
            CBAddress address = null;
            if (network != null) {
                for (int i = 0; i < addresses.size(); i++) {
                    CBAddress a = addresses.get(i);
                    if (a.getNetwork().equalsIgnoreCase(network)) {
                        address = a;
                    }
                }
            }
            if (address == null) {
                address = addressesResponse.getData().get(0);
            }
            return address.getAddress();
        }
        if (addressesResponse != null && addressesResponse.getErrors() != null) {
            log.error("getCryptoAddress - " + addressesResponse.getErrorMessages());
        }
        return null;
    }

    protected String getNetworkName(String cryptoCurrency) {
        return supportedCryptoCurrencyToNetworkMap.get(cryptoCurrency);
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only " + Arrays.toString(getCryptoCurrencies().toArray()) + " not "
                    + cryptoCurrency);
            return null;
        }
        initIfNeeded(cryptoCurrency);
        long timeStamp = getTimestamp();
        CBAccountResponse accountResponse = api.getAccount(apiKey, API_VERSION,
                CBDigest.createInstance(apiSecret, timeStamp), timeStamp, accountIds.get(cryptoCurrency));
        if (accountResponse != null && accountResponse.getData() != null
                && cryptoCurrency.equalsIgnoreCase(accountResponse.getData().getBalance().getCurrency())) {
            return accountResponse.getData().getBalance().getAmount().stripTrailingZeros();
        }
        if (accountResponse != null && accountResponse.getErrors() != null) {
            log.error("getCryptoBalance - " + accountResponse.getErrorMessages());
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only " + Arrays.toString(getCryptoCurrencies().toArray()) + " not "
                    + cryptoCurrency);
            return null;
        }
        initIfNeeded(cryptoCurrency);
        long timeStamp = getTimestamp();
        String destinationTag = null;
        if (cryptoCurrency.equals(CryptoCurrency.XRP.getCode())) {
            String[] addressParts = destinationAddress.split(":");
            if (addressParts.length == 2) {
                destinationAddress = addressParts[0];
                destinationTag = addressParts[1];
            }
        }
        if (CryptoCurrency.USDT.getCode().equals(cryptoCurrency)) {
            amount = amount.setScale(6, RoundingMode.FLOOR);
        }
        log.info("sending {} {} to {}", amount, cryptoCurrency, destinationAddress);
        CBSendRequest sendRequest = new CBSendRequest("send", destinationAddress,
                amount.stripTrailingZeros().toPlainString(), cryptoCurrency, description, description, destinationTag); // note
                                                                                                                        // that
                                                                                                                        // description
                                                                                                                        // is
                                                                                                                        // here
                                                                                                                        // used
                                                                                                                        // as
                                                                                                                        // unique
                                                                                                                        // token
                                                                                                                        // as
                                                                                                                        // reply
                                                                                                                        // protection
        CBSendResponse response = api.send(apiKey, API_VERSION, CBDigest.createInstance(apiSecret, timeStamp),
                timeStamp, accountIds.get(cryptoCurrency), sendRequest);
        if (response != null && response.getData() != null) {
            return response.getData().getId();
        }
        if (response != null && response.getErrors() != null) {
            log.error("sendCoins - " + response.getErrorMessages());
        }
        return null; // some error happened
    }

    /**
     * Calls the function with the id of the last item as the "startingAfter"
     * parameter until all pages are loaded
     */
    protected <T extends CBPaginatedItem> List<T> paginate(Function<String, CBPaginatedResponse<T>> function) {
        LinkedList<T> items = new LinkedList<>();
        String startingAfter = null; // start pagination from the beginning
        do {
            log.trace("Getting items, startingAfter: {}", startingAfter);
            CBPaginatedResponse<T> response = function.apply(startingAfter);

            if (response.getWarnings() != null) {
                log.warn(response.getWarnings().toString());
            }

            if (response.getErrors() != null) {
                throw new IllegalStateException(response.getErrorMessages());
            }

            startingAfter = null;
            if (response.getData() != null && response.getData().size() > 0) {
                items.addAll(response.getData());
                if (response.getPagination() != null && response.getPagination().getNext_uri() != null) {
                    startingAfter = items.getLast().getId();
                }
            }
        } while (startingAfter != null);
        return items;
    }

    // public static void main(String[] args) {
    // ServerUtil.setLoggerLevel("si.mazi.rescu","trace");
    // String cryptoCurrency = CryptoCurrency.BTC.getCode();
    // CoinbaseWalletV2 w = new
    // CoinbaseWalletV2("LGcOlxy5UNGXGcKp","8bTu2aKO9VsRHNaK7fvf6Y5dyb87GaoV",null);
    // String cryptoAddress = w.getCryptoAddress(cryptoCurrency);
    // log.info("cryptoAddress = " + cryptoAddress);
    // BigDecimal cryptoBalance = w.getCryptoBalance(cryptoCurrency);
    // log.info("cryptoBalance = " + cryptoBalance);
    // String result = w.sendCoins("1Nqip1Qc6EP88jwNrVwFy2CiXAuzPhdPgG", new
    // BigDecimal("0.0005"), cryptoCurrency, "RXIDS");
    // log.info("result = " + result);
    // }
}
