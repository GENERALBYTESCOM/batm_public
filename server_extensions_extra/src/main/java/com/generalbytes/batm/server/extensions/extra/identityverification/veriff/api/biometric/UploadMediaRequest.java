package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric;

/**
 * Request for uploading media to a Veriff session
 * Used for POST /v1/sessions/:sessionId/media
 */
public record UploadMediaRequest(Image image) {

    public record Image(
        // Type of image
        String context,
        // Base64 encoded image content
        String content
    ) {
    }

    /**
     * Creates a request for uploading biometric data
     *
     * @param base64Data Base64 encoded biometric data
     * @return UploadMediaRequest instance
     */
    public static UploadMediaRequest createBiometricRequest(String base64Data) {
        return new UploadMediaRequest(new Image("face", base64Data));
    }
}