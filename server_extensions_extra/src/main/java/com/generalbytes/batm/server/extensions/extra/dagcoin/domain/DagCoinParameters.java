package com.generalbytes.batm.server.extensions.extra.dagcoin.domain;

public class DagCoinParameters {
	
	private String apiUrl;
	private String encryptionKey;
	private String publicKey;
	private String privateKey;
	private String merchantKey;
	
	public DagCoinParameters(String apiUrl, String encryptionKey, String publicKey, String privateKey, String merchantKey) {
		super();
		this.apiUrl = apiUrl;
		this.encryptionKey = encryptionKey;
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.merchantKey = merchantKey;
	}

	public String getApiUrl() {
		return apiUrl;
	}
	
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	public String getEncryptionKey() {
		return encryptionKey;
	}
	
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}
	
	public String getPublicKey() {
		return publicKey;
	}
	
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}	
	
	public String getMerchantKey() {
		return merchantKey;
	}
	
	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}

}
