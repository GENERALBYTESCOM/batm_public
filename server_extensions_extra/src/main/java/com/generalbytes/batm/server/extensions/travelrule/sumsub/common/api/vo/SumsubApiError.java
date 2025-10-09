package com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * General Sumsub API error response.
 *
 * @see <a href="https://docs.sumsub.com/reference/review-api-health#errors">Sumsub documentation</a>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubApiError {
    private String description;
    private Integer code;
    private String correlationId;
    private Integer errorCode;
    private String errorName;

    @Override
    public String toString() {
        return String.format("%s, error code: %d, error name: %s, HTTP code: %d, correlation ID: %s",
                description, errorCode, errorName, code, correlationId);
    }
}
