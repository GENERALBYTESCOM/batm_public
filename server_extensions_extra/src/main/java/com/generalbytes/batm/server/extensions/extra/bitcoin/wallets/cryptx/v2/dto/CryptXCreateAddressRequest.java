package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto;

public class CryptXCreateAddressRequest {

    private String name;

    private AddressFormat addressFormat;

    private AddressType addressType;

    private String password;

    public enum AddressFormat {
        LEGACY,
        P2SH_SEGWIT,
        BECH32,
        CASH_ADDRESS,
        SAPLING
    }

    public enum AddressType {
        DEPOSIT
    }

    public CryptXCreateAddressRequest(String name, String password) {
        this.name = name;
        this.password = password;
        this.addressFormat = AddressFormat.P2SH_SEGWIT;
        this.addressType = AddressType.DEPOSIT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressFormat getAddressFormat() {
        return addressFormat;
    }

    public void setAddressFormat(AddressFormat addressFormat) {
        this.addressFormat = addressFormat;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
