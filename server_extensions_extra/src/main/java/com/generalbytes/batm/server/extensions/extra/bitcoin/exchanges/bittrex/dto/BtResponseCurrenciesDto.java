package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class BtResponseCurrenciesDto {

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("result")
    private List<BtCurrenciesDto> result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BtCurrenciesDto> getResult() {
        return result;
    }

    public void setResult(List<BtCurrenciesDto> result) {
        this.result = result;
    }
}
