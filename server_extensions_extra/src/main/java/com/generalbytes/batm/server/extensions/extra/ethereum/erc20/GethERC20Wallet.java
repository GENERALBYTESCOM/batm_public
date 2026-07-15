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

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.EtherUtils;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.generated.ERC20Interface;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
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

/**
 * ERC-20 token wallet backed by a local Geth or Erigon node via its standard JSON-RPC API.
 * Config string: gethERC20_SYMBOL_DECIMALS_0xCONTRACT:http://HOST:PORT:mnemonicOrPassword[:gasLimit[:gasPriceMultiplier]]
 */
public class GethERC20Wallet implements IWallet, IQueryableWallet, IGeneratesNewDepositCryptoAddress {

    private static final Logger log = LoggerFactory.getLogger(GethERC20Wallet.class);

    private final String contractAddress;
    private final String tokenSymbol;
    private final int tokenDecimalPlaces;
    private final Credentials credentials;
    private final Web3j w;
    private final BigInteger fixedGasLimit;
    private final BigDecimal gasPriceMultiplier;
    private final ERC20Interface noGasContract;

    public GethERC20Wallet(String nodeUrl, String mnemonicOrPassword, String tokenSymbol,
                           int tokenDecimalPlaces, String contractAddress,
                           BigInteger fixedGasLimit, BigDecimal gasPriceMultiplier) {
        this.tokenSymbol = tokenSymbol;
        this.tokenDecimalPlaces = tokenDecimalPlaces;
        this.contractAddress = contractAddress.toLowerCase();
        this.fixedGasLimit = fixedGasLimit;
        this.gasPriceMultiplier = gasPriceMultiplier;
        this.credentials = initCredentials(mnemonicOrPassword);
        this.w = Web3j.build(new HttpService(nodeUrl));
        this.noGasContract = ERC20Interface.load(this.contractAddress, w, credentials, DummyContractGasProvider.INSTANCE);
    }

    private Credentials initCredentials(String mnemonicOrPassword) {
        String mnemonic = mnemonicOrPassword.contains(" ")
            ? mnemonicOrPassword
            : EtherUtils.generateMnemonicFromPassword(mnemonicOrPassword);
        return EtherUtils.loadBip44Credentials(mnemonic, EtherUtils.ETHEREUM_WALLET_PASSWORD);
    }

    private ERC20Interface getContract(String destinationAddress, BigInteger tokensAmount) {
        ERC20ContractGasProvider contractGasProvider = new ERC20ContractGasProvider(
            contractAddress, credentials.getAddress(), destinationAddress, tokensAmount, fixedGasLimit, gasPriceMultiplier, w);
        return ERC20Interface.load(this.contractAddress, w, credentials, contractGasProvider);
    }

    private BigDecimal convertToBigDecimal(BigInteger value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value)
            .setScale(tokenDecimalPlaces, BigDecimal.ROUND_DOWN)
            .divide(BigDecimal.TEN.pow(tokenDecimalPlaces), BigDecimal.ROUND_DOWN)
            .stripTrailingZeros();
    }

    private BigInteger convertFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.multiply(BigDecimal.TEN.pow(tokenDecimalPlaces)).toBigInteger();
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
            log.error("GethERC20Wallet error: unknown cryptocurrency.");
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
            log.error("GethERC20Wallet error: unknown cryptocurrency.");
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
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        try {
            BigInteger amount = noGasContract.balanceOf(address).send();
            if (amount != null) {
                BigDecimal balance = convertToBigDecimal(amount);
                int confirmations = balance != null && balance.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0;
                return new ReceivedAmount(balance != null ? balance : BigDecimal.ZERO, confirmations);
            }
        } catch (Exception e) {
            log.error("Error reading received amount for address {}.", address, e);
        }
        return ReceivedAmount.ZERO;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("GethERC20Wallet error: unknown cryptocurrency.");
            return null;
        }

        if (destinationAddress != null) {
            destinationAddress = destinationAddress.toLowerCase();
        }

        BigDecimal cryptoBalance = getCryptoBalance(cryptoCurrency);
        if (cryptoBalance == null || cryptoBalance.compareTo(amount) < 0) {
            log.error("GethERC20Wallet error: Not enough tokens. Balance: {} {}, trying to send: {} {}", cryptoBalance, cryptoCurrency, amount, cryptoCurrency);
            return null;
        }

        try {
            log.info("GethERC20Wallet sending {} {} from {} via contract {} to {}", amount, cryptoCurrency, credentials.getAddress(), contractAddress, destinationAddress);
            BigInteger tokens = convertFromBigDecimal(amount);
            TransactionReceipt receipt = getContract(destinationAddress, tokens)
                .transfer(destinationAddress, tokens)
                .send();
            log.debug("GethERC20Wallet receipt: {}", receipt);
            return receipt.getTransactionHash();
        } catch (Exception e) {
            log.error("Error sending coins.", e);
        }
        return null;
    }
}
