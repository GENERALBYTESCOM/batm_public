package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

/**
 * Created by Martin on 21/08/25.
 */
public class FiatCryptoResponse {

    private String status;

    private SimpleCoinResponse response;

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public SimpleCoinResponse getResponse() {
        return response;
    }

    public void setResponse(SimpleCoinResponse response) {
        this.response = response;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}