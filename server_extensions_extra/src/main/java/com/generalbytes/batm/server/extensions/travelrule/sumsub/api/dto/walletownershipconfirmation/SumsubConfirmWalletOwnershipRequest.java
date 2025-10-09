package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.walletownershipconfirmation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object for confirm wallet ownership.
 * Used in {@link SumsubTravelRuleApi#confirmWalletOwnership}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubConfirmWalletOwnershipRequest {
    private ApplicantParticipant applicantParticipant;

    /**
     * Info about applicant participant.
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApplicantParticipant {
        private String fullName;
        private String externalUserId;
        private String type;
    }
}
