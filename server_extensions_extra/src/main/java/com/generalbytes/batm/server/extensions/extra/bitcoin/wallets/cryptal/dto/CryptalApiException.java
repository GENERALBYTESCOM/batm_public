package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import si.mazi.rescu.HttpStatusExceptionSupport;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptalApiException extends HttpStatusExceptionSupport {

    @JsonProperty("messageCode")
    private String messageCode;

    @JsonProperty("messageDescription")
    private String messageDescription;

    public String getMessageCode() {
        return messageCode;
    }

    @Override
    public String getMessage() {
        return messageCode + ": " + messageDescription;
    }
}
