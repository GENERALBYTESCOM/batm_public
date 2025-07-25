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

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseV2ApiWrapper;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBBalance;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBNetwork;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBTransaction;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CoinbaseWalletV2WithUniqueAddresses extends CoinbaseWalletV2 implements IGeneratesNewDepositCryptoAddress, IQueryableWallet {
    private static final Logger log = LoggerFactory.getLogger(CoinbaseWalletV2WithUniqueAddresses.class);

    public CoinbaseWalletV2WithUniqueAddresses(CoinbaseV2ApiWrapper api, String accountName) {
        super(api, accountName);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only {} not {}", Arrays.toString(getCryptoCurrencies().toArray()), cryptoCurrency);
            return null;
        }
        initIfNeeded(cryptoCurrency);
        long timeStamp = getTimestamp();
        CBCreateAddressResponse addressesResponse = api.createAddress(API_VERSION, timeStamp, accountIds.get(cryptoCurrency), new CBCreateAddressRequest(label));
        if (addressesResponse != null && addressesResponse.getData() != null) {
            CBAddress address = addressesResponse.getData();
            String network = getNetworkName(cryptoCurrency);
            if (!address.getNetwork().equalsIgnoreCase(network)) {
                log.warn("network does not match; {} network name: {}, received: {}", cryptoCurrency, network, address.getNetwork());
                return null;
            }
            return address.getAddress();
        }
        if (addressesResponse != null && addressesResponse.getErrors() != null) {
            log.error("generateNewDepositCryptoAddress - {}", addressesResponse.getErrorMessages());
        }
        return null;
    }

    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only {}, not {}", getCryptoCurrencies(), cryptoCurrency);
            return ReceivedAmount.ZERO;
        }
        initIfNeeded(cryptoCurrency);

        String addressId = getAddressId(address, cryptoCurrency);
        List<CBTransaction> transactions = getReceivedTransactions(addressId, cryptoCurrency);
        log.trace("Received transactions: {}", transactions);
        int confirmations = getConfirmations(transactions);
        BigDecimal amount = getAmount(transactions);
        ReceivedAmount receivedAmount = new ReceivedAmount(amount, confirmations);
        receivedAmount.setTransactionHashes(getTransactionHashes(transactions));
        return receivedAmount;
    }

    private List<String> getTransactionHashes(List<CBTransaction> transactions) {
        return transactions.stream()
            .map(CBTransaction::getNetwork)
            .filter(Objects::nonNull)
            .map(CBNetwork::getHash)
            .filter(hash -> hash != null && !hash.isBlank())
            .toList();
    }

    private BigDecimal getAmount(List<CBTransaction> transactions) {
        return transactions.stream()
            .map(CBTransaction::getAmount)
            .map(CBBalance::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int getConfirmations(List<CBTransaction> transactions) {
        if (transactions.isEmpty()) {
            return 0;
        }
        if (transactions.stream().allMatch(this::isCompleted)) {
            log.trace("All transactions completed");
            return 999;
        }
        return 0;
    }

    private List<CBTransaction> getReceivedTransactions(String addressId, String cryptoCurrency) {
        return getTransactions(addressId, cryptoCurrency).stream()
            .filter(t -> "send".equalsIgnoreCase(t.getType()))
            .filter(t -> isPending(t) || isCompleted(t))
            .filter(t -> t.getAmount() != null && t.getAmount().getCurrency().equalsIgnoreCase(cryptoCurrency))
            .toList();
    }

    private boolean isPending(CBTransaction transaction) {
        return "pending".equalsIgnoreCase(transaction.getStatus());
    }

    private boolean isCompleted(CBTransaction transaction) {
        return "completed".equalsIgnoreCase(transaction.getStatus());
    }

    private String getAddressId(String address, String cryptoCurrency) {
        return getAddresses(cryptoCurrency).stream()
            .filter(a -> a.getAddress().equals(address))
            .findAny()
            .map(CBAddress::getId)
            .orElseThrow(() -> new IllegalStateException("Address '" + address + "' not found"));
    }

    private List<CBTransaction> getTransactions(String addressId, String cryptoCurrency) {
        return paginate(startingAfter -> {
            long timeStamp = getTimestamp();
            String accountId = accountIds.get(cryptoCurrency);
            return api.getAddressTransactions(API_VERSION, timeStamp, accountId, addressId, 100, startingAfter);
        });
    }

    private List<CBAddress> getAddresses(String cryptoCurrency) {
        return paginate(startingAfter -> {
            long timeStamp = getTimestamp();
            String accountId = accountIds.get(cryptoCurrency);
            return api.getAddresses(API_VERSION, timeStamp, accountId, 100, startingAfter);
        });
    }
}
