package com.generalbytes.batm.server.extensions.aml.verification;

public class CreateApplicantResponse {
    private String applicantId;
    private String verificationWebUrl;
    private boolean chargeable = false;

    public CreateApplicantResponse() {
    }

    public CreateApplicantResponse(String applicantId, String verificationWebUrl) {
        this.applicantId = applicantId;
        this.verificationWebUrl = verificationWebUrl;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public String getVerificationWebUrl() {
        return verificationWebUrl;
    }

    public boolean isChargeable() {
        return chargeable;
    }

    /**
     * Setting this to true will result in the operator being charged for this identity verification.
     * Used for the "GB Cloud" verification option
     */
    public void setChargeable(boolean chargeable) {
        this.chargeable = chargeable;
    }
}
