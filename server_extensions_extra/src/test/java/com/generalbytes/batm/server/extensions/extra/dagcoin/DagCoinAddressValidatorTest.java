package com.generalbytes.batm.server.extensions.extra.dagcoin;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.dagcoin.domain.DagCoinParameters;
import com.dagcoin.domain.DagEnvironment;

public class DagCoinAddressValidatorTest {
	
	private DagCoinAddressValidator validator;
	
	@Before
	public void setUp() {
		this.validator = new DagCoinAddressValidator(
				new DagCoinParameters(
						DagEnvironment.DEVELOPMENT, "", "", "", "", ""));
	}
	
	/*
	@Test
	public void testIsAddressValid() {
		String walletId = DagCoinPropertiesLoader.getTestWalletAddress();
		boolean isValid = this.validator.isAddressValid(walletId);
		System.out.println("Wallet address " + walletId + " is valid? - " + isValid);
		assertTrue(isValid);
	}

	@Test
	public void testIsAddressNotValid() {
		String walletId = "7BWIKVYU7F6WGYWADL5ITHTTQSV5X";
		boolean isValid = this.validator.isAddressValid(walletId);
		System.out.println("Wallet address " + walletId + " is valid? - " + isValid);
		assertFalse(isValid);
	}
	*/
}
