package com.generalbytes.batm.server.extensions.extra.dagcoin.wallets.dag;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.dagcoin.domain.DagCoinParameters;
import com.dagcoin.domain.DagEnvironment;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.dagcoin.DagCoinPropertiesLoader;

public class DagWalletTest {
	
	private DagWallet wallet;
	private String walletID;
	
	@Before
	public void setUp() {
		this.walletID = DagCoinPropertiesLoader.getTestWalletAddress();
		this.wallet = new DagWallet(this.walletID, 
				new DagCoinParameters(DagEnvironment.DEVELOPMENT, "", "", "", "", ""));
	}
	
	@Test
	public void testGetCryptoAddress() {
		String address = this.wallet.getCryptoAddress(CryptoCurrency.DAG.code);
		System.out.println("Crypto address - " + address);
		assertTrue(address.equalsIgnoreCase(this.walletID));
	}
	
	@Test
	public void testGetCryptoCurrencies() {
		String code = this.wallet.getCryptoCurrencies().iterator().next();
		System.out.println("Crypto currency - " + code);
		assertTrue(code.equals(CryptoCurrency.DAG.code));
	}
	
	@Test
	public void testGetPreferredCryptoCurrency() {
		String code = this.wallet.getPreferredCryptoCurrency();
		System.out.println("Preferred crypto currency - " + code);
		assertTrue(code.equals(CryptoCurrency.DAG.code));
	}
	
	@Test
	public void testGetCryptoBalance() {
		BigDecimal balance = this.wallet.getCryptoBalance(CryptoCurrency.DAG.code);
		System.out.println("Balance of wallet - " + balance);
		assertNotNull(balance);
	}

	@Test
	public void testSendCoins() {
		BigDecimal amount = new BigDecimal(1);
		String txnID = this.wallet.sendCoins(this.wallet.getCryptoAddress(CryptoCurrency.DAG.code), amount, CryptoCurrency.DAG.code, "Test");
		System.out.println(
				"Transaction of " + amount +
				" successful for - " + this.wallet.getCryptoAddress(CryptoCurrency.DAG.code) +
				" with Transaction ID - " + txnID
				);
		assertNotNull(txnID);
	}

}
