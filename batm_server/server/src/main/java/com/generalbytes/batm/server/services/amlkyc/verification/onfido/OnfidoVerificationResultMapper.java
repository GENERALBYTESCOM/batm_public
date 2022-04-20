package com.generalbytes.batm.server.services.amlkyc.verification.onfido;

import com.generalbytes.batm.server.common.data.amlkyc.ApplicantCheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.CheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.IdentityApplicant;
import com.onfido.Onfido;
import com.onfido.models.Check;
import com.onfido.models.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.CAUTION;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.CAUTION_DATA_COMPARISON;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.CAUTION_DATA_CONSISTENCY;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.CAUTION_DATA_VALIDATION;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.CAUTION_FACIAL_COMPARISON;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.CAUTION_IMAGE_INTEGRITY;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.CAUTION_VISUAL_CONSISTENCY;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.CLEAR;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.REJECTED;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.REJECTED_AGE_VALIDATION;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.REJECTED_IMAGE_INTEGRITY;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.SUSPECTED;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.SUSPECTED_COMPROMISED_DOCUMENT;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.SUSPECTED_DATA_CONSISTENCY;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.SUSPECTED_DATA_VALIDATION;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.SUSPECTED_POLICE_RECORD;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.SUSPECTED_VISUAL_CONSISTENCY;
import static com.generalbytes.batm.server.common.data.amlkyc.CheckResult.UNKNOWN;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoIdentityVerificationProvider.callInTry;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.CAUTION_CHECK_SUBRESULT;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.CLEAR_CHECK_RESULT;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.CLEAR_CHECK_SUBRESULT;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_CHECK_AGE_VALID;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_CHECK_COMPROMISED;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_CHECK_DATA_COMPARISON;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_CHECK_DATA_CONSISTENCY;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_CHECK_DATA_VALIDATION;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_CHECK_IMAGE_INTEGRITY;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_CHECK_POLICE_RECORD;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_CHECK_VISUAL_CONSISTENCY;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.DOCUMENT_REPORT_TYPE;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.FACIAL_COMPARISON;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.FACIAL_IMAGE_INTEGRITY;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.FACIAL_SIMILARITY_REPORT_TYPE;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.FACIAL_VISUAL_AUTHENTICITY;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.REJECTED_CHECK_SUBRESULT;
import static com.generalbytes.batm.server.services.amlkyc.verification.onfido.OnfidoReportConstants.SUSPECTED_CHECK_SUBRESULT;
import static java.util.Collections.emptyList;

public class OnfidoVerificationResultMapper {

    private Onfido onfido;

