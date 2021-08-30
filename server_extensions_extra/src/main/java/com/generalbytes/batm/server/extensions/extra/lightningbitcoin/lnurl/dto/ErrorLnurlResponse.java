package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.lnurl.dto;

public class ErrorLnurlResponse {
    private final String status = "ERROR";
    private final String reason;

    public ErrorLnurlResponse(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

}
