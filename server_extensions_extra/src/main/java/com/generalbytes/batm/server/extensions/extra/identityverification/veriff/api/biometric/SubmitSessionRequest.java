package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric;

/**
 * Request for submitting a Veriff session
 * Used for PATCH /v1/sessions/:sessionId
 */
public record SubmitSessionRequest(Verification verification) {
    /**
     * Status of the verification session
     */
    public record Verification(String status) {
    }

    /**
     * Creates a request for submitting a session
     *
     * @return SubmitSessionRequest instance with the status "submitted"
     */
    public static SubmitSessionRequest createSubmitRequest() {
        return new SubmitSessionRequest(new Verification("submitted"));
    }
}