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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.aeternity.rpc.AeternityRPCClient;
import com.generalbytes.batm.server.extensions.extra.common.IRPCWallet;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.BaseKeyPair;
import com.kryptokrauts.aeternity.sdk.domain.secret.impl.MnemonicKeyPair;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairService;
import com.kryptokrauts.aeternity.sdk.service.keypair.KeyPairServiceFactory;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;


public class AeternityWallet implements IWallet, IRPCWallet{
    private String cryptoCurrency = CryptoCurrency.AE.getCode();
    private BaseKeyPair credentials;
    private List<String >mnemonicSeedWords = new ArrayList<String>();
    private String mnemonicPassword = null;
    private AeternityRPCClient rpcClient;
    private static final Logger log = LoggerFactory.getLogger(AeternityWallet.class);
    
    public AeternityWallet(String mnemonicsOrPrivateKey)  {
    	final KeyPairService keyPairService = new KeyPairServiceFactory().getService();
    	MnemonicKeyPair master = null;
    	boolean tryMnemonicRecover = false;
		
		try {
			rpcClient = new AeternityRPCClient(null);
			credentials = keyPairService.generateBaseKeyPairFromSecret(mnemonicsOrPrivateKey);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			tryMnemonicRecover = true;
			e.printStackTrace();
		}
		if (tryMnemonicRecover) {
			try {
				mnemonicSeedWords.addAll(Arrays.asList(mnemonicsOrPrivateKey.split("\\s*,\\s*")));
				mnemonicPassword = mnemonicSeedWords.remove(mnemonicSeedWords.size() - 1);
				master = keyPairService.recoverMasterMnemonicKeyPair(mnemonicSeedWords, mnemonicPassword);
				credentials = EncodingUtils.createBaseKeyPair(
	                    keyPairService.generateDerivedKey(master, true).toRawKeyPair());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
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
			Tx signedTx = ((AeternityRPCClient)rpcClient).createAndSignTransaction(credentials.getPublicKey(), destinationAddress, amount, credentials.getPrivateKey());
			String txid = ((AeternityRPCClient)rpcClient).broadcastTransaction(signedTx);
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
    
    public RPCClient getClient() {
		return rpcClient;
	}
}
