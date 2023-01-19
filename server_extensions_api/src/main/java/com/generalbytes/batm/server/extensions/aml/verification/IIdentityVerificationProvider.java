package com.generalbytes.batm.server.extensions.aml.verification;

import com.generalbytes.batm.server.extensions.IIdentity;

public interface IIdentityVerificationProvider {
    /**
     * Creates an applicant that will be used for identification verification
     * @param identity null if this is called on global server from a delegating provider on a standalone server
     * @param gbApiKey
     * @param customerLanguage could be used to display verification website in the same language as the user selected on the terminal. Some providers do not use this and auto detects the language
     * @param vendorData vendor specific data. Some providers send it back in the webhook, or it is just displayed on the provider's dashboard
     */
    CreateApplicantResponse createApplicant(IIdentity identity, String gbApiKey, String customerLanguage, String vendorData);

    /**
     * Should be called after all documents and data are uploaded to applicant. Starts verification process on
     * external service.
     * Called by the verification website.
     * Some providers (Veriff) start the check automatically after creating the Applicant and this is not called
     * @param applicantId - external id
     * @return checkId - id of check in external service
     */
    String submitCheck(String applicantId);

    /**
     * Processes verification results sent from the verification provider
     * @param webhookKey some implementations (onfido) could use this to identify
     *                   the organization / identity / verification Applicant that this webhook belongs to
     */
    void processWebhookEvent(String rawPayload, String signature, String webhookKey) throws IdentityCheckWebhookException;


}
