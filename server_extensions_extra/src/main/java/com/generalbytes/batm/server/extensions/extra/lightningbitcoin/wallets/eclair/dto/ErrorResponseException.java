package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

public class ErrorResponseException extends RuntimeException {
    public String error;

    @Override
    public String getMessage() {
        return error;
    }

}
