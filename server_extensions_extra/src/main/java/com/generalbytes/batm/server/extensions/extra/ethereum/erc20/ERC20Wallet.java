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
package com.generalbytes.batm.server.extensions.extra.ethereum.erc20;

import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.EtherUtils;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.generated.ERC20Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ERC20Wallet implements IWallet{
    private final String contractAddress;
    private final String tokenSymbol;
    private final int tokenDecimalPlaces;
    private final Credentials credentials;
    private final Web3j w;
    private final BigInteger fixedGasLimit;
    private final BigDecimal gasPriceMultiplier;
    private final ERC20Interface noGasContract;

    private static final Logger log = LoggerFactory.getLogger(ERC20Wallet.class);

    public ERC20Wallet(String projectId, String mnemonicOrPassword, String tokenSymbol, int tokenDecimalPlaces, String contractAddress, BigInteger fixedGasLimit, BigDecimal gasPriceMultiplier) {
        this.tokenSymbol = tokenSymbol;
        this.tokenDecimalPlaces = tokenDecimalPlaces;
        this.contractAddress = contractAddress.toLowerCase();
        this.fixedGasLimit = fixedGasLimit;
        this.gasPriceMultiplier = gasPriceMultiplier;
        this.credentials = initCredentials(mnemonicOrPassword);
        this.w = Web3j.build(new HttpService("https://mainnet.infura.io/v3/" + projectId));
        this.noGasContract = ERC20Interface.load(this.contractAddress, w, credentials, DummyContractGasProvider.INSTANCE);
    }

    private ERC20Interface getContract(String destinationAddress, BigInteger tokensAmount) {
        ERC20ContractGasProvider contractGasProvider = new ERC20ContractGasProvider(contractAddress, credentials.getAddress(), destinationAddress, tokensAmount, fixedGasLimit, gasPriceMultiplier, w);
        return ERC20Interface.load(this.contractAddress, w, credentials, contractGasProvider);
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
            BigInteger amount = noGasContract.balanceOf(credentials.getAddress()).send();
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

        if (destinationAddress != null) {
            destinationAddress = destinationAddress.toLowerCase();
        }

        BigDecimal cryptoBalance = getCryptoBalance(cryptoCurrency);
        if (cryptoBalance == null || cryptoBalance.compareTo(amount) < 0) {
            log.error("ERC20 wallet error: Not enough tokens. Balance is: " + cryptoBalance + " " + cryptoCurrency +". Trying to send: " + amount + " " + cryptoCurrency);
            return null;
        }

        try {
            log.info("ERC20 sending coins from " + credentials.getAddress() + " using smart contract " + contractAddress + " to: " + destinationAddress + " " + amount + " " + cryptoCurrency);
            BigInteger tokens = convertFromBigDecimal(amount);
            TransactionReceipt receipt = getContract(destinationAddress, tokens)
                .transfer(destinationAddress, tokens)
                .sendAsync()
                .get(10, TimeUnit.SECONDS);
            log.debug("ERC20 receipt: {}", receipt);
            return receipt.getTransactionHash();
        } catch (TimeoutException e) {
            return "info_in_future"; // the response is really slow, this can happen but the transaction can succeed anyway
        } catch (Exception e) {
            log.error("Error sending coins.", e);
        }
        return null;
    }
}
