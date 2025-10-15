package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response object containing data about the result of PII verification.
 * Used in {@link GtrApi#verifyPii(String, GtrVerifyPiiRequest)}.
 */
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrVerifyPiiResponse {
    private VerifyPiiData data;
    @Getter
    public boolean success;
    @Getter
    private Integer verifyStatus;
    @Getter
    private String verifyMessage;

    public String getBeneficiaryPublicKey() {
        return data.getBeneficiaryPublicKey();
    }

    public String getEmptyPiiSchema() {
        return data.getEmptyPiiSchema();
    }

    public String getEncryptedPayload() {
        return data.getEncryptedPayload();
    }

    public String getHashSalt() {
        return data.getHashSalt();
    }

    public String getInitiatorPublicKey() {
        return data.getInitiatorPublicKey();
    }

    public String getOriginatorPublicKey() {
        return data.getOriginatorPublicKey();
    }

    public String getReceiverPublicKey() {
        return data.getReceiverPublicKey();
    }

    public String getRequestId() {
        return data.getRequestId();
    }

    public Integer getSecretType() {
        return data.getSecretType();
    }

    public String getTravelRuleId() {
        return data.getTravelruleId();
    }

    public List<GtrVerifyField> getVerifyFields() {
        return data.getVerifyFields();
    }

    @Getter
    private static class VerifyPiiData {
        private String beneficiaryPublicKey;
        private String emptyPiiSchema;
        private String encryptedPayload;
        private String hashSalt;
        private String initiatorPublicKey;
        private String originatorPublicKey;
        private String receiverPublicKey;
        private String requestId;
        private Integer secretType;
        private String travelruleId;
        private List<GtrVerifyField> verifyFields;
    }

}
