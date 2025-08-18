package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents the response sent in reaction to a received webhook message for PII verification.
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/getting-started-with-vasp-solution-and-integration/travel-rule-standard-2-0-solution#receiver-callback-api-3-pii-verification">Global Travel Rule (GTR) documentation</a>
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GtrWebhookVerifyPiiResponse extends GtrWebhookMessageResponse {

    private final VerifyPiiData data = new VerifyPiiData();

    /**
     * Data for PII verification response.
     */
    @Getter
    @Setter
    public static class VerifyPiiData {
        private String encryptedPayload;
        private String initiatorPublicKey;
        private String receiverPublicKey;
        private int secretType;
        private List<GtrVerifyField> verifyFields;
    }

}
