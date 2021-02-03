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
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddressesResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by b00lean on 23.7.17.
 */

public class CoinbaseWalletV2 implements IWallet {
    private static final Logger log = LoggerFactory.getLogger("batm.master.CoinbaseWallet2");

    protected static final String API_VERSION="2016-07-23";
    private String preferredCryptoCurrency;
    protected String apiKey;
    protected String apiSecret;
    protected ICoinbaseV2API api;
    protected String accountName;
    protected Map<String,String> accountIds = new HashMap<>();

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
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BAT.getCode());
        result.add(CryptoCurrency.DAI.getCode());
        result.add(CryptoCurrency.BIZZ.getCode());
        result.add(CryptoCurrency.BCH.getCode());
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.ETC.getCode());
        result.add(CryptoCurrency.ETH.getCode());
        result.add(CryptoCurrency.DASH.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.XRP.getCode());
        return result;
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
            accountId = getAccountId(accountName,cryptoCurrency);
            accountIds.put(cryptoCurrency,accountId);
        }
    }

    /**
     * @return all accounts from the API using pagination
     */
    private List<CBAccount> getAccounts() {
        LinkedList<CBAccount> accounts = new LinkedList<>();
        String startingAfter = null; // start pagination from the beginning
        do {
            log.debug("Getting accounts, startingAfter: {}", startingAfter);
            long timeStamp = getTimestamp();
            CBAccountsResponse accountsResponse = api.getAccounts(apiKey, API_VERSION, CBDigest.createInstance(apiSecret, timeStamp), timeStamp, 100, startingAfter);

            if (accountsResponse.getErrors() != null) {
                throw new IllegalStateException(accountsResponse.getErrorMessages());
            }
            if (accountsResponse.getWarnings() != null) {
                log.warn("getAccounts warning: {}", accountsResponse.getWarnings());
            }
            startingAfter = null;
            if (accountsResponse.getData() != null && accountsResponse.getData().size() > 0) {
                accounts.addAll(accountsResponse.getData());
                if (accountsResponse.getPagination() != null && accountsResponse.getPagination().getNext_uri() != null) {
                    startingAfter = accounts.getLast().getId();
                }
            }
        } while (startingAfter != null);
        return accounts;
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
        return System.currentTimeMillis()/1000;
    }


    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only " + Arrays.toString(getCryptoCurrencies().toArray()) + " not " + cryptoCurrency);
            return null;
        }
        initIfNeeded(cryptoCurrency);
        long timeStamp = getTimestamp();
        CBAddressesResponse addressesResponse = api.getAccountAddresses(apiKey, API_VERSION, CBDigest.createInstance(apiSecret, timeStamp), timeStamp, accountIds.get(cryptoCurrency));
        if (addressesResponse != null && addressesResponse.getData() != null && !addressesResponse.getData().isEmpty()) {
            List<CBAddress> addresses = addressesResponse.getData();
            String network  = getNetworkName(cryptoCurrency);
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
        if (CryptoCurrency.BTC.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return "bitcoin";
        }else if (CryptoCurrency.LTC.getCode().equalsIgnoreCase(cryptoCurrency)){
            return "litecoin";
        }else if (CryptoCurrency.ETH.getCode().equalsIgnoreCase(cryptoCurrency)
            || CryptoCurrency.BAT.getCode().equalsIgnoreCase(cryptoCurrency)
            || CryptoCurrency.DAI.getCode().equalsIgnoreCase(cryptoCurrency)
            || CryptoCurrency.BIZZ.getCode().equalsIgnoreCase(cryptoCurrency)){
            return "ethereum";
        }else if (CryptoCurrency.BCH.getCode().equalsIgnoreCase(cryptoCurrency)){
            return "bitcoincash";
        } else if (CryptoCurrency.DASH.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return "dash";
        }
        return null;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only " + Arrays.toString(getCryptoCurrencies().toArray()) + " not " + cryptoCurrency);
            return null;
        }
        initIfNeeded(cryptoCurrency);
        long timeStamp = getTimestamp();
        CBAccountResponse accountResponse = api.getAccount(apiKey, API_VERSION, CBDigest.createInstance(apiSecret, timeStamp), timeStamp,accountIds.get(cryptoCurrency));
        if (accountResponse != null && accountResponse.getData() != null && cryptoCurrency.equalsIgnoreCase(accountResponse.getData().getBalance().getCurrency())) {
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
            log.error("Wallet supports only " + Arrays.toString(getCryptoCurrencies().toArray()) + " not " + cryptoCurrency);
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
        CBSendRequest sendRequest = new CBSendRequest("send",destinationAddress,amount.stripTrailingZeros().toPlainString(),cryptoCurrency,description, description, destinationTag); //note that description is here used as unique token as reply protection
        CBSendResponse response = api.send(apiKey,API_VERSION, CBDigest.createInstance(apiSecret, timeStamp), timeStamp,accountIds.get(cryptoCurrency), sendRequest);
        if (response != null && response.getData() != null) {
            return response.getData().getId();
        }
        if (response != null && response.getErrors() != null) {
            log.error("sendCoins - " + response.getErrorMessages());
        }
        return null; //some error happened
    }

//    public static void main(String[] args) {
//        ServerUtil.setLoggerLevel("si.mazi.rescu","trace");
//        String cryptoCurrency = CryptoCurrency.BTC.getCode();
//        CoinbaseWalletV2 w = new CoinbaseWalletV2("LGcOlxy5UNGXGcKp","8bTu2aKO9VsRHNaK7fvf6Y5dyb87GaoV",null);
//        String cryptoAddress = w.getCryptoAddress(cryptoCurrency);
//        log.info("cryptoAddress = " + cryptoAddress);
//        BigDecimal cryptoBalance = w.getCryptoBalance(cryptoCurrency);
//        log.info("cryptoBalance = " + cryptoBalance);
//        String result = w.sendCoins("1Nqip1Qc6EP88jwNrVwFy2CiXAuzPhdPgG", new BigDecimal("0.0005"), cryptoCurrency, "RXIDS");
//        log.info("result = " + result);
//    }
}
