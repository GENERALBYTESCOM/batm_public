package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

public class ErrorResponseException extends RuntimeException {
    public String error;

    @Override
    public String getMessage() {
        return error;
    }

}
