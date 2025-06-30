package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.exception;

/**
 * Exception thrown to indicate that an invalid identity verification provider was used or configured.
 * This exception is typically thrown when the identity verification provider does not meet the required criteria
 * or when there is an issue in the configuration or initialization process of the provider.
 *
 * <p>The purpose of this exception is to specifically signal configuration or initialization errors related to
 * identity verification providers, allowing for targeted handling of such cases.
 */
public class InvalidIdentityVerificationProviderException extends Exception {
    public InvalidIdentityVerificationProviderException(String message) {
        super(message);
    }
}
