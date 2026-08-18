package com.generalbytes.batm.server.extensions.aml.verification;

/**
 * Thrown when registering an identity verification session fails.
 *
 * @see com.generalbytes.batm.server.extensions.IExtensionContext#registerVerificationSessionForIdentity(IVerificationSessionRequest)
 */
public class RegisterVerificationException extends Exception {

    public RegisterVerificationException(String message) {
        super(message);
    }
}
