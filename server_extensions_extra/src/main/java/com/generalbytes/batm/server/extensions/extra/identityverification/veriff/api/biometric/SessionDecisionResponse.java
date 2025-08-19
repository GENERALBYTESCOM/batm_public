package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric;

/**
 * Response from the Veriff decision endpoint.
 * Used for GET /v1/sessions/:sessionId/decision
 */
public record SessionDecisionResponse(String status, Verification verification) {

    public record Verification(
        // UUID-v4, which identifies the verification session.
        String id,
        // Status of the verification.
        String status,
        // Reason for the verification decision.
        String reason,
        // Vendor data that was provided when creating the session (identity ID).
        String vendorData
    ) {
    }

    /**
     * Checks if the verification is approved.
     *
     * @return true if the verification is approved, false otherwise
     */
    public boolean isApproved() {
        return verification != null && "approved".equals(verification.status());
    }

    /**
     * Checks if the verification is still in progress (no decision yet).
     *
     * @return true if the verification is still in progress, false otherwise
     */
    public boolean isInProgress() {
        return verification == null || verification.status() == null || "submitted".equals(verification.status());
    }

    /**
     * Checks if the verification is considered as declined.
     *
     * @return true if the verification is considered declined, false otherwise
     */
    public boolean isDeclined() {
        return verification != null && (
            "declined".equals(verification.status()) || // fraud or severe reason
                "resubmission_requested".equals(verification.status()) || // subpar photo or something missing
                "review".equals(verification.status()) // needs human review
        );
    }
}