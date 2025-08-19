package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric;

/**
 * Response from uploading media to a Veriff session
 * Used for POST /v1/sessions/:sessionId/media
 */
public record UploadMediaResponse(String status) {

    /**
     * Checks if the upload was successful
     *
     * @return true if status equals "success", false otherwise
     */
    public boolean isSuccess() {
        return "success".equals(status);
    }
}