package com.generalbytes.batm.server.extensions.aml.verification;

/**
 * Result of a biometric authentication process.
 */
public record BiometricAuthenticationResponse(boolean success, String message, String sessionId) {
    /**
     * Creates a successful result.
     *
     * @param sessionId ID of the verification session
     * @return BiometricAuthenticationResponse instance
     */
    public static BiometricAuthenticationResponse success(String sessionId) {
        return new BiometricAuthenticationResponse(true, "Authentication successful", sessionId);
    }

    /**
     * Creates a failed result.
     *
     * @param message Error message
     * @return BiometricAuthenticationResponse instance
     */
    public static BiometricAuthenticationResponse failure(String message) {
        return new BiometricAuthenticationResponse(false, message, null);
    }
}