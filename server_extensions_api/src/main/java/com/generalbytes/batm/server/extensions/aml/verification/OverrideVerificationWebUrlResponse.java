package com.generalbytes.batm.server.extensions.aml.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object for overriding verification URL.
 */
@Getter
@Setter
@AllArgsConstructor
public class OverrideVerificationWebUrlResponse {

    /**
     * The overridden verification URL (verification link).
     * <b>It must not be null or blank.</b>
     */
    private String verificationWebUrl;

    /**
     * Flag to control whether the verification link should be sent via SMS.
     * When false (default), the verification link will be sent automatically via SMS.
     * When true, sending SMS will be skipped. This allows distributing the link separately or via different channel.
     */
    private boolean skipSendingSms = false;

    public OverrideVerificationWebUrlResponse(String verificationWebUrl) {
        this.verificationWebUrl = verificationWebUrl;
    }

}
