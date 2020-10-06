package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto;

public class CryptXException extends RuntimeException {

    private String errorKey;

    private String errorMessage;

    public String getErrorKey() {
        return errorKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public CryptXException(String errorMessage, String errorKey) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorKey = errorKey;
    }

}
