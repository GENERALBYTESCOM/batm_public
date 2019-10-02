package com.generalbytes.batm.server.extensions.extra.dagcoin;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dagcoin.domain.DagEnvironment;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.dagcoin.sources.dag.DagCoinRateSource;
import com.generalbytes.batm.server.extensions.extra.dagcoin.wallets.dag.DagWallet;

public class DagCoinExtensionTest {
	
	private DagCoinExtension extension;
	private String walletId;
	
	@Before
	public void setUp() {
		this.extension = new DagCoinExtension(DagEnvironment.DEVELOPMENT);
		this.walletId = DagCoinPropertiesLoader.getTestWalletAddress();
	}

	@Test
	public void testCreateRateSource() {
		// exchangeName:fiatCurrency:apiUrl:publicKey:privateKey:merchantKey
		String dagRate = "dagrate:EUR:apiUrl:publicKey:privateKey:merchantKey";
		DagCoinRateSource rateSource = (DagCoinRateSource) this.extension.createRateSource(dagRate);
		assertTrue(rateSource.getPreferredFiatCurrency().equals(FiatCurrency.EUR.getCode()));
	}

	@Test
	public void testCreateWallet() {
		// walletname:walletId:apiUrl:encryptionKey:publicKey:privateKey
		String walletParams = "dagd:" + this.walletId + ":apiUrl:encryptionKey:publicKey:privateKey:merchantKey";
		DagWallet wallet = (DagWallet) this.extension.createWallet(walletParams);
		assertTrue(wallet.getPreferredCryptoCurrency().equals(CryptoCurrency.DAG.code));
	}

	/*
	@Test
	public void testCreateAddressValidator() {
		DagCoinAddressValidator validator = (DagCoinAddressValidator) this.extension.createAddressValidator(CryptoCurrency.DAG.code);
		assertTrue(validator.isAddressValid(this.walletId));
	}
	*/

	@Test
	public void testGetSupportedCryptoCurrencies() {
		assertTrue(this.extension.getSupportedCryptoCurrencies().iterator().next().equals(CryptoCurrency.DAG.code));
	}

	@Test
	public void testGetName() {
		assertTrue(this.extension.getName().equalsIgnoreCase("BATM DagCoin extension"));
	}
	
	/*
	@Test
	public void testCreatePaperWalletGenerator() {
		String cryptoCurrency = "DAG";
		String onetimePassword = "12345";
		String userLanguage = "en";
		
		IPaperWalletGenerator iPaperWalletGenerator = extension.createPaperWalletGenerator(cryptoCurrency);
		IPaperWallet iPaperWallet = iPaperWalletGenerator.generateWallet(cryptoCurrency, onetimePassword, userLanguage);
		
		System.out.println("Paper Wallet :: Address - " + iPaperWallet.getAddress()
									+  " :: Content - " + iPaperWallet.getContent()
									+  " :: ContentType - " + iPaperWallet.getContentType()
									+  " :: CryptoCurrency - " + iPaperWallet.getCryptoCurrency()
									+  " :: FileExtension - " + iPaperWallet.getFileExtension()
									+  " :: Message - " + iPaperWallet.getMessage()
									+  " :: PrivateKey - " + iPaperWallet.getPrivateKey());
		
		assertNotNull(iPaperWallet.getAddress());
		assertNotNull(iPaperWallet.getContent());
		assertNotNull(iPaperWallet.getContentType());	
		assertNotNull(iPaperWallet.getCryptoCurrency()); 	
		assertNotNull(iPaperWallet.getFileExtension());		
		assertNotNull(iPaperWallet.getMessage());
		assertNotNull(iPaperWallet.getPrivateKey());	
	}
	*/

}
