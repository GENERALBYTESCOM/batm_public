package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto;

import java.util.List;

public class CryptXSendTransactionRequest {

	private String address;

	private String value;

	private List<AddressValuePair> addressValues;

	private String uniqueId;

	private int blocksSize;

	private String customFeePrice;

	private String customGasLimit;

	private boolean subtractFeeFromOutputs;

	private String passphrase;

	public CryptXSendTransactionRequest(String address, String value, String uniqueId, int blocksSize, String customFeePrice, String customGasLimit, String passphrase) {
		this.address = address;
		this.value = value;
		this.uniqueId = uniqueId;
		this.blocksSize = blocksSize != 0 ? blocksSize : 2;
		this.customFeePrice = customFeePrice;
		this.customGasLimit = customGasLimit;
		this.subtractFeeFromOutputs = false;
		this.addressValues = null;
		this.passphrase = passphrase;
	}

	public CryptXSendTransactionRequest(List<AddressValuePair> addressValues, String uniqueId, int blocksSize, String customFeePrice, String customGasLimit, String passphrase) {
		this.address = null;
		this.value = null;
		this.uniqueId = uniqueId;
		this.blocksSize = blocksSize != 0 ? blocksSize : 2;
		this.subtractFeeFromOutputs = false;
		this.addressValues = addressValues;
		this.customFeePrice = customFeePrice;
		this.customGasLimit = customGasLimit;
		this.passphrase = passphrase;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<AddressValuePair> getAddressValues() {
		return addressValues;
	}

	public void setAddressValues(List<AddressValuePair> addressValues) {
		this.addressValues = addressValues;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public int getBlocksSize() {
		return blocksSize;
	}

	public void setBlocksSize(int blocksSize) {
		this.blocksSize = blocksSize;
	}

	public String getCustomFeePrice() {
		return customFeePrice;
	}

	public void setCustomFeePrice(String customFeePrice) {
		this.customFeePrice = customFeePrice;
	}

	public String getCustomGasLimit() {
		return customGasLimit;
	}

	public void setCustomGasLimit(String customGasLimit) {
		this.customGasLimit = customGasLimit;
	}

	public boolean isSubtractFeeFromOutputs() {
		return subtractFeeFromOutputs;
	}

	public void setSubtractFeeFromOutputs(boolean subtractFeeFromOutputs) {
		this.subtractFeeFromOutputs = subtractFeeFromOutputs;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public static class AddressValuePair {
		public String address;
		public String value;

		public AddressValuePair(String address, String value) {
			this.address = address;
			this.value = value;
		}
	}
}
