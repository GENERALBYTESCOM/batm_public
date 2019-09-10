package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto;

public class ErrorResponseException extends RuntimeException {
    public String error;
    public Long code;

    @Override
    public String getMessage() {
        return error;
    }

}
