package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.IIdentityBase;
import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.digest.SumSubWebhookSecretDigest;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.exception.SumSubException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantReviewedWebhook;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.BaseWebhookBody;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityVerificationSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionInfoResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import si.mazi.rescu.HttpStatusIOException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * The SumSubWebhookProcessor class is responsible for processing incoming webhooks from the SumSub
 * system. It verifies webhook payload signatures, parses the data, and performs different actions
 * based on the type of webhook event.
 *
 * <p>The supported webhook events include:
 * - "applicantReviewed": Handles events when an applicant's verification is reviewed.
 * - "applicantLevelChanged": Handles applicant level change notifications.
 */
@Slf4j
@AllArgsConstructor
public class SumSubWebhookProcessor {

    private final IExtensionContext ctx;
    private final SumSubApiService apiService;
    private final SumSubWebhookParser webhookParser;
    private final SumSubApplicantReviewedResultMapper checkResultMapper;
    private final String webhookSecretKey;

    /**
     * Processes incoming webhook payloads by verifying their signatures and handling the webhook
     * events based on the type of notification received. This method supports different webhook types,
     * such as "applicantReviewed" and "applicantLevelChanged", and invokes corresponding handling
     * logic for each type. If the signature verification fails, the process will stop with an exception.
     *
     * @param rawPayload the raw JSON payload received in the webhook
     * @param baseWebhookBody the deserialized base webhook body containing the common attributes of the webhook
     * @param payloadDigest the expected digest of the payload for signature verification
     * @param payloadDigestAlg the algorithm used to generate the payload digest
     * @throws IdentityCheckWebhookException if any error occurs during payload signature verification or processing
     */
    public void process(String rawPayload,
                        BaseWebhookBody baseWebhookBody,
                        String payloadDigest,
                        String payloadDigestAlg) throws IdentityCheckWebhookException {
        verifySignature(rawPayload, payloadDigest, payloadDigestAlg);
        switch (baseWebhookBody.getType()) {
            case "applicantReviewed":
                log.info("Applicant reviewed webhook received: applicant {}, inspection {}, correlation {}",
                        baseWebhookBody.getApplicantId(), baseWebhookBody.getInspectionId(), baseWebhookBody.getCorrelationId());
                ApplicantReviewedWebhook reviewedWebhook = webhookParser.parse(rawPayload, ApplicantReviewedWebhook.class);
                processApplicantReviewedWebhook(reviewedWebhook, rawPayload);
                break;
            case "applicantLevelChanged":
                log.info("Applicant level changed webhook received: applicant {}, inspection {}, correlation {}, new level {}",
                        baseWebhookBody.getApplicantId(), baseWebhookBody.getInspectionId(), baseWebhookBody.getCorrelationId(), baseWebhookBody.getLevelName());
                if (!Objects.equals(apiService.getStartingLevelName(), baseWebhookBody.getLevelName())) {
                    // send SMS to the user if their Sum&Sub applicant level changed to a new level (that is not the starting level)
                    sendLevelChangedSMSToIdentity(baseWebhookBody.getExternalUserId());
                } else {
                    log.info("Applicant level changed webhook received but level for applicant {} is still the same.", baseWebhookBody.getApplicantId());
                }
                break;
            default:
                log.info("Other webhook type received: {}, applicant: {}, further processing skipped",
                        baseWebhookBody.getType(), baseWebhookBody.getApplicantId());
        }
    }

