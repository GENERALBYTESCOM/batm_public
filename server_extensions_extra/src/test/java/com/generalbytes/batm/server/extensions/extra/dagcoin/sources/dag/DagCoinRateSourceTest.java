package com.generalbytes.batm.server.extensions.extra.dagcoin.sources.dag;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;

public class DagCoinRateSourceTest {
	
	private DagCoinRateSource rateSource;
	
	@Before
	public void setUp() {
		this.rateSource = new DagCoinRateSource(FiatCurrency.EUR.getCode());
	}
	
	@Test
	public void testGetCryptoCurrencies() {
		int num = this.rateSource.getCryptoCurrencies().size();
		String currency = this.rateSource.getCryptoCurrencies().iterator().next();
		
		System.out.println("Crypto currency supported - " + currency);
		
		assertTrue(num == 1);
		assertTrue(currency.equals(CryptoCurrency.DAG.code));
	}
	
	@Test
	public void testGetFiatCurrencie() {
		int num = this.rateSource.getFiatCurrencies().size();
		String currency = this.rateSource.getFiatCurrencies().iterator().next();
		
		System.out.println("Fiat currency supported - " + currency);
		
		assertTrue(num == 1);
		assertTrue(currency.equals(FiatCurrency.EUR.getCode()));
	}
	
	@Test
	public void testGetPreferredFiatCurrency() {
		String currency = this.rateSource.getPreferredFiatCurrency();
		System.out.println("Preferred fiat currency - " + currency);
		assertTrue(currency.equals(FiatCurrency.EUR.getCode()));
	}
	
	@Test
	public void testGetExchangeRateLast() {
		BigDecimal rate = this.rateSource.getExchangeRateLast(CryptoCurrency.DAG.code, FiatCurrency.EUR.getCode());
		System.out.println("Exchange rate - " + rate);
		assertNotNull(rate);
	}

}
