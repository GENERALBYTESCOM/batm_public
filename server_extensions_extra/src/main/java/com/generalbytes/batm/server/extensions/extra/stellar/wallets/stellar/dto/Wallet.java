package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

public class Wallet {
	private String id;
	private String pubkey;
	private String secret;
	private String apiKey;
	private boolean isTestnet;
	private String hostname;
	private String crypto;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCrypto() {
		return crypto;
	}

	public void setCrypto(String crypto) {
		this.crypto = crypto;
	}

	public String getPubkey() {
		return pubkey;
	}

	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public boolean getIsTestnet() {
		return isTestnet;
	}

	public void setIsTestnet(boolean isTestnet) {
		this.isTestnet = isTestnet;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}