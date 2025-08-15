package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric;

/**
 * Response from submitting a Veriff session for verification
 * Used for PATCH /v1/sessions/:sessionId
 */
public record SubmitSessionResponse(String status) {

    /**
     * Checks if the submission was successful
     *
     * @return true if status equals "success", false otherwise
     */
    public boolean isSuccess() {
        return "success".equals(status);
    }

}