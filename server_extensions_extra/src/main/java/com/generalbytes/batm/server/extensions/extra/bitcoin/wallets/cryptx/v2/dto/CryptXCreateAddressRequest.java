package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto;

public class CryptXCreateAddressRequest {

    private String name;

    private AddressFormat addressFormat;

    private AddressType addressType;

    private String passphrase;

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

    public CryptXCreateAddressRequest(String name, String passphrase) {
        this.name = name;
        this.passphrase = passphrase;
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

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

}
