package com.generalbytes.batm.server.extensions.extra.dagcoin.domain;

public class PaperWalletResponse {
	
	private String walletId;
	private String cardId;
	private String atmPin;
	
	public String getAtmPin() {
		return atmPin;
	}

	public void setAtmPin(String atmPin) {
		this.atmPin = atmPin;
	}

	public String getWalletId() {
		return walletId;
	}
	
	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}
	
	public String getCardId() {
		return cardId;
	}
	
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

}
