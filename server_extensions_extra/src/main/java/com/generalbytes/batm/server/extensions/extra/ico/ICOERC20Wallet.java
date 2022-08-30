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
package com.generalbytes.batm.server.extensions.extra.ico;

import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.EtherUtils;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.generated.ERC20Interface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ICOERC20Wallet implements IWallet {
    private final String contractAddress;
    private final String currencyAddress;
    private final String tokenAddress;
    private final String tokenSymbol;
    private final int tokenDecimalPlaces;
    private final Credentials credentials;
    private final Web3j w;
    private final BigInteger fixedGasLimit;
    private final BigDecimal gasPriceMultiplier;
    private final ERC20Interface noGasContract;
    private final ERC20Interface noGasTokenContract;
    private long chainID;
    private static final Logger log = LoggerFactory.getLogger(ICOERC20Wallet.class);

    public ICOERC20Wallet(long chainID, String rpcURL, String mnemonicOrPassword, String tokenSymbol, int tokenDecimalPlaces,
                          String contractAddress, String currencyAddress, String tokenAddress, BigInteger fixedGasLimit, BigDecimal gasPriceMultiplier) {

        StringBuilder sb = new StringBuilder();

        sb.append("BetVerseICOERC20Wallet:: ").append("\n");
        sb.append("ChainID:: " + chainID).append("\n");
        sb.append("rpcURL:: " + rpcURL).append("\n");
        sb.append("tokenSymbol:: " + tokenSymbol).append("\n");
        sb.append("tokenDecimalPlaces:: " + tokenDecimalPlaces).append("\n");
        sb.append("contractAddress:: " + contractAddress).append("\n");
        sb.append("currencyAddress:: " + currencyAddress).append("\n");
        sb.append("tokenAddress:: " + tokenAddress).append("\n");
        sb.append("fixedGasLimit:: " + fixedGasLimit).append("\n");
        sb.append("gasPriceMultiplier:: " + gasPriceMultiplier).append("\n");
        sb.append("mnemonicOrPassword:: " + mnemonicOrPassword).append("\n");

        this.tokenSymbol = tokenSymbol;
        this.tokenDecimalPlaces = tokenDecimalPlaces;
        this.contractAddress = contractAddress.toLowerCase();
        this.currencyAddress = currencyAddress.toLowerCase();
        this.tokenAddress = tokenAddress.toLowerCase();
        this.fixedGasLimit = fixedGasLimit;
        this.gasPriceMultiplier = gasPriceMultiplier; //gasPriceMultiplier;
        this.chainID = chainID;

        this.w = Web3j.build(new HttpService("https://" + rpcURL));

        this.credentials = initCredentials(mnemonicOrPassword);

        this.noGasContract = ERC20Interface.load(this.contractAddress, w, new FastRawTransactionManager(this.w, this.credentials, chainID), BigInteger.valueOf(100000000000L), DefaultGasProvider.GAS_LIMIT);
        this.noGasTokenContract = ERC20Interface.load(this.tokenAddress, w, new FastRawTransactionManager(this.w, this.credentials, chainID), BigInteger.valueOf(100000000000L), DefaultGasProvider.GAS_LIMIT);

    }

    private ERC20Interface getContract(String destinationAddress, BigInteger tokensAmount) {
        return ERC20Interface.load(this.contractAddress, w, new FastRawTransactionManager(this.w, this.credentials, chainID), BigInteger.valueOf(100000000000L), DefaultGasProvider.GAS_LIMIT);
    }

    private ERC20Interface getTokenContract() {
        return ERC20Interface.load(this.tokenAddress, w, new FastRawTransactionManager(this.w, this.credentials, chainID), BigInteger.valueOf(100000000000L), DefaultGasProvider.GAS_LIMIT);
    }

    private BigDecimal convertToBigDecimal(String value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value).setScale(tokenDecimalPlaces, BigDecimal.ROUND_DOWN)
                .divide(BigDecimal.TEN.pow(tokenDecimalPlaces), BigDecimal.ROUND_DOWN).stripTrailingZeros();
    }

    private BigInteger convertFromBigDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.multiply(BigDecimal.TEN.pow(tokenDecimalPlaces)).toBigInteger();
    }

    private Credentials initCredentials(String mnemonicOrPassword) {
        try {
            String mnemonic;
            if (!mnemonicOrPassword.contains(" ")) {
                // it is a password
                mnemonic = EtherUtils.generateMnemonicFromPassword(mnemonicOrPassword);
            } else {
                mnemonic = mnemonicOrPassword;
            }

            return EtherUtils.loadBip44Credentials(mnemonic, EtherUtils.ETHEREUM_WALLET_PASSWORD);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
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
            BigInteger amount = noGasTokenContract.balanceOf(credentials.getAddress()).send();
            if (amount != null) {
                return convertToBigDecimal(String.valueOf(amount));
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

        try {
            BigInteger tokens = convertFromBigDecimal(amount);
            final BigInteger transferAmountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigIntegerExact();

            TransactionReceipt receipt = getContract(destinationAddress, transferAmountWei)
                    .buy(destinationAddress, transferAmountWei, this.currencyAddress)
                    .sendAsync()
                    .get(240, TimeUnit.SECONDS);

            return receipt.getTransactionHash();
        } catch (TimeoutException e) {
            log.error(e.getMessage(), e);
            return "info_in_future"; // the response is really slow, this can happen but the transaction can succeed
                                     // anyway
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
