package com.generalbytes.batm.server.extensions.travelrule.gtr.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

/**
 * Custom exception thrown when a Global Travel Rule (GTR) API call fails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrApiException extends RuntimeException {

    @Getter
    private final Integer statusCode;
    private final String detailedMessage;

    /**
     * Constructor.
     *
     * @param gtrError An object containing error data.
     */
    @JsonCreator
    public GtrApiException(GtrApiError gtrError) {
        super(getValidMessage(gtrError));

        if (gtrError == null) {
            this.statusCode = null;
            this.detailedMessage = null;
        } else {
            this.statusCode = gtrError.getVerifyStatus();
            this.detailedMessage = getValidDetailMessage(gtrError.getVerifyMessage());
        }
    }

    private static String getValidMessage(GtrApiError gtrError) {
        if (gtrError == null) {
            return "UNEXPECTED RESPONSE";
        }

        if (gtrError.getMsg() == null) {
            return "missing message";
        }

        return gtrError.getMsg();
    }

    private String getValidDetailMessage(String detailMessage) {
        if (detailMessage == null) {
            return "missing detail message";
        }

        return detailMessage;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (detailedMessage != null) {
            message += " (" + detailedMessage + ")";
        }

        return message;
    }

}
