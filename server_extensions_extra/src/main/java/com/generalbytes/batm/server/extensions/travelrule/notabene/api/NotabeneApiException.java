package com.generalbytes.batm.server.extensions.travelrule.notabene.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneApiError;
import lombok.Getter;

/**
 * Custom exception thrown when a Notabene API call fails
 * and the API provides a detailed error description.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneApiException extends RuntimeException {

    /**
     * The name of the error.
     */
    private final String name;
    /**
     * The HTTP status code.
     */
    private final int code;
    /**
     * The error stack message.
     */
    private final String stack;

    @JsonCreator
    public NotabeneApiException(@JsonProperty("err") NotabeneApiError notabeneError) {
        super(notabeneError.getMessage());
        this.name = notabeneError.getName();
        this.code = notabeneError.getCode();
        this.stack = notabeneError.getStack();
    }

}
