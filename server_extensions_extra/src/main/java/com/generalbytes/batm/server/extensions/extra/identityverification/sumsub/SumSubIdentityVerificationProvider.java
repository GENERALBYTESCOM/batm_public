package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.aml.verification.CreateApplicantResponse;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantIdResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.BaseWebhookBody;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityVerificationSessionResponse;
import lombok.extern.slf4j.Slf4j;
import si.mazi.rescu.HttpStatusIOException;

/**
 * The SumSubIdentityVerificationProvider class is responsible for handling identity verifications using the SumSub API.
 * This class integrates with the SumSubApiService for API calls and processes SumSub webhooks for asynchronous interactions.
 * It provides functionality to create applicants, manage sessions, and handle webhook events related to identity verification.
 *
 * <p>Key responsibilities of this class include:
 * - Creating or retrieving applicants from SumSub using public identity IDs.
 * - Initiating identity verification sessions to be used by external clients.
 * - Processing incoming webhooks sent by SumSub to notify about events.
 */
@Slf4j
public class SumSubIdentityVerificationProvider implements IIdentityVerificationProvider {

    private final SumSubApiService apiService;
    private final SumSubWebhookProcessor sumSubWebhookProcessor;

    public SumSubIdentityVerificationProvider(SumSubApiService apiService, SumSubWebhookProcessor sumSubWebhookProcessor) {
        this.apiService = apiService;
        this.sumSubWebhookProcessor = sumSubWebhookProcessor;
    }

    @Override
    public CreateApplicantResponse createApplicant(String customerLanguage, String identityPublicId) {
        try {
            String applicantId = getOrCreateApplicantByPublicIdentityId(identityPublicId);
            if (applicantId != null) {
                log.info("Create SumSub session for identity {}, applicant: {}, language: {}", identityPublicId, applicantId, customerLanguage);
                CreateIdentityVerificationSessionResponse createSessionResponse = apiService.createSession(identityPublicId, customerLanguage);
                log.info("Received {} for {}", createSessionResponse, identityPublicId);
                String verificationWebUrl = createSessionResponse.getUrl();

                return new CreateApplicantResponse(applicantId, verificationWebUrl);
            }
        } catch (HttpStatusIOException e) {
            log.error("Error creating SumSub session, HTTP response code: {}, body: {}, error message: {}", e.getHttpStatusCode(), e.getHttpBody(), e.getMessage());
        } catch (Exception e) {
            log.error("Error creating SumSub session", e);
        }
        return null;
    }

    private String getOrCreateApplicantByPublicIdentityId(String identityPublicId) {
        boolean applicantAlreadyExistsError = false;
        try {
            log.info("Create SumSub applicant for {}", identityPublicId);
            ApplicantIdResponse createApplicantResponse = apiService.createApplicant(identityPublicId);
            return createApplicantResponse.getId();
        } catch (HttpStatusIOException e) {
            // if error is applicant already exists, continue to get applicant by identity public id, else return null
            if (e.getHttpStatusCode() == 409) {
                log.info("Got 409 from create applicant, likely applicant already exists");
                applicantAlreadyExistsError = true;
            } else {
                log.error("Error creating SumSub applicant, HTTP response code: {}, body: {}, error message: {}",
                        e.getHttpStatusCode(), e.getHttpBody(), e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error creating SumSub applicant", e);
        }

        try {
            if (applicantAlreadyExistsError) {
                // 409 error, from creating applicant, find applicant by externalId
                log.info("Find SumSub applicant for {}", identityPublicId);
                ApplicantInfoResponse applicantInfoResponse = apiService.getApplicantByExternalId(identityPublicId);
                return applicantInfoResponse.getId();
            }
        } catch (HttpStatusIOException e) {
            log.error("Error finding SumSub applicant, HTTP response code: {}, body: {}, error message: {}",
                    e.getHttpStatusCode(), e.getHttpBody(), e.getMessage());
        } catch (Exception e) {
            log.error("Error finding SumSub applicant", e);
        }

        return null;
    }

    public void processWebhook(String rawPayload,
                               BaseWebhookBody baseWebhookBody,
                               String payloadDigest,
                               String payloadDigestAlg) throws IdentityCheckWebhookException {
        sumSubWebhookProcessor.process(rawPayload, baseWebhookBody, payloadDigest, payloadDigestAlg);
    }
}
