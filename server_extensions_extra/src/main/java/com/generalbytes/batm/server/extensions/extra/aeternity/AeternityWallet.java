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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.aeternity.rpc.AeternityRPCClient;
import com.generalbytes.batm.server.extensions.extra.aeternity.rpc.AeternityRPCClient.RawTransactionImpl;
import com.generalbytes.batm.server.extensions.extra.common.IRPCWallet;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.generalbytes.batm.server.extensions.payment.IPaymentOutput;
import com.kryptokrauts.aeternity.generated.model.Account;
import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.exception.AException;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Transaction;

public class AeternityWallet implements IWallet, IRPCWallet{
    private String cryptoCurrency = CryptoCurrency.AE.getCode();
    private BaseKeyPair credentials;
    private List<String >mnemonicSeedWords = new ArrayList<String>();
    private String mnemonicPassword = null;
    private AeternityRPCClient rpcClient;
    private static final Logger log = LoggerFactory.getLogger(AeternityWallet.class);
    private static final String WALLETS_PATH = "/batm/aeternity_wallets/";
    
    public AeternityWallet(String mnemonic)  {
    	final KeyPairService keyPairService = new KeyPairServiceFactory().getService();
    	MnemonicKeyPair master = null;
    	boolean walletIsNew = false;
    	
		try {
			System.out.println("Load wallet with mnemonics: " + mnemonic);
			rpcClient = new AeternityRPCClient(null);
			if (mnemonic == null || mnemonic.isEmpty()) {
				mnemonicPassword = PasswordGenerator.generateRandomPassword(12);
				master = keyPairService.generateMasterMnemonicKeyPair(mnemonicPassword);
				mnemonicSeedWords.addAll(master.getMnemonicSeedWords());
				walletIsNew = true;
				
			}
			else {
				mnemonicSeedWords.addAll(Arrays.asList(mnemonic.split("\\s*,\\s*")));
				mnemonicPassword = mnemonicSeedWords.remove(mnemonicSeedWords.size() - 1);
				master = keyPairService.recoverMasterMnemonicKeyPair(mnemonicSeedWords, mnemonicPassword);
			}
	    	// derive a key                    
	    	credentials = EncodingUtils.createBaseKeyPair(
	    	                                keyPairService.generateDerivedKey(master, true).toRawKeyPair());
	    	if (walletIsNew) saveAeternityWallet();
		} catch (AException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public String getMnemonicSeedWordsSerialized() {
    	List<String> mnemonicWord = new ArrayList<String>();
    	mnemonicWord.addAll(mnemonicSeedWords);
    	mnemonicWord.add(mnemonicPassword);
		return mnemonicWord.toString().replace(" ", "").replace("]", "").replace("[", "");
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
        return rpcClient.getAccountBalance(credentials.getPublicKey());
    }
    
    public BigDecimal getReceivedAmount(String address, String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Aeternity wallet error: unknown cryptocurrency.");
            return null;
        }
        return rpcClient.getAccountBalance(address);
    }

    
    
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("AeternityWallet wallet error: unknown cryptocurrency.");
            return null;
        }
		
		try {
			log.info("AeternityWallet sending coins from " + credentials.getPublicKey() + " to: " + destinationAddress + " " + amount + " " + cryptoCurrency);
			Tx signedTx = ((AeternityRPCClient)rpcClient).createAndSignTransactionAeternity(credentials.getPublicKey(), destinationAddress, amount);
			String txid = ((AeternityRPCClient)rpcClient).sendSignedTransactionAeternity(signedTx);
			log.debug("AeternityWallet receipt = " + txid);
			return txid;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
        return null;
    }
    
    public String getWalletPrivateKey () {
    	return credentials.getPrivateKey();
    }
    
    public static AeternityWallet loadAeternityWallet(String address) {
    	String mnemonics = null;
    	try(BufferedReader br = new BufferedReader(new FileReader(WALLETS_PATH + address))) {
    	    StringBuilder sb = new StringBuilder();
    	    String line = br.readLine();

    	    while (line != null) {
    	        sb.append(line);
    	        line = br.readLine();
    	    }
    	    mnemonics = sb.toString();
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new AeternityWallet(mnemonics);
    }
    
    //saving mnomonics in file in configured path. later will be used by AeternityRPCClient.createAndSignTransactionAeternity(String, String, BigDecimal) to initialized wallets privKey and sign the transaction.
    //TODO: change plain text format  with some encryption after tests ? 
    public void saveAeternityWallet() {
    	String mnemonics = getMnemonicSeedWordsSerialized();//load them for example from FS by address fileName
    	try (PrintStream out = new PrintStream(new FileOutputStream(WALLETS_PATH + credentials.getPublicKey()))) {
		    out.print(mnemonics);
		}
    	catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public RPCClient getClient() {
		return rpcClient;
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
