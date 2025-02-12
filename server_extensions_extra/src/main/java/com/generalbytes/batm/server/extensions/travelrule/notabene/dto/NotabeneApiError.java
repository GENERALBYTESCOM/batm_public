package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * General Notabene Api error response.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneApiError {

    /**
     * The name of the error.
     */
    private String name;
    /**
     * The HTTP status code.
     */
    private int code;
    /**
     * The error message.
     */
    private String message;
    /**
     * The error stack message.
     */
    private String stack;

}
