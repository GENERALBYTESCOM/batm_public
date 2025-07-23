package com.generalbytes.batm.server.extensions.aml.verification;

/**
 * Response object for overriding verification URL.
 */
public class OverrideVerificationWebUrlResponse {

    /**
     * The overridden verification URL (verification link).
     */
    private String verificationWebUrl;

    /**
     * Flag to control whether the verification link should be sent via SMS.
     * When false (default), the verification link will be sent automatically via SMS.
     * When true, sending SMS will be skipped. This allows distributing the link separately or via different channel.
     */
    private boolean skipSendingSms = false;

    public OverrideVerificationWebUrlResponse() {
    }

    public OverrideVerificationWebUrlResponse(String verificationWebUrl) {
        this.verificationWebUrl = verificationWebUrl;
    }

    public OverrideVerificationWebUrlResponse(String verificationWebUrl, boolean skipSendingSms) {
        this.verificationWebUrl = verificationWebUrl;
        this.skipSendingSms = skipSendingSms;
    }

    public String getVerificationWebUrl() {
        return verificationWebUrl;
    }

    public void setVerificationWebUrl(String verificationWebUrl) {
        this.verificationWebUrl = verificationWebUrl;
    }

    public boolean isSkipSendingSms() {
        return skipSendingSms;
    }

    public void setSkipSendingSms(boolean skipSendingSms) {
        this.skipSendingSms = skipSendingSms;
    }
}
