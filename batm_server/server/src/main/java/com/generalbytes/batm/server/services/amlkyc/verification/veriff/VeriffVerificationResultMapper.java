package com.generalbytes.batm.server.services.amlkyc.verification.veriff;

import com.generalbytes.batm.server.common.data.amlkyc.ApplicantCheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.ApplicantCheckResult.DocumentType;
import com.generalbytes.batm.server.common.data.amlkyc.CheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.IdentityApplicant;
import com.generalbytes.batm.server.extensions.Country;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationDecisionWebhookRequest.Verification;
import com.generalbytes.batm.server.util.StringUtils2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class VeriffVerificationResultMapper {
    private static final Logger log = LoggerFactory.getLogger(VeriffVerificationResultMapper.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT).withZone(ZoneId.systemDefault());

    public ApplicantCheckResult mapResult(VerificationDecisionWebhookRequest decisionRequest, IdentityApplicant identityApplicant) {
        Objects.requireNonNull(decisionRequest, "decisionRequest cannot be null");
        Verification verification = decisionRequest.verification;
        Objects.requireNonNull(verification, "verification cannot be null");

        ApplicantCheckResult result = new ApplicantCheckResult();
        result.setCheckId(decisionRequest.getApplicantId());
        result.setIdentityApplicant(identityApplicant);

        result.setResult(mapCheckResult(verification.status));

        result.setFisrtName(verification.person.firstName);
        result.setLastName(verification.person.lastName);
        result.setBirthDate(parseDate(verification.person.dateOfBirth));

        result.setDocumentType(mapDocumentType(verification.document.type));
        result.setExpirationDate(parseDate(verification.document.validUntil));

        Verification.Person.Address address = getAddress(verification);
        if (address != null) {
            result.setRawAddress(address.fullAddress);

            if (address.parsedAddress != null) {
                result.setStreetAddress(StringUtils2.joinNonBlankOrNull(address.parsedAddress.street, address.parsedAddress.houseNumber, address.parsedAddress.unit));
                result.setCity(address.parsedAddress.city);
                result.setZip(address.parsedAddress.postcode);
                result.setState(address.parsedAddress.state);
                result.setCountry(mapCountry(address.parsedAddress.country));
            }
        }

        if (result.getCountry() == null) {
            result.setCountry(mapCountry(verification.document.country));
        }

        result.setDocumentNumber(verification.document.number);
        return result;
    }

    private Verification.Person.Address getAddress(Verification verification) {
        if (verification.person.addresses == null || verification.person.addresses.isEmpty()) {
            return null;
        }
        return verification.person.addresses.get(0);
    }

    private CheckResult mapCheckResult(Verification.Status status) {
        Objects.requireNonNull(status, "verification status cannot be null");
        switch (status) {
            case approved:
                return CheckResult.CLEAR;
            case resubmission_requested:
                return CheckResult.RESUBMISSION_REQUESTED;
            case declined:
                return CheckResult.REJECTED;
            case abandoned:
            case expired:
                return CheckResult.EXPIRED;
            default:
                throw new RuntimeException("Unexpected verification status: " + status);
        }
    }

    /**
     * Converts iso2 to iso3 country code
     */
    private String mapCountry(String iso2) {
        if (iso2 == null) {
            return null;
        }
        return Country.valueOf(iso2.toUpperCase(Locale.ROOT)).getIso3();
    }

    private DocumentType mapDocumentType(Verification.Document.Type type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case DRIVERS_LICENSE:
                return DocumentType.driving_licence;
            case ID_CARD:
                return DocumentType.national_identity_card;
            case PASSPORT:
                return DocumentType.passport;
            case RESIDENCE_PERMIT:
                return DocumentType.residence_permit;
            case OTHER:
                return DocumentType.other;
            default:
                throw new RuntimeException("Unexpected verification document type: " + type);
        }
    }

    private static Date parseDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            LocalDate parsedDate = DATE_FORMAT.parse(date, LocalDate::from);
            return Date.from(parsedDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            log.error("Error parsing date: " + date, e);
            return null;
        }
    }
}

