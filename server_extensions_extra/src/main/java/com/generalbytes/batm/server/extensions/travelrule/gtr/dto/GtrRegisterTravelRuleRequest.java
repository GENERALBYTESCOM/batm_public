package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.RequestIdGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object containing generated Request ID from {@link RequestIdGenerator}.
 * Used in {@link GtrApi#registerTravelRuleRequest(String, GtrRegisterTravelRuleRequest)}.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GtrRegisterTravelRuleRequest {
    /**
     * Newly generated Request ID from {@link RequestIdGenerator}.
     */
    private String requestId;
    /**
     * Source VASP code.
     */
    private String sourceVaspCode;
    /**
     * Verify type. According to the documentation and the response from the GTR server, it should take the value = {@code 4}.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#initiator-api-2-register-travel-rule-request">Global Travel Rule (GTR) documentation</a>
     */
    private Integer verifyType;
}
