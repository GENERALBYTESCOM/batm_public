package com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api.vo.SumsubApiError;

/**
 * Custom exception thrown when a Sumsub API call fails.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubApiException extends RuntimeException {

    /**
     * Constructor.
     *
     * @param sumsubError An object containing error data.
     */
    @JsonCreator
    public SumsubApiException(SumsubApiError sumsubError) {
        super(getValidMessage(sumsubError));
    }

    private static String getValidMessage(SumsubApiError sumsubError) {
        if (sumsubError == null) {
            return "UNEXPECTED RESPONSE";
        }

        return sumsubError.toString();
    }

}