    private static final Logger log = LoggerFactory.getLogger(OnfidoVerificationResultMapper.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    public OnfidoVerificationResultMapper(Onfido onfido) {
        this.onfido = onfido;
    }

    public ApplicantCheckResult mapResult(Check check, IdentityApplicant identityApplicant) {
        List<Report> reports = emptyList();
        if (check.getReportIds() != null) {
            reports = check.getReportIds().stream()
                .map(id ->  callInTry(() -> onfido.report.find(id)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }

        ApplicantCheckResult result = new ApplicantCheckResult();
        result.setCheckId(check.getId());
        result.setIdentityApplicant(identityApplicant);
        result.setResult(mapCheckResult(check, reports));
        fillPersonalInformations(result, reports);
        return result;
    }

    private CheckResult mapCheckResult(Check check, List<Report> reports) {
        if (CLEAR_CHECK_RESULT.equals(check.getResult())) {
            return CLEAR;
        }
        Optional<Report> documentReport = reports.stream().filter(r -> DOCUMENT_REPORT_TYPE.equals(r.getName())).findFirst();
        Optional<Report> facialSimilarityReport = reports.stream().filter(r -> FACIAL_SIMILARITY_REPORT_TYPE.equals(r.getName())).findFirst();

        CheckResult checkResult = null;
        if (documentReport.isPresent()) {
            checkResult = mapDocumentResult(documentReport.get());
        }
        if ((checkResult == null || CLEAR == checkResult || UNKNOWN == checkResult) && facialSimilarityReport.isPresent()) {
            checkResult = mapFacialSimilarityResult(facialSimilarityReport.get());
        }
        return checkResult == null ? UNKNOWN : checkResult;
    }

    private CheckResult mapDocumentResult(Report r) {
        String reportResult = r.getResult();
        CheckResult subResult = mapSubResult(r.getSubResult());
        if (CLEAR_CHECK_RESULT.equals(reportResult)) {
            return CLEAR;
        }
        if (r.getBreakdown() == null || r.getBreakdown().isEmpty()) {
            return subResult;
        }

        CheckResult breakdownResult = null;
        switch (subResult) {
            case CAUTION:
                breakdownResult = mapDocumentCaution(r);
                break;
            case SUSPECTED:
                breakdownResult = mapDocumentSuspected(r);
                break;
            case REJECTED:
                breakdownResult = mapDocumentRejected(r);
                break;
            default:
                break;
        }
        return breakdownResult == null ? subResult : breakdownResult;
    }

    private CheckResult mapDocumentRejected(Report report) {
        for (String key : report.getBreakdown().keySet()) {
            Map<String, Object> resultMap = (Map<String, Object>) report.getBreakdown().get(key);
            String partialResult = (String) resultMap.get("result");

            if (partialResult != null && !partialResult.equals(CLEAR_CHECK_RESULT)) {
                switch (key) {
                    case DOCUMENT_CHECK_IMAGE_INTEGRITY:
                        return REJECTED_IMAGE_INTEGRITY;
                    case DOCUMENT_CHECK_AGE_VALID:
                        return REJECTED_AGE_VALIDATION;
                    default:
                        log.info("Unmapped document rejected result: {}", key);
                }
            }
        }
        return null;
    }


    private CheckResult mapDocumentSuspected(Report report) {
        for (String key : report.getBreakdown().keySet()) {
            Map<String, Object> resultMap = (Map<String, Object> ) report.getBreakdown().get(key);
            String partialResult = (String) resultMap.get("result");

            if (partialResult != null && !partialResult.equals(CLEAR_CHECK_RESULT)) {
                switch (key) {
                    case DOCUMENT_CHECK_COMPROMISED:
                        return SUSPECTED_COMPROMISED_DOCUMENT;
                    case DOCUMENT_CHECK_POLICE_RECORD:
                        return SUSPECTED_POLICE_RECORD;
                    case DOCUMENT_CHECK_DATA_CONSISTENCY:
                        return SUSPECTED_DATA_CONSISTENCY;
                    case DOCUMENT_CHECK_VISUAL_CONSISTENCY:
                        return SUSPECTED_VISUAL_CONSISTENCY;
                    case DOCUMENT_CHECK_DATA_VALIDATION:
                        return SUSPECTED_DATA_VALIDATION;
                    default:
                        log.info("Unmapped document suspected result: {}", key);
                }
            }
        }
        return null;
    }

    private CheckResult mapDocumentCaution(Report report) {
        for (String key : report.getBreakdown().keySet()) {
            Map<String, Object> resultMap = (Map<String, Object> ) report.getBreakdown().get(key);
            String partialResult = (String) resultMap.get("result");

            if (partialResult != null && !partialResult.equals(CLEAR_CHECK_RESULT)) {
                switch (key) {
                    case DOCUMENT_CHECK_VISUAL_CONSISTENCY:
                        return CAUTION_VISUAL_CONSISTENCY;
                    case DOCUMENT_CHECK_IMAGE_INTEGRITY:
                        return CAUTION_IMAGE_INTEGRITY;
                    case DOCUMENT_CHECK_DATA_VALIDATION:
                        return CAUTION_DATA_VALIDATION;
                    case DOCUMENT_CHECK_DATA_COMPARISON:
                        return CAUTION_DATA_COMPARISON;
                    case DOCUMENT_CHECK_DATA_CONSISTENCY:
                        return CAUTION_DATA_CONSISTENCY;
                    default:
                        log.info("Unmapped document caution result: {}", key);
                }
            }
        }
        return null;
    }

    private CheckResult mapFacialSimilarityResult(Report r) {
        String reportResult = r.getResult();
        if (CLEAR_CHECK_RESULT.equals(reportResult)) {
            return CLEAR;
        }
        if (r.getBreakdown() == null || r.getBreakdown().isEmpty()) {
            return CAUTION;
        }

        for (String key : r.getBreakdown().keySet()) {
            Map<String, Object> resultMap = (Map<String, Object> ) r.getBreakdown().get(key);
            String partialResult = (String) resultMap.get("result");
            if (partialResult != null && !partialResult.equals(CLEAR_CHECK_RESULT)) {
                switch (key) {
                    case FACIAL_COMPARISON:
                        return CAUTION_FACIAL_COMPARISON;
                    case FACIAL_IMAGE_INTEGRITY:
                        return CAUTION_IMAGE_INTEGRITY;
                    case FACIAL_VISUAL_AUTHENTICITY:
                        return CAUTION_VISUAL_CONSISTENCY;
                    default:
                        log.info("Unmapped facial similarity result: {}", key);
                }
            }
        }
        return UNKNOWN;
    }

    private void fillPersonalInformations(ApplicantCheckResult result, List<Report> reports) {
        reports.forEach(r -> {
            if (DOCUMENT_REPORT_TYPE.equals(r.getName())) {
                Map<String, Object> docProperties = r.getProperties();
                result.setFisrtName((String) docProperties.get("first_name"));
                result.setLastName((String) docProperties.get("last_name"));
                result.setDocumentType(mapDocumentType((String) docProperties.get("document_type")));
                result.setBirthDate(parseDate(docProperties, "date_of_birth"));
                result.setExpirationDate(parseDate(docProperties, "date_of_expiry"));

                String country = (String) docProperties.get("nationality");
                if (country == null) {
                    country = (String) docProperties.get("issuing_country");
                }
                result.setCountry(country);

                if (docProperties.get("document_numbers") != null) {
                    List<Map<String, Object>> documentNumbers = (List<Map<String, Object>>) docProperties.get("document_numbers");
                    if (documentNumbers.size() > 0) {
                        result.setDocumentNumber((String) documentNumbers.get(0).get("value"));
                    }
                }
            }
        });
    }

    private ApplicantCheckResult.DocumentType mapDocumentType(String type) {
        if (type == null) {
            return null;
        }
        try {
            return ApplicantCheckResult.DocumentType.valueOf(type);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown document type: " + type);
            return ApplicantCheckResult.DocumentType.other;
        }
    }

    private static Date parseDate(Map<String, Object> props, String fieldName) {
        String s = (String) props.get(fieldName);
        if (s != null) {
            try {
                return Date.from(DATE_FORMAT.parse(s, Instant::from));
            } catch (DateTimeParseException e) {
                log.error("Error parsing date.", e);
                return null;
            }
        }
        return null;
    }

    private CheckResult mapSubResult(String subResult) {
        if (subResult == null) {
            return UNKNOWN;
        }
        switch (subResult) {
            case CLEAR_CHECK_SUBRESULT:
                return CLEAR;
            case CAUTION_CHECK_SUBRESULT:
                return CAUTION;
            case REJECTED_CHECK_SUBRESULT:
                return REJECTED;
            case SUSPECTED_CHECK_SUBRESULT:
                return SUSPECTED;
            default:
                return UNKNOWN;
        }
    }
}
