package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.CheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.DocumentType;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.CountryRegionNameParser;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantAddress;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantDocument;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfo;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantReviewResult;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantReviewedWebhook;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionImage;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.ReviewAnswer;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.ReviewRejectType;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.SumSubDocumentType;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * The SumSubApplicantReviewedResultMapper is responsible for mapping data from SumSub ApplicantReviewWebhook,
 * ApplicantInfoResponse, and InspectionInfoResponse into an ApplicantCheckResult object.
 *
 * <p>This class handles the transformation of complex data structures provided by SumSub APIs into domain-specific
 * representations, ensuring that all relevant applicant, document, and address information is accurately mapped.
 */
@Slf4j
public class SumSubApplicantReviewedResultMapper {

    /**
     * Maps the result from the SumSub API to an {@link ApplicantCheckResult}.
     *
     * @param applicantReviewed the webhook data containing the review result and inspection ID of the applicant
     * @param applicantInfoResponse the detailed information about the applicant including personal data and documents
     * @param inspectionInfoResponse the inspection data including metadata and associated images
     * @return an {@link ApplicantCheckResult} containing the mapped details from the provided SumSub data
     */
    public ApplicantCheckResult mapResult(ApplicantReviewedWebhook applicantReviewed,
                                          ApplicantInfoResponse applicantInfoResponse,
                                          InspectionInfoResponse inspectionInfoResponse) {
        log.info("Setting ApplicantCheckResult from SumSub info");
        ApplicantCheckResult checkResult = new ApplicantCheckResult();
        checkResult.setCheckId(applicantReviewed.getInspectionId());
        checkResult.setIdentityApplicantId(applicantReviewed.getApplicantId());
        checkResult.setResult(mapCheckResult(applicantReviewed.getReviewResult()));

        if (applicantInfoResponse.getInfo() != null) {
            ApplicantInfo info = applicantInfoResponse.getInfo();
            // set personal information
            checkResult.setFirstName(info.getFirstName());
            checkResult.setLastName(info.getLastName());
            checkResult.setBirthDate(fromLocalDate(info.getDob()));

            // Get the IDENTITY document type from the inspection and convert to a GB document type
            ApplicantDocument ssDocument = extractIdentityDocument(info.getIdDocs(), inspectionInfoResponse.getImages());
            if (ssDocument != null) {
                // set info from a document
                checkResult.setDocumentType(translateSSDocumentType(ssDocument.getIdDocType()));
                checkResult.setDocumentNumber(ssDocument.getNumber());
                checkResult.setExpirationDate(fromLocalDate(ssDocument.getValidUntil()));
                // ALPHA-3 code
                checkResult.setCountry(ssDocument.getCountry());
            }

            // set address information
            ApplicantAddress firstAddress = getFirstAddress(info.getAddresses());
            if (firstAddress != null) {
                checkResult.setRawAddress(firstAddress.getFormattedAddress());
                checkResult.setStreetAddress(firstAddress.getStreet());
                checkResult.setCity(firstAddress.getTown());
                checkResult.setZip(firstAddress.getPostCode());
                checkResult.setState(CountryRegionNameParser
                        .getRegionCodeFromCountry(firstAddress.getState(), firstAddress.getCountry()));
                checkResult.setCountry(firstAddress.getCountry());
            }
        }

        return checkResult;
    }

    /**
     * Maps the provided {@link ApplicantReviewResult} to a corresponding {@link CheckResult}.
     * The mapping is based on the review answer and review reject type provided within the review result.
     *
     * @param reviewResult the {@link ApplicantReviewResult} containing the review answer and reject type.
     *                     The review answer (GREEN or RED) determines the resulting check status.
     * @return the mapped {@link CheckResult}. Returns {@code CheckResult.CLEAR} for GREEN,
     *         {@code CheckResult.REJECTED} for RED with FINAL reject type, and
     *         {@code CheckResult.RESUBMISSION_REQUESTED} for RED with RETRY reject type.
     */
    public CheckResult mapCheckResult(ApplicantReviewResult reviewResult) {
        ReviewAnswer answerType = reviewResult.getReviewAnswer();
        ReviewRejectType rejectType = reviewResult.getReviewRejectType();

        Objects.requireNonNull(answerType, "Review result answer (GREEN, RED) cannot be null");
        return switch (answerType) {
            case GREEN -> CheckResult.CLEAR;
            case RED -> switch (rejectType) {
                case FINAL -> CheckResult.REJECTED;
                case RETRY -> CheckResult.RESUBMISSION_REQUESTED;
            };
        };

    }

    /**
     * Translates a given SumSubDocumentType to a corresponding DocumentType used for internal representation.
     *
     * @param sumSubDocumentType the document type from SumSub to be translated
     * @return the corresponding DocumentType mapping or 'DocumentType.other' if no match is found
     */
    public DocumentType translateSSDocumentType(SumSubDocumentType sumSubDocumentType) {
        return switch (sumSubDocumentType) {
            case DRIVERS -> DocumentType.driving_licence;
            case PASSPORT -> DocumentType.passport;
            case RESIDENCE_PERMIT -> DocumentType.residence_permit;
            case ID_CARD -> DocumentType.national_identity_card;
            default -> DocumentType.other;
        };
    }

    private ApplicantDocument extractIdentityDocument(List<ApplicantDocument> documentList, List<InspectionImage> inspectionImageList) {
        // get the first GREEN review result image that is an identity document to find the type
        SumSubDocumentType identityDocumentType = inspectionImageList.stream()
                .filter(SumSubApplicantReviewedResultMapper::isReviewAnswerGreen)
                .filter(SumSubApplicantReviewedResultMapper::isInspectionImageForIdentityDocument)
                .map(inspectionImage -> inspectionImage.getIdDocDef().getIdDocType())
                .findFirst().orElse(null);

        if (identityDocumentType != null) {
            return documentList.stream().filter(docs -> docs.getIdDocType() == identityDocumentType).findFirst().orElse(null);
        }
        return null;
    }

    private static boolean isReviewAnswerGreen(InspectionImage inspectionImage) {
        return inspectionImage.getReviewResult() != null && inspectionImage.getReviewResult().getReviewAnswer() == ReviewAnswer.GREEN;
    }

    private static boolean isInspectionImageForIdentityDocument(InspectionImage inspectionImage) {
        return inspectionImage.getIdDocDef() != null && SumSubDocumentType.identityDocuments().contains(inspectionImage.getIdDocDef().getIdDocType());
    }

    private ApplicantAddress getFirstAddress(List<ApplicantAddress> ssAddresses) {
        if (ssAddresses != null && !ssAddresses.isEmpty()) {
            // use first address
            return ssAddresses.get(0);
        }
        return null;
    }

    private Date fromLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
