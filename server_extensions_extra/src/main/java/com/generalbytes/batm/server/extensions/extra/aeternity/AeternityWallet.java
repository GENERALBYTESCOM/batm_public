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
package com.generalbytes.batm.server.extensions.extra.aeternity;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;

import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.AccountServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.math.BigInteger;

import static org.web3j.utils.Convert.Unit.ETHER;

public class AeternityWallet implements IWallet{
    private String cryptoCurrency = CryptoCurrency.AE.getCode();
    private BaseKeyPair credentials;
    //private Web3j w;
    final static String baseUrl = "https://sdk-mainnet.aepps.com/v2"; // default: https://sdk-testnet.aepps.com/v2
    final Network mainnet = Network.MAINNET; // default: TESTNET -> ae_uat
    private static ServiceConfiguration serviceConf;
    private static AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(AeternityWallet.class);

    public AeternityWallet(String password, String mnemonic)  {
    	serviceConf = ServiceConfiguration.configure().baseUrl(baseUrl).compile();
    	accountService = new AccountServiceFactory().getService(serviceConf);
    	final KeyPairService keyPairService = new KeyPairServiceFactory().getService();
    	MnemonicKeyPair master = null;
		try {
			if (mnemonic == null) {
				master = keyPairService.generateMasterMnemonicKeyPair(password);
		    	System.out.println("generating mnemonic words: " + master.getMnemonicSeedWords());
			}
			else {
				List<String> mnemonicWords = Arrays.asList(mnemonic.split("\\s*,\\s*"));
				master = keyPairService.recoverMasterMnemonicKeyPair(mnemonicWords, password);
			}
	    	// derive a key                    
	    	credentials = EncodingUtils.createBaseKeyPair(
	    	                                keyPairService.generateDerivedKey(master, true).toRawKeyPair());
			
		} catch (AException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
        return credentials.getPublicKey();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Aeternity wallet error: unknown cryptocurrency.");
            return null;
        }
        try {
        	Account account = accountService.getAccount(credentials.getPublicKey()).blockingGet();
        	//Account account = accountService.getAccount("ak_22eGVeNdHCPV7Sy5X6jrYY5AL7RVHvTwNDECMX5yjEXMEkpjWj").blockingGet();
        	BigInteger balance = account.getBalance();
            return new BigDecimal(balance); 
        } catch (Exception e) {
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
            log.info("InfuraWallet sending coins from " + "credentials.getAddress()" + " to: " + destinationAddress + " " + amount + " " + cryptoCurrency);

            CompletableFuture<TransactionReceipt> future = Transfer.sendFunds(
                null, null,
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
