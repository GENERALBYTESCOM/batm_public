package com.generalbytes.batm.server.extensions.extra.dagcoin.domain;

import java.math.BigDecimal;

/**
 * Response class for POST /wallet/{walletId}/balance/get
 * 
 * @author shubhrapahwa
 *
 */
public class WalletBalanceResponse {
	
	private BigDecimal amount;
	private BigDecimal pending;

	public BigDecimal getPending() {
		return pending;
	}

	public void setPending(BigDecimal pending) {
		this.pending = pending;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	

}
