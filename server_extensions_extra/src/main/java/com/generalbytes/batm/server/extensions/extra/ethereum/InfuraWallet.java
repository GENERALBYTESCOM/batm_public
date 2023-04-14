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
import com.generalbytes.batm.server.extensions.IWallet;
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
        try {
            if (!getCryptoCurrencies().contains(cryptoCurrency)) {
                log.error("InfuraWallet wallet error: unknown cryptocurrency: {}", cryptoCurrency);
                return null;
            }
            if (destinationAddress == null) {
                log.error("Destination address is null");
                return null;
            }
            if (!EtherUtils.isEtherAddressValid(destinationAddress)) {
                log.error("ETH destination adddress is not valid or has no valid checksum: {}", destinationAddress);
                return null;
            }

            destinationAddress = destinationAddress.toLowerCase();
            BigInteger amountInWei = convertEthToWei(amount);
            log.info("InfuraWallet - sending {} {} (= {} wei) from {} to {}", amount, cryptoCurrency, amountInWei, credentials.getAddress(), destinationAddress);
            RawTransactionManager transactionManager = new RawTransactionManager(w, credentials);
            BigInteger gasLimit = getGasLimit(destinationAddress, amount);
            BigInteger gasPrice = getCurrentGasPrice();

            log.info("InfuraWallet - gasPrice: {} gasLimit: {}", gasPrice, gasLimit);
            String txId = broadcastEthTransaction(destinationAddress, amountInWei, transactionManager, gasLimit, gasPrice);
            log.info("InfuraWallet - txId: {}, successfully broadcasted tx to send {} {} to {}", txId, amount, cryptoCurrency, destinationAddress);
            return txId;

        } catch (Exception e) {
            log.error("Error sending {} {} to {} (description: {})", amount, cryptoCurrency, destinationAddress, description, e);
        }
        return null;
    }

    private String broadcastEthTransaction(String destinationAddress, BigInteger amountInWei, RawTransactionManager transactionManager, BigInteger gasLimit, BigInteger gasPrice) throws IOException {
        final EthSendTransaction ethSentTransaction = transactionManager.sendTransaction(gasPrice, gasLimit, destinationAddress, "", amountInWei);
        if (ethSentTransaction.hasError()) {
            throw new IOException("error sending ETH tx: " + ethSentTransaction.getError().getMessage());
        }
        return ethSentTransaction.getTransactionHash();
    }

    private BigInteger getCurrentGasPrice() throws IOException {
        EthGasPrice ethGasPrice = w.ethGasPrice().send();
        if (ethGasPrice == null) {
            throw new IOException("Couldn't fetch gasPrice");
        }
        if (ethGasPrice.hasError()) {
            throw new IOException("Error getting gasPrice: " + ethGasPrice.getError().getMessage());
        }
        BigInteger recommendedGasPriceFromInfura = ethGasPrice.getGasPrice();
        if (recommendedGasPriceFromInfura.compareTo(BigInteger.ZERO) <= 0) {
            throw new RuntimeException("Couldn't fetch valid gasPrice, got: " + recommendedGasPriceFromInfura);
        }
        log.info("Gasprice from Infura: {} Gwei", Convert.fromWei(String.valueOf(recommendedGasPriceFromInfura), Convert.Unit.GWEI));
        return recommendedGasPriceFromInfura;
    }

    private BigInteger convertEthToWei(BigDecimal valueInEth) {
        BigDecimal weiValue = Convert.toWei(valueInEth, Convert.Unit.ETHER);
        if (!Numeric.isIntegerValue(weiValue)) {
            throw new UnsupportedOperationException("Non decimal Wei value provided: " + valueInEth + " " + ETHER + " = " + weiValue + " Wei");
        }
        return weiValue.toBigIntegerExact();
    }

    private BigInteger getGasLimit(String destinationAddress, BigDecimal amount) throws IOException {
        BigInteger weiValue = Convert.toWei(amount, ETHER).toBigIntegerExact();
        Transaction transaction = Transaction.createEtherTransaction(credentials.getAddress(), null, null, null, destinationAddress, weiValue);
        EthEstimateGas estimateGas = w.ethEstimateGas(transaction).send();
        if (estimateGas.hasError()) {
            throw new IOException("Error getting gas limit estimate: " + estimateGas.getError().getMessage());
        }
        return estimateGas.getAmountUsed();
    }

}