    private void processApplicantReviewedWebhook(ApplicantReviewedWebhook applicantReviewedWebhook, String rawPayload)
            throws IdentityCheckWebhookException {
        try {
            ApplicantInfoResponse applicantInfoResponse = apiService.getApplicantByExternalId(applicantReviewedWebhook.getExternalUserId());
            InspectionInfoResponse inspectionInfoResponse = apiService.getInspectionInfo(applicantReviewedWebhook.getInspectionId());

            ApplicantCheckResult result = checkResultMapper.mapResult(applicantReviewedWebhook, applicantInfoResponse, inspectionInfoResponse);
            IIdentity identity = ctx.findIdentityByIdentityId(applicantReviewedWebhook.getExternalUserId());
            // if identity is not in STATE_TO_BE_VERIFIED, the processIdentityVerificationResult call will not update the state
            // after discussing with GB, this is intended due to some inconsistent webhook issues they have with Veriff
            // but with Sum&Substance, we need to update the state since they do ongoing monitoring,
            // and we should update the state with new webhook information
            if (identity.getState() != IIdentityBase.STATE_TO_BE_VERIFIED) {
                // only update the state to STATE_TO_BE_VERIFIED and add a new note
                ctx.updateIdentity(identity.getPublicId(), identity.getExternalId(), IIdentityBase.STATE_TO_BE_VERIFIED,
                        identity.getType(), identity.getCreated(), identity.getRegistered(), identity.getVipBuyDiscount(),
                        identity.getVipSellDiscount(), "SumSubWebhookProcessor: move identity state to STATE_TO_BE_VERIFIED for new applicantReviewed webhook.",
                        identity.getLimitCashPerTransaction(), identity.getLimitCashPerHour(), identity.getLimitCashPerDay(),
                        identity.getLimitCashPerWeek(), identity.getLimitCashPerMonth(), identity.getLimitCashPer3Months(),
                        identity.getLimitCashPer12Months(), identity.getLimitCashPerCalendarQuarter(), identity.getLimitCashPerCalendarYear(),
                        identity.getLimitCashTotalIdentity(), identity.getConfigurationCashCurrency());
            }
            ctx.processIdentityVerificationResult(rawPayload, result);
        } catch (HttpStatusIOException e) {
            log.error("Error getting info from SumSub: HTTP response code: {}, body: {}, error message: {}", e.getHttpStatusCode(), e.getHttpBody(), e.getMessage());
            throw new IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "",
                    "Error getting info from SumSub.");
        } catch (Exception e) {
            throw new SumSubException(e);
        }
    }

    private void sendLevelChangedSMSToIdentity(String identityId) throws IdentityCheckWebhookException {
        try {
            IIdentity identity = ctx.findIdentityByIdentityId(identityId);
            List<IIdentityPiece> cellphonePieces = identity.getIdentityPieces().stream()
                    .filter(identityPiece -> identityPiece.getPieceType() == IIdentityPiece.TYPE_CELLPHONE)
                    .toList();
            IIdentityPiece latestCellPhone = Collections.max(cellphonePieces, Comparator.comparing(IIdentityPiece::getCreated));
            CreateIdentityVerificationSessionResponse sessionResponse = apiService.createSession(identityId, null);
            log.info("Sending SMS to ask identity {} to continue their verification on the new level: {}", identityId, sessionResponse.getUrl());
            ctx.sendSMSAsync(identity.getCreatedByTerminalSerialNumber(), latestCellPhone.getPhoneNumber(),
                    String.format("Please use the following link to continue your verification process. %s", sessionResponse.getUrl()));
        } catch (HttpStatusIOException e) {
            log.error("Error creating SumSub session, HTTP response code: {}, body: {}, error message: {}", e.getHttpStatusCode(), e.getHttpBody(), e.getMessage());
            throw new IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "",
                    "Error creating SumSub session.");
        } catch (Exception e) {
            throw new SumSubException(e);
        }
    }

    private void verifySignature(String rawPayload, String payloadDigest, String payloadDigestAlg) throws IdentityCheckWebhookException {
        SumSubWebhookSecretDigest sumsubDigest = new SumSubWebhookSecretDigest(this.webhookSecretKey, payloadDigestAlg);
        String computedSignature = sumsubDigest.digest(rawPayload);
        if (!Objects.equals(payloadDigest, computedSignature)) {
            throw new IdentityCheckWebhookException(Status.UNAUTHORIZED.getStatusCode(), "signature verification failure",
                    "Wrong  secret used? computed: '" + computedSignature + "', received: '" + payloadDigest
                            + "', payload: '" + rawPayload + "'. " + "Was SumSub webhook secret configured properly?");
        }
    }
}
