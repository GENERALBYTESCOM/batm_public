package com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite.dto;

public class RegisterApplicantRequest {
    private String applicantId;
    private String sdkToken;
    private String serverUrl;

    public RegisterApplicantRequest() {}

    public RegisterApplicantRequest(String applicantId, String sdkToken, String serverUrl) {
        this.applicantId = applicantId;
        this.sdkToken = sdkToken;
        this.serverUrl = serverUrl;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getSdkToken() {
        return sdkToken;
    }

    public void setSdkToken(String sdkToken) {
        this.sdkToken = sdkToken;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
