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
package com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp;

import com.generalbytes.batm.common.currencies.SlpToken;
import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.ThrowingSupplier;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.BitcoinCashAddress;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.ISlpdbApi;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.AddressesBalanceSlpdbRequest;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.IncomingTransactionsSlpdbRequest;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.IncomingTransactionsSlpdbResponse;
import com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto.StatusSlpdbRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.SlpWallet;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.BroadcastElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.BroadcastElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.CreateNewAddressElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.GetAddressUnspentElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.GetAddressUnspentElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesAndBalancesElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesAndBalancesElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesAndBalancesElectrumResponse.AddressInfoResult;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.ListAddressesElectrumResponse;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.PayToSlpElectrumRequest;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto.PayToSlpElectrumResponse;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.ClientConfigUtil;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ElectronCashSlpWallet extends SlpWallet implements IWallet, IGeneratesNewDepositCryptoAddress, IQueryableWallet {
    private static final Logger log = LoggerFactory.getLogger(ElectronCashSlpWallet.class);

    // UTXOs with tokens have this many BCH satoshis but the same TX can also send any amount of BCH in separate outputs
    // so the address or transaction "balance" or value can be higher but UTXO having tokens has this value
    public static final int TOKEN_UTXO_SATS = 546; // dust limit - value of SLP UTXOs

    private final IElectronCashSlpApi api;
    private final List<ISlpdbApi> slpdbApis;

    private Supplier<Integer> bestBchBlockHeightCachedSupplier = Suppliers.memoizeWithExpiration(this::getBestBlockHeight, 1, TimeUnit.MINUTES);

    public ElectronCashSlpWallet(String user, String password, String host, int port) {
        String url = new HttpUrl.Builder().scheme("http").host(host).port(port).build().toString();
        ClientConfig config = new ClientConfig();
        ClientConfigUtil.addBasicAuthCredentials(config, user, password);
        this.api = RestProxyFactory.createProxy(IElectronCashSlpApi.class, url, config, ElectronCashSlpWallet::interceptErrorResponse);

        this.slpdbApis = ISlpdbApi.create();
    }

    protected String getCryptoAddressInternal(String cryptoCurrency) throws IOException, AddressFormatException {
        ListAddressesElectrumResponse listAddressesResp = api.listAddresses(new ListAddressesElectrumRequest());
        if (listAddressesResp.result.isEmpty()) {
            return generateNewDepositCryptoAddressInternal(cryptoCurrency, null);
        }
        return new BitcoinCashAddress(listAddressesResp.result.get(0)).getSimpleledger(false);
    }

    protected String generateNewDepositCryptoAddressInternal(String cryptoCurrency, String label) throws IOException, AddressFormatException {
        String address = api.createNewAddress(new CreateNewAddressElectrumRequest()).result;
        return new BitcoinCashAddress(address).getSimpleledger(false);
    }

    @Override
    protected String sendCoinsInternal(String destinationAddress, BigDecimal amountRounded, String cryptoCurrency, String description) throws IOException {
        PayToSlpElectrumResponse payToSlpResponse = api.payToSlp(new PayToSlpElectrumRequest(SlpToken.valueOf(cryptoCurrency).getTokenId(), destinationAddress, amountRounded));
        BroadcastElectrumResponse broadcastResponse = api.broadcast(new BroadcastElectrumRequest(payToSlpResponse.result.hex));
        return broadcastResponse.isSuccess() ? broadcastResponse.getTxId() : null;
    }

    @Override
    protected BigDecimal getCryptoBalanceInternal(String cryptoCurrency) throws IOException {
        // not implemented in current Electron Cash SLP version. Workaround using external online service:
        // - get all wallet's addresses
        // - filter out those with zero BCH balance - there will be no token balance on them
        // - (if electrum is running locally it would be possible to query UTXOs (a call per address) and filter only tose with TOKEN_UTXO_SATS value)
        // - convert all addresses to the simpleledger format
        // - ask SLPDB online for total balance of those addresses for a given token ID

        ListAddressesAndBalancesElectrumResponse addressesResp = api.listAddresses(new ListAddressesAndBalancesElectrumRequest());

        List<String> addressesWithBalance = addressesResp.result.stream()
            // balance > 0 (returned in a strange format like "0," but if there is any non-0 number it is fine)
            .filter(a -> a.getBalance().matches(".*[1-9].*"))
            .map(AddressInfoResult::getAddress)
            .map(this::addressToSimpleledgerWithPrefix)
            .collect(Collectors.toList());

        // hopefully this is just a temporary workaround until proper RPC calls are implemented in Electron Cash.
        // If this list (and the following query) gets too large it would be possible to send all funds to self to have less addresses with funds

        return getSlpdbApi().getBalance(new AddressesBalanceSlpdbRequest(SlpToken.valueOf(cryptoCurrency).getTokenId(), addressesWithBalance)).getBalance();
    }

    @Override
    public ReceivedAmount getReceivedAmountInternal(String address, String cryptoCurrency) throws IOException, AddressFormatException {
        // Electron Cash SLP currently does not support getting a balance of a token over JSON RPC.
        // Workaround:
        // - get BCH UTSOs of the address
        // - if there is any of the TOKEN_UTXO_SATS value there were possibly some tokens received
        // - only then call SLPDB (online) for incoming transactions on that address
        GetAddressUnspentElectrumResponse addressUnspentResp = api.getAddressUnspent(new GetAddressUnspentElectrumRequest(address));
        if (addressUnspentResp.result.stream().noneMatch(utxo -> utxo.value == TOKEN_UTXO_SATS)) {
            return ReceivedAmount.ZERO;
        }
        String slpAddress = new BitcoinCashAddress(address).getSimpleledger(true);
        IncomingTransactionsSlpdbResponse resp = getSlpdbApi().getIncoimngTransactions(new IncomingTransactionsSlpdbRequest(SlpToken.valueOf(cryptoCurrency).getTokenId(), slpAddress));

        List<IncomingTransactionsSlpdbResponse.TransactionResult> allTransactions = resp.getAllTransactions();
        BigDecimal totalAmount = allTransactions.stream().map(t -> t.amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<String> transactionHashes = getTransactionHashes(allTransactions);

        ReceivedAmount receivedAmount = createReceivedAmount(resp, totalAmount);
        if (!transactionHashes.isEmpty()) {
            receivedAmount.setTransactionHashes(transactionHashes);
        }
        return receivedAmount;
    }

    private List<String> getTransactionHashes(List<IncomingTransactionsSlpdbResponse.TransactionResult> allTransactions) {
        return allTransactions.stream()
            .map(t -> t.tx)
            .filter(Objects::nonNull)
            .toList();
    }

    private ReceivedAmount createReceivedAmount(IncomingTransactionsSlpdbResponse resp, BigDecimal totalAmount) {
        if (resp.c.isEmpty()) { // no confirmed transactions
            return new ReceivedAmount(totalAmount, 0);
        }
        int maxTransactionHeight = resp.c.stream().mapToInt(t -> t.height).max().orElseThrow(IllegalStateException::new);
        return new ReceivedAmount(totalAmount, bestBchBlockHeightCachedSupplier.get() - maxTransactionHeight + 1);
    }

    private String addressToSimpleledgerWithPrefix(String address) throws RuntimeException {
        try {
            return new BitcoinCashAddress(address).getSimpleledger(true);
        } catch (AddressFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer getBestBlockHeight() {
        try {
            return getSlpdbApi().getStatus(new StatusSlpdbRequest()).getBchBlockHeight();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ///// error handling /////

    // standard rescu Exception-handling does not work because the error response has the same structure as the correct one
    private static Object interceptErrorResponse(InvocationHandler invocationHandler, Object proxy, Method method, Object[] args) throws Throwable {
        Object res = invocationHandler.invoke(proxy, method, args);
        if (res instanceof BroadcastElectrumResponse && ((BroadcastElectrumResponse) res).getError() != null) {
            // nonstandard error response - returned in different field
            throw new ElectrumErrorResponseException(((BroadcastElectrumResponse) res).getError());
        }
        if (res instanceof ElectrumResponse) {
            ElectrumResponse.ElectrumResponseError error = ((ElectrumResponse) res).error;
            if (error != null && error.message != null) {
                throw new ElectrumErrorResponseException(error.message);
            }
        }
        return res;
    }

    @Override
    protected <T> T callCheckedCustom(ThrowingSupplier<T> supplier) throws Exception {
        try {
            return supplier.get();
        } catch (ElectrumErrorResponseException e) {
            log.error("REST call returned an error: {}", e.getMessage());
        }
        return null;
    }

    private ISlpdbApi getSlpdbApi() {
        // use random URL each time so if one does not return any results another url is used on one of the next tries
        return slpdbApis.get(new Random().nextInt(slpdbApis.size()));
    }
}
