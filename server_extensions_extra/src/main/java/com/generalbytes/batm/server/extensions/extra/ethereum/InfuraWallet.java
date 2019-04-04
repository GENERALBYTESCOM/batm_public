/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.web3j.utils.Convert.Unit.ETHER;

public class InfuraWallet implements IWallet{
    private String cryptoCurrency = CryptoCurrency.ETH.getCode();
    private Credentials credentials;
    private Web3j w;

    private static final Logger log = LoggerFactory.getLogger(InfuraWallet.class);

    public InfuraWallet(String projectId, String mnemonicOrPassword) {
        credentials = initCredentials(mnemonicOrPassword);
        w = Web3j.build(new HttpService("https://mainnet.infura.io/v3/" + projectId));
    }

    private Credentials initCredentials(String mnemonicOrPassword) {
        String mnemonic;
        if (!mnemonicOrPassword.contains(" ")) {
            //it is a password
            mnemonic = EtherUtils.generateMnemonicFromPassword(mnemonicOrPassword);
        }else{
            mnemonic = mnemonicOrPassword;
        }
        return EtherUtils.loadBip44Credentials(mnemonic,EtherUtils.ETHEREUM_WALLET_PASSWORD);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> currencies = new HashSet<>();
        currencies.add(cryptoCurrency);
        return currencies;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return cryptoCurrency;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("InfuraWallet wallet error: unknown cryptocurrency.");
            return null;
        }
        return credentials.getAddress();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("InfuraWallet wallet error: unknown cryptocurrency.");
            return null;
        }
        try {
            EthGetBalance ethGetBalance = w
                .ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.PENDING)
                .sendAsync()
                .get();
            return Convert.fromWei(new BigDecimal(ethGetBalance.getBalance()), ETHER);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error reading balance.", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("InfuraWallet wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            log.info("InfuraWallet sending coins from " + credentials.getAddress() + " to: " + destinationAddress + " " + amount + " " + cryptoCurrency);

            CompletableFuture<TransactionReceipt> future = Transfer.sendFunds(
                w, credentials,
                destinationAddress,
                amount,
                Convert.Unit.ETHER)
                .sendAsync();

            TransactionReceipt receipt = future.get(10, TimeUnit.SECONDS);
            log.debug("InfuraWallet receipt = " + receipt);
            return receipt.getTransactionHash();
        } catch (TimeoutException e) {
            return "info_in_future"; //error probably will not happen as we waited already 10 seconds.
        } catch (IOException e) {
            log.error("Error sending coins.", e);
        } catch (InterruptedException | TransactionException e) {
            log.error("Error sending coins.", e);
        } catch (Exception e) {
            log.error("Error sending coins.", e);
        }
        return null;
    }

}
