package com.generalbytes.batm.server.extensions.extra.dagcoin.domain;

/**
 * Request class for POST /transaction/make
 * 
 * @author shubhrapahwa
 *
 */
public class TransactionRequest {
	
	private String recipientWalletId;
	private String currency;
	private String amount;
	
	public TransactionRequest(String recipentWalletId, String currency, String amount) {
		super();
		this.recipientWalletId = recipentWalletId;
		this.currency = currency;
		this.amount = amount;
	}
	
	public String getRecipientWalletId() {
		return recipientWalletId;
	}
	
	public void setRecipientWalletId(String recipientWalletId) {
		this.recipientWalletId = recipientWalletId;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getAmount() {
		return amount;
	}
	
	public void setAmount(String amount) {
		this.amount = amount;
	}

}
