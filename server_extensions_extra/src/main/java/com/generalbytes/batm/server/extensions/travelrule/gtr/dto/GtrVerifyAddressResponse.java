package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object containing data about the result of address verification.
 * Used in {@link GtrApi#verifyAddress(String, GtrVerifyAddressRequest)}.
 */
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrVerifyAddressResponse {
    @Getter
    public boolean success;
    private Integer verifyStatus;
    private String verifyMessage;

    public Integer getStatusCode() {
        return verifyStatus;
    }

    public String getMessage() {
        return verifyMessage;
    }
}
