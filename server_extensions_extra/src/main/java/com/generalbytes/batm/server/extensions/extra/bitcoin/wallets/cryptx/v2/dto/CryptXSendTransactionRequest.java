package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto;

import java.math.BigInteger;
import java.util.List;

public class CryptXSendTransactionRequest {

	private String address;

	private BigInteger value;

	private List<AddressValuePair> addressValues;

	private String uniqueId;

    private String description;

	private int blocksSize;

	private String customFeePrice;

	private String customGasLimit;

	private boolean subtractFeeFromOutputs;

	private String password;

	public CryptXSendTransactionRequest(String address, BigInteger value, String uniqueId, int blocksSize, String customFeePrice, String customGasLimit, String password) {
		this.address = address;
		this.value = value;
		this.uniqueId = uniqueId;
        this.description = "Description of " + uniqueId;
		this.blocksSize = blocksSize != 0 ? blocksSize : 2;
		this.customFeePrice = customFeePrice;
		this.customGasLimit = customGasLimit;
		this.subtractFeeFromOutputs = false;
		this.addressValues = null;
		this.password = password;
	}

	public CryptXSendTransactionRequest(List<AddressValuePair> addressValues, String uniqueId, int blocksSize, String customFeePrice, String customGasLimit, String password) {
		this.address = null;
		this.value = null;
		this.uniqueId = uniqueId;
        this.description = "Description of " + uniqueId;
		this.blocksSize = blocksSize != 0 ? blocksSize : 2;
		this.subtractFeeFromOutputs = false;
		this.addressValues = addressValues;
		this.customFeePrice = customFeePrice;
		this.customGasLimit = customGasLimit;
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigInteger getValue() {
		return value;
	}

	public void setValue(BigInteger value) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static class AddressValuePair {
		public String address;
		public BigInteger value;

		public AddressValuePair(String address, BigInteger value) {
			this.address = address;
			this.value = value;
		}
	}
}
