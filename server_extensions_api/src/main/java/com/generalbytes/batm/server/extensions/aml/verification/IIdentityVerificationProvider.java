package com.generalbytes.batm.server.extensions.aml.verification;

import com.generalbytes.batm.server.extensions.IIdentity;

public interface IIdentityVerificationProvider {
    /**
     * Creates an applicant that will be used for identification verification
     * @param gbApiKey
     * @param customerLanguage could be used to display verification website in the same language as the user selected on the terminal. Some providers do not use this and auto detects the language
     * @param identityPublicId Some providers use this in the webhook, or it is just displayed on the provider's dashboard
     */
    CreateApplicantResponse createApplicant(String gbApiKey, String customerLanguage, String identityPublicId);

}
