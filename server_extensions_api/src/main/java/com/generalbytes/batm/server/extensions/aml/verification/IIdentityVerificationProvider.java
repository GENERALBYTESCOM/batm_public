package com.generalbytes.batm.server.extensions.aml.verification;

public interface IIdentityVerificationProvider {
    /**
     * Creates an applicant that will be used for identification verification
     * @param customerLanguage could be used to display verification website in the same language as the user selected on the terminal. Some providers do not use this and auto detects the language
     * @param identityPublicId Some providers use this in the webhook, or it is just displayed on the provider's dashboard
     */
    CreateApplicantResponse createApplicant(String customerLanguage, String identityPublicId);

    /**
     * Overrides the verification web URL for a specific verification process.
     * The purpose of this method is to enable modification of the verification web URL if necessary.
     * It is expected to fall back to the original URL obtained from {@link #createApplicant(String, String)} for backwards compatibility.
     *
     * @param overrideRequest the request object containing the original verification web URL and additional details needed to process the override.
     * @return an {@code OverrideVerificationWebUrlResponse} object containing the overridden verification web URL, which defaults to the original URL provided in the request.
     */
    default OverrideVerificationWebUrlResponse overrideVerificationWebUrl(OverrideVerificationWebUrlRequest overrideRequest) {
        return new OverrideVerificationWebUrlResponse(overrideRequest.getOriginalVerificationWebUrl());
    }
}
