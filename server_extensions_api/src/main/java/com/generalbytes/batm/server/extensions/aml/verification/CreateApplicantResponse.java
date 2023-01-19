package com.generalbytes.batm.server.extensions.aml.verification;

public class CreateApplicantResponse {
    private String applicantId;
    private String sdkToken;
    private String verificationWebUrl;

    public CreateApplicantResponse() {}

    public CreateApplicantResponse(String applicantId, String sdkToken, String verificationWebUrl) {
        this.applicantId = applicantId;
        this.sdkToken = sdkToken;
        this.verificationWebUrl = verificationWebUrl;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public String getSdkToken() {
        return sdkToken;
    }

    public String getVerificationWebUrl() {
        return verificationWebUrl;
    }
}
