/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.ethereum.erc20;

import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.EtherUtils;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.generated.ERC20Interface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ERC20Wallet implements IWallet{
    private String tokenSymbol;
    private int tokenDecimalPlaces;
    private ERC20Interface contract;

    private Credentials credentials;
    private String apiKey;
    private Web3j w;

    private static final Logger log = LoggerFactory.getLogger(ERC20Wallet.class);

    public ERC20Wallet(String apiKey, String mnemonicOrPassword, String tokenSymbol, int tokenDecimalPlaces, String contractAddress) {
        this.apiKey = apiKey;

        this.tokenSymbol = tokenSymbol;
        this.tokenDecimalPlaces = tokenDecimalPlaces;

        credentials = initCredentials(mnemonicOrPassword);
        w = Web3j.build(new HttpService("https://mainnet.infura.io/v3/" + apiKey));
        try {
            final BigInteger gasPrice = w.ethGasPrice().send().getGasPrice();

            ContractGasProvider gasProvider = new ContractGasProvider() {
                @Override
                public BigInteger getGasPrice(String contractFunc) {
                    return gasPrice;
                }

                @Override
                public BigInteger getGasPrice() {
                    return gasPrice;
                }

                @Override
                public BigInteger getGasLimit(String contractFunc) {
                    if (ERC20Interface.FUNC_TRANSFER.equals(contractFunc)) {
                        //get gas estimate for the transaction
                        BigInteger transferGasEstimate = getTransferGasEstimate(credentials.getAddress(), new BigDecimal("1"));
                        //Make gas limit 10% higher than estimate just to be safe
                        BigInteger gasLimit = new BigDecimal(transferGasEstimate).multiply(new BigDecimal("1.1")).setScale(0, RoundingMode.UP).toBigInteger();
                        return gasLimit;
                    }else{
                        return null;
                    }
                }

                @Override
                public BigInteger getGasLimit() {
                    return null;
                }
            };
            contract = ERC20Interface.load(contractAddress, w, credentials, gasProvider);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BigInteger getTransferGasEstimate(String to, BigDecimal tokens) {
        final Function function = new Function(
            ERC20Interface.FUNC_TRANSFER,
            Arrays.asList(new org.web3j.abi.datatypes.Address(to), new org.web3j.abi.datatypes.generated.Uint256(convertFromBigDecimal(tokens))),
            Collections.emptyList());

        Transaction tx = Transaction.createEthCallTransaction(credentials.getAddress(), contract.getContractAddress(), FunctionEncoder.encode(function));
        try {
            EthEstimateGas estimateGas = w.ethEstimateGas(tx).send();
            return estimateGas.getAmountUsed();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private BigDecimal convertToBigDecimal(BigInteger value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value).setScale(tokenDecimalPlaces, BigDecimal.ROUND_DOWN).divide(BigDecimal.TEN.pow(tokenDecimalPlaces), BigDecimal.ROUND_DOWN).stripTrailingZeros();
    }

    private BigInteger convertFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.multiply(BigDecimal.TEN.pow(tokenDecimalPlaces)).toBigInteger();
    }

    private Credentials initCredentials(String mnemonicOrPassword) {
        String mnemonic;
        if (!mnemonicOrPassword.contains(" ")) {
            //it is a password
            mnemonic = EtherUtils.generateMnemonicFromPassword(mnemonicOrPassword);
        }else{
            mnemonic = mnemonicOrPassword;
        }
        return EtherUtils.loadBip44Credentials(mnemonic, EtherUtils.ETHEREUM_WALLET_PASSWORD);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> currencies = new HashSet<>();
        currencies.add(tokenSymbol);
        return currencies;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return tokenSymbol;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("ERC20 wallet error: unknown cryptocurrency.");
            return null;
        }
        return credentials.getAddress();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("ERC20 wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            BigInteger amount = contract.balanceOf(credentials.getAddress()).send();
            if (amount != null) {
                return convertToBigDecimal(amount);
            }
        } catch (Exception e) {
            log.error("Error obtaining balance.", e);
        }
        return null;
    }


    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("ERC20 wallet error: unknown cryptocurrency.");
            return null;
        }

        BigDecimal cryptoBalance = getCryptoBalance(cryptoCurrency);
        if (cryptoBalance == null || cryptoBalance.compareTo(amount) < 0) {
            log.error("ERC20 wallet error: Not enough tokens. Balance is: " + cryptoBalance + " " + cryptoCurrency +". Trying to send: " + amount + " " + cryptoCurrency);
            return null;
        }

        try {
            log.info("ERC20 sending coins from " + credentials.getAddress() + " using smart contract " + contract.getContractAddress() + " to: " + destinationAddress + " " + amount + " " + cryptoCurrency);
            CompletableFuture<TransactionReceipt> future = contract.transfer(destinationAddress, convertFromBigDecimal(amount)).sendAsync();
            //We give the transaction 10 seconds
            TransactionReceipt receipt = future.get(10, TimeUnit.SECONDS);
            log.debug("ERC20 receipt = " + receipt);
            return receipt.getTransactionHash();
        } catch (TimeoutException e) {
            return "info_in_future"; //error probably will not happen as we waited already 20 seconds.
        } catch (InterruptedException e) {
            log.error("Error sending coins.", e);
        } catch (ExecutionException e) {
            log.error("Error sending coins.", e);
        } catch (Exception e) {
            log.error("Error sending coins.", e);
        }
        return null;
    }
}
