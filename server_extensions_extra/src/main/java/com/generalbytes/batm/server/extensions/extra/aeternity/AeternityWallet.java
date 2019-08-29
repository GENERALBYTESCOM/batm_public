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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.Network;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.account.AccountService;
import com.kryptokrauts.aeternity.sdk.service.account.AccountServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainService;
import com.kryptokrauts.aeternity.sdk.service.chain.ChainServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionService;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.transaction.TransactionServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

public class AeternityWallet implements IWallet{
    private String cryptoCurrency = CryptoCurrency.AE.getCode();
    private BaseKeyPair credentials;
    private List<String >mnemonicSeedWords = new ArrayList<String>();
    private String mnemonicPassword = null;
    final static String baseUrl = "https://sdk-mainnet.aepps.com/v2"; // default: https://sdk-testnet.aepps.com/v2
    final Network mainnet = Network.MAINNET;
    private static ServiceConfiguration serviceConf;
    private static AccountService accountService;
    private static ChainService chainService;
    private static TransactionService transactionService;
    private static final Logger log = LoggerFactory.getLogger(AeternityWallet.class);

    public AeternityWallet(String mnemonic)  {
    	serviceConf = ServiceConfiguration.configure().baseUrl(baseUrl).compile();
    	accountService = new AccountServiceFactory().getService(serviceConf);
    	chainService = new ChainServiceFactory().getService(serviceConf);
    	transactionService = new TransactionServiceFactory().getService(TransactionServiceConfiguration.configure().baseUrl(baseUrl).network(mainnet).compile());

    	final KeyPairService keyPairService = new KeyPairServiceFactory().getService();
    	MnemonicKeyPair master = null;
		try {
			if (mnemonic == null) {
				mnemonicPassword = PasswordGenerator.generateRandomPassword(12);
				master = keyPairService.generateMasterMnemonicKeyPair(mnemonicPassword);
				mnemonicSeedWords.addAll(master.getMnemonicSeedWords());
				System.out.println("generating mnemonic words: " + getMnemonicSeedWordsSerialized());
				
			}
			else {
				mnemonicSeedWords.addAll(Arrays.asList(mnemonic.split("\\s*,\\s*")));
				mnemonicPassword = mnemonicSeedWords.remove(mnemonicSeedWords.size() - 1);
				master = keyPairService.recoverMasterMnemonicKeyPair(mnemonicSeedWords, mnemonicPassword);
			}
	    	// derive a key                    
	    	credentials = EncodingUtils.createBaseKeyPair(
	    	                                keyPairService.generateDerivedKey(master, true).toRawKeyPair());
			
		} catch (AException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public String getMnemonicSeedWordsSerialized() {
    	List<String> mnemonicWord = new ArrayList<String>();
    	mnemonicWord.addAll(mnemonicSeedWords);
    	mnemonicWord.add(mnemonicPassword);
		return mnemonicWord.toString().replace(" ", "");
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
            log.error("Aeternity wallet error: unknown cryptocurrency.");
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
        return getAccountBalance(credentials.getPublicKey());
    }
    
    public BigDecimal getReceivedAmount(String address, String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Aeternity wallet error: unknown cryptocurrency.");
            return null;
        }
        return getAccountBalance(address);
    }

    private BigDecimal getAccountBalance(String address) {
    	BigDecimal returnBalance = BigDecimal.ZERO;
    	try {
			Account account = accountService.getAccount(address).blockingGet();
			BigDecimal divider = new BigDecimal("1000000000000000000");
			BigInteger balance = account.getBalance();
			if (balance.signum() == 1) {
				returnBalance = new BigDecimal(balance);
				returnBalance = returnBalance.divide(divider);
	    	}
    	}catch (Exception e) {
    		if (e.getMessage().contains("Not Found")) return BigDecimal.ZERO;
            log.error("Error reading balance.", e);
            return null;
		}
    	return returnBalance;
    }
    
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("AeternityWallet wallet error: unknown cryptocurrency.");
            return null;
        }
		
		try {
			log.info("AeternityWallet sending coins from " + credentials.getPublicKey() + " to: " + destinationAddress + " " + amount + " " + cryptoCurrency);
			
			Account account = accountService.getAccount(credentials.getPublicKey()).blockingGet();
	        // get block to determine current height for calculation of TTL
	        KeyBlock block = chainService.getCurrentKeyBlock().blockingGet();
	        BigDecimal multiplier = new BigDecimal("1000000000000000000");
	        BigInteger amountToSend = amount.multiply(multiplier).toBigInteger();
        	log.debug("AeternityWallet amountToSend = " + amountToSend);
	        // some payload included within tx
			String payload = "some payload";
			// tx will be valid for the next ten blocks
			BigInteger ttl = block.getHeight().add(BigInteger.TEN);
			// we need to increase the current account nonce by one
			BigInteger nonce = account.getNonce().add(BigInteger.ONE);
			
			AbstractTransaction<?> spendTxWithCalculatedFee =
			         transactionService
			                 .getTransactionFactory()
			                 .createSpendTransaction(
			                		 credentials.getPublicKey(), destinationAddress, amountToSend, payload, ttl, nonce);
			
			// choose one of the spendTx above to create the UnsignedTx-object
			UnsignedTx unsignedTx =
			         transactionService.createUnsignedTransaction(spendTxWithCalculatedFee).blockingGet();
			// sign the tx
			Tx signedTx = transactionService.signTransaction(unsignedTx, credentials.getPrivateKey());
			// hopefully you receive a successful txResponse
			PostTxResponse txResponse = transactionService.postTransaction(signedTx).blockingGet();
			log.debug("AeternityWallet receipt = " + txResponse);
			return txResponse.getTxHash();
	        
		} catch (CryptoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
        return null;
    }
    
    private static class PasswordGenerator {

        private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        private static final String NUMBER = "0123456789";
        private static final String OTHER_CHAR = "@";
        private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
        // optional, make it more random
        private static final String PASSWORD_ALLOW_BASE_SHUFFLE = shuffleString(PASSWORD_ALLOW_BASE);
        private static final String PASSWORD_ALLOW = PASSWORD_ALLOW_BASE_SHUFFLE;
        private static SecureRandom random = new SecureRandom();

        public static String generateRandomPassword(int length) {
            if (length < 1) throw new IllegalArgumentException();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int rndCharAt = random.nextInt(PASSWORD_ALLOW.length());
                char rndChar = PASSWORD_ALLOW.charAt(rndCharAt);
                // debug
                //System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);
                sb.append(rndChar);
            }
            return sb.toString();
        }
        // shuffle
        public static String shuffleString(String string) {
            List<String> letters = Arrays.asList(string.split(""));
            Collections.shuffle(letters);
            return letters.stream().collect(Collectors.joining());
        }
    }
}
