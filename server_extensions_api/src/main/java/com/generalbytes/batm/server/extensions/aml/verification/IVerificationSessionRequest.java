package com.generalbytes.batm.server.extensions.aml.verification;

/**
 * Describes an identity verification session started externally (e.g. in a web or mobile app)
 * that should be registered in the server.
 *
 * @see com.generalbytes.batm.server.extensions.IExtensionContext#registerVerificationSessionForIdentity(IVerificationSessionRequest)
 */
public interface IVerificationSessionRequest {

    /**
     * Not null. Public ID of the identity being verified. NonNull.
     */
    String getIdentityPublicId();

    /**
     * Not null. Session identifier assigned by the external verification system.
     * Corresponds to the {@link IdentityApplicant#getApplicantId()} of the session.
     */
    String getVerificationSessionId();

    /**
     * Not null. URL the customer uses to complete the verification.
     * The server uses it to reuse the existing session if the customer arrives at a terminal
     * while verification is still in progress.
     */
    String getVerificationWebUrl();
}
