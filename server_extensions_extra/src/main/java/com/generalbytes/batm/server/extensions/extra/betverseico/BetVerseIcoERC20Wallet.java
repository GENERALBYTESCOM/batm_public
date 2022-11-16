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
package com.generalbytes.batm.server.extensions.extra.betverseico;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.ethereum.EtherUtils;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.DummyContractGasProvider;
import com.generalbytes.batm.server.extensions.extra.ethereum.erc20.ERC20ContractGasProvider;
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BetVerseIcoERC20Wallet implements IWallet {
    private final String contractAddress;
    private final String tokenAddress;
    private final String daiAddress;
    private final String tokenSymbol;
    private final int tokenDecimalPlaces;
    private final Credentials credentials;
    private final Web3j w;
    private final BigInteger fixedGasLimit;
    private final BigDecimal gasPriceMultiplier;
    private final ERC20Interface noGasContract;
    private final ERC20Interface noGasTokenContract;
    private long chainID;
    private final String urlPolygonAPI;
    private static final Logger log = LoggerFactory.getLogger(BetVerseIcoERC20Wallet.class);

    public BetVerseIcoERC20Wallet(long chainID, String rpcURL, String mnemonicOrPassword, String tokenSymbol, int tokenDecimalPlaces,
                                  String contractAddress, String tokenAddress, String daiAddress, BigInteger fixedGasLimit, BigDecimal gasPriceMultiplier, String urlPolygonAPI) {

        StringBuilder sb = new StringBuilder();

        sb.append("BetVerseIcoERC20Wallet:: ").append("\n");
        sb.append("ChainID:: " + chainID).append("\n");
        sb.append("rpcURL:: " + rpcURL).append("\n");
        sb.append("tokenSymbol:: " + tokenSymbol).append("\n");
        sb.append("tokenDecimalPlaces:: " + tokenDecimalPlaces).append("\n");
        sb.append("contractAddress:: " + contractAddress).append("\n");
        sb.append("tokenAddress:: " + tokenAddress).append("\n");
        sb.append("daiAddress:: " + daiAddress).append("\n");
        sb.append("fixedGasLimit:: " + fixedGasLimit).append("\n");
        sb.append("gasPriceMultiplier:: " + gasPriceMultiplier).append("\n");
        sb.append("mnemonicOrPassword:: " + mnemonicOrPassword).append("\n");
        sb.append("UrlPolygonAPI:: " + urlPolygonAPI).append("\n");

        this.tokenSymbol = tokenSymbol;
        this.tokenDecimalPlaces = tokenDecimalPlaces;
        this.contractAddress = contractAddress.toLowerCase();
        this.tokenAddress = tokenAddress.toLowerCase();
        this.daiAddress = daiAddress.toLowerCase();
        this.fixedGasLimit = fixedGasLimit;
        this.gasPriceMultiplier = gasPriceMultiplier;
        this.chainID = chainID;
        this.urlPolygonAPI = urlPolygonAPI;
        this.w = Web3j.build(new HttpService("https://" + rpcURL));

        this.credentials = initCredentials(mnemonicOrPassword);

        this.noGasContract = ERC20Interface.load(this.contractAddress, w, new FastRawTransactionManager(this.w, this.credentials, chainID), DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
        this.noGasTokenContract = ERC20Interface.load(this.tokenAddress, w, new FastRawTransactionManager(this.w, this.credentials, chainID), DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
    }

    private ERC20Interface getContract(String destinationAddress, BigInteger tokensAmount) throws IOException {
        BigInteger gasPrice = getCurrentGas();
        return ERC20Interface.load(this.contractAddress, w, new FastRawTransactionManager(this.w, this.credentials, chainID), gasPrice, DefaultGasProvider.GAS_LIMIT);
    }

    private BigInteger getCurrentGas() throws IOException {
        URL url = new URL("https://" + this.urlPolygonAPI);
        BetVerseGasPriceResult result = new BetVerseGasPriceResult();

        URLConnection urlc = url.openConnection();
        urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);
        PrintStream ps = new PrintStream(urlc.getOutputStream());
        ps.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        String l = null;
        while ((l=br.readLine())!=null) {
            System.out.println((l));
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(l, BetVerseGasPriceResult.class);
        }
        br.close();

        BigInteger roundValue = new BigInteger(String.valueOf(Math.round(Float.parseFloat(result.result.FastGasPrice))));
        BigInteger value = new BigInteger(roundValue + "000000000");
        return value;
    }

    private BigDecimal convertToBigDecimal(BigInteger value) {
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
            BigInteger amount = noGasTokenContract.balanceOf(this.contractAddress).send();
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
            log.error("ERC20 wallet error: Not enough tokens. Balance is: " + cryptoBalance + " " + cryptoCurrency
                    + ". Trying to send: " + amount + " " + cryptoCurrency);
            return null;
        }

        try {
            BigInteger tokens = convertFromBigDecimal(amount);
            BigInteger price = getContract(destinationAddress, tokens).AtmPriceICO().send();
            BigDecimal priceInDecimal = convertToBigDecimal(price);

            BigDecimal totalAmountToBuy = amount.multiply(priceInDecimal);
            tokens = convertFromBigDecimal(totalAmountToBuy);

            TransactionReceipt receipt = getContract(destinationAddress, tokens)
                    .buy(destinationAddress, tokens, this.daiAddress)
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
