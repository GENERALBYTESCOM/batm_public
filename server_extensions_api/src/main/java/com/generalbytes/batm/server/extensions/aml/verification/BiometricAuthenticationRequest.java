package com.generalbytes.batm.server.extensions.aml.verification;

import java.io.InputStream;

/**
 * Request data for a biometric authentication process.
 *
 * @param identityPublicId The identity public ID associated with the biometric data.
 * @param biometricData    InputStream containing the biometric data (face photo).
 */
public record BiometricAuthenticationRequest(String identityPublicId, InputStream biometricData) {
}