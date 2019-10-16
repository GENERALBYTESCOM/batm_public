package com.generalbytes.batm.server.extensions.extra.dagcoin.domain;

import java.math.BigDecimal;

public class CustomerWalletBalanceResponse {
	
	private BigDecimal balance;

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = new BigDecimal(balance);
	}

}
