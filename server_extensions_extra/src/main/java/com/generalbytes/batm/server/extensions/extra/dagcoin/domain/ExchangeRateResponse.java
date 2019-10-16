package com.generalbytes.batm.server.extensions.extra.dagcoin.domain;

import java.math.BigDecimal;

/**
 * Response class for GET /dagcoin/exchangeRate
 * 
 * @author shubhrapahwa
 *
 */
public class ExchangeRateResponse {
	
	private BigDecimal rate;
	private String currencyPair;


	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getCurrencyPair() {
		return currencyPair;
	}

	public void setCurrency(String currencyPair) {
		this.currencyPair = currencyPair;
	}
}
