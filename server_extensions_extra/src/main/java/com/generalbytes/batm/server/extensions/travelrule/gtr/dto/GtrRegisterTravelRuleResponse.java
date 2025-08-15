package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object containing basic data about VASP.
 * Used in {@link GtrApi#vaspDetail(String, String)}.
 */
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrRegisterTravelRuleResponse {
    /**
     * Data object containing Request ID and Travel Rule ID.
     */
    private RegisterTravelRuleData data;

    public String getRequestId() {
        return data.getRequestId();
    }

    public String getTravelRuleId() {
        return data.getTravelruleId();
    }

    @Getter
    private static class RegisterTravelRuleData {
        private String requestId;
        private String travelruleId;
    }
}
