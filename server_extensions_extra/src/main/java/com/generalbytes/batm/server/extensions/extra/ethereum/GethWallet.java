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
package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.web3j.utils.Convert.Unit.ETHER;

/**
 * ETH wallet backed by a local Geth or Erigon node via its standard JSON-RPC API.
 * Config string: geth:http://HOST:PORT:mnemonicOrPassword
 */
public class GethWallet implements IWallet, IQueryableWallet, IGeneratesNewDepositCryptoAddress {

    private static final Logger log = LoggerFactory.getLogger(GethWallet.class);

    private final String cryptoCurrency = CryptoCurrency.ETH.getCode();
    private final Credentials credentials;
    private final Web3j w;

    public GethWallet(String nodeUrl, String mnemonicOrPassword) {
        this.credentials = initCredentials(mnemonicOrPassword);
        this.w = Web3j.build(new HttpService(nodeUrl));
    }

    private Credentials initCredentials(String mnemonicOrPassword) {
        String mnemonic = mnemonicOrPassword.contains(" ")
            ? mnemonicOrPassword
            : EtherUtils.generateMnemonicFromPassword(mnemonicOrPassword);
        return EtherUtils.loadBip44Credentials(mnemonic, EtherUtils.ETHEREUM_WALLET_PASSWORD);
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
            log.error("GethWallet error: unknown cryptocurrency.");
            return null;
        }
        return credentials.getAddress();
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        return getCryptoAddress(cryptoCurrency);
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("GethWallet error: unknown cryptocurrency.");
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
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        try {
            EthGetBalance ethGetBalance = w
                .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();
            BigDecimal balance = Convert.fromWei(new BigDecimal(ethGetBalance.getBalance()), ETHER);
            int confirmations = balance.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0;
            return new ReceivedAmount(balance, confirmations);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error reading received amount for address {}.", address, e);
        }
        return ReceivedAmount.ZERO;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            if (!getCryptoCurrencies().contains(cryptoCurrency)) {
                log.error("GethWallet error: unknown cryptocurrency: {}", cryptoCurrency);
                return null;
            }
            if (destinationAddress == null) {
                log.error("Destination address is null");
                return null;
            }
            if (!EtherUtils.isEtherAddressValid(destinationAddress)) {
                log.error("ETH destination address is not valid: {}", destinationAddress);
                return null;
            }

            destinationAddress = destinationAddress.toLowerCase();
            BigInteger amountInWei = convertEthToWei(amount);
            log.info("GethWallet - sending {} {} (= {} wei) from {} to {}", amount, cryptoCurrency, amountInWei, credentials.getAddress(), destinationAddress);

            RawTransactionManager transactionManager = new RawTransactionManager(w, credentials);
            BigInteger gasLimit = getGasLimit(destinationAddress, amount);
            BigInteger gasPrice = getCurrentGasPrice();

            log.info("GethWallet - gasPrice: {} gasLimit: {}", gasPrice, gasLimit);
            EthSendTransaction ethSentTransaction = transactionManager.sendTransaction(gasPrice, gasLimit, destinationAddress, "", amountInWei);
            if (ethSentTransaction.hasError()) {
                log.error("GethWallet - error sending ETH tx: {}", ethSentTransaction.getError().getMessage());
                return null;
            }
            String txId = ethSentTransaction.getTransactionHash();
            log.info("GethWallet - txId: {}, sent {} {} to {}", txId, amount, cryptoCurrency, destinationAddress);
            return txId;
        } catch (Exception e) {
            log.error("Error sending {} {} to {} (description: {})", amount, cryptoCurrency, destinationAddress, description, e);
        }
        return null;
    }

    private BigInteger getCurrentGasPrice() throws IOException {
        EthGasPrice ethGasPrice = w.ethGasPrice().send();
        if (ethGasPrice == null || ethGasPrice.hasError()) {
            throw new IOException("Error getting gasPrice: " + (ethGasPrice != null ? ethGasPrice.getError().getMessage() : "null response"));
        }
        BigInteger gasPrice = ethGasPrice.getGasPrice();
        if (gasPrice.compareTo(BigInteger.ZERO) <= 0) {
            throw new IOException("Invalid gasPrice: " + gasPrice);
        }
        log.info("GethWallet - gasPrice: {} Gwei", Convert.fromWei(String.valueOf(gasPrice), Convert.Unit.GWEI));
        return gasPrice;
    }

    private BigInteger convertEthToWei(BigDecimal valueInEth) {
        BigDecimal weiValue = Convert.toWei(valueInEth, ETHER);
        if (!Numeric.isIntegerValue(weiValue)) {
            throw new UnsupportedOperationException("Non-integer Wei value: " + valueInEth + " ETH = " + weiValue + " Wei");
        }
        return weiValue.toBigIntegerExact();
    }

    private BigInteger getGasLimit(String destinationAddress, BigDecimal amount) throws IOException {
        BigInteger weiValue = Convert.toWei(amount, ETHER).toBigIntegerExact();
        Transaction transaction = Transaction.createEtherTransaction(credentials.getAddress(), null, null, null, destinationAddress, weiValue);
        EthEstimateGas estimateGas = w.ethEstimateGas(transaction).send();
        if (estimateGas.hasError()) {
            throw new IOException("Error estimating gas: " + estimateGas.getError().getMessage());
        }
        return estimateGas.getAmountUsed();
    }
}
