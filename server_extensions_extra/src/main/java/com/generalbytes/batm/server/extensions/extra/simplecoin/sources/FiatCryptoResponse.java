package com.generalbytes.batm.server.extensions.extra.simplecoin.sources;

/**
 * Created by Martin on 21/08/25.
 */
public class FiatCryptoResponse {

    private String status;
    private SimpleCoinResponse response; //Test

    private String error;

    //
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

/*
    {
        "status": "ok",
        "response": {
            "rate": 42529.67891113135,
            "rate_inverse": 2.3512991999999998e-5
        },
        "error": null,
        "error_code": null
    }
 */
