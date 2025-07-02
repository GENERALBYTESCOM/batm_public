package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.Country;
import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.CheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.DocumentType;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.webhook.VerificationDecisionWebhookRequest.Verification;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VeriffVerificationResultMapper {
    private static final Logger log = LoggerFactory.getLogger(VeriffVerificationResultMapper.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT).withZone(ZoneId.systemDefault());

    // Countries that have house number before street name, list not complete, source: https://en.wikipedia.org/wiki/Address#Address_format
    private static final Set<Country> houseNumberFirstCountries = Collections.unmodifiableSet(EnumSet.of(
        Country.US,
        Country.GB,
        Country.AU,
        Country.CA,
        Country.FR));

    public ApplicantCheckResult mapResult(VerificationDecisionWebhookRequest decisionRequest) {
        Objects.requireNonNull(decisionRequest, "decisionRequest cannot be null");
        Verification verification = decisionRequest.verification;
        Objects.requireNonNull(verification, "verification cannot be null");

        ApplicantCheckResult result = new ApplicantCheckResult();
        result.setCheckId(decisionRequest.getApplicantId());
        result.setIdentityApplicantId(decisionRequest.getApplicantId());
        result.setResult(mapCheckResult(verification.status));
        if (verification.status == Verification.Status.declined ||
            verification.status == Verification.Status.resubmission_requested) {
            result.setResultReason(mapVerificationFailureReasonCode(verification.reasonCode));
        }

        result.setFirstName(verification.person.firstName);
        result.setLastName(verification.person.lastName);
        result.setBirthDate(parseDate(verification.person.dateOfBirth));

        result.setDocumentType(mapDocumentType(verification.document.type));
        result.setExpirationDate(parseDate(verification.document.validUntil));

        Verification.Person.Address address = getAddress(verification);
        if (address != null) {
            result.setRawAddress(address.fullAddress);

            if (address.parsedAddress != null) {
                result.setStreetAddress(mapStreetAddress(address.parsedAddress));
                result.setCity(address.parsedAddress.city);
                result.setZip(address.parsedAddress.postcode);
                result.setState(mapState(address.parsedAddress.state));
                result.setCountry(mapCountry(address.parsedAddress.country));
            }
        }

        if (result.getCountry() == null) {
            result.setCountry(mapCountry(verification.document.country));
        }

        result.setDocumentNumber(verification.document.number);
        return result;
    }

    /**
     * @return House number, street name and unit number (if present) formatted as one string.
     * Depending on the address country the formatted line starts with the house number (e.g. "10 Downing Street")
     * or with the street name (e.g. "Dlouhá 33").
     */
    private String mapStreetAddress(Verification.Person.Address.ParsedAddress parsedAddress) {
        if (houseNumberFirstCountries.contains(getCountry(parsedAddress.country))) {
            return join(parsedAddress.houseNumber, parsedAddress.street, parsedAddress.unit);
        }
        return join(parsedAddress.street, parsedAddress.houseNumber, parsedAddress.unit);
    }

    private String join(String... values) {
        return Strings.emptyToNull(
            Stream.of(values)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" ")));
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

    private String mapState(String state) {
        return state == null ? null : state.toUpperCase();
    }

    /**
     * Converts iso2 to iso3 country code
     */
    private String mapCountry(String iso2) {
        Country country = getCountry(iso2);
        return country == null ? null : country.getIso3();
    }

    private Country getCountry(String iso2) {
        if (iso2 == null) {
            return null;
        }
        return Country.valueOf(iso2.toUpperCase(Locale.ROOT));
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

    /**
     * Maps Veriff reason codes to human-readable descriptions.
     * Based on the <a href="https://devdocs.veriff.com/v1/docs/verification-session-decision-codes-table">Veriff documentation</a>
     */
    private static String mapVerificationFailureReasonCode(Integer reasonCode) {
        if (reasonCode == null) {
            return null;
        }

        String reasonDescription = switch (reasonCode) {
            // Declined codes
            case 102 -> "Suspected document tampering";
            case 103 -> "Person showing the document does not appear to match document photo";
            case 105 -> "Suspicious behaviour";
            case 106 -> "Known fraud";
            case 108 -> "Velocity/abuse duplicated end-user";
            case 109 -> "Velocity/abuse duplicated device";
            case 110 -> "Velocity/abuse duplicated ID";
            case 112 -> "Restricted IP location";
            case 113 -> "Suspicious behaviour - Identity Farming";

            // Resubmission requested codes
            case 201 -> "Video and/or photos missing";
            case 204 -> "Poor image quality";
            case 205 -> "Document damaged";
            case 206 -> "Document type not supported";
            case 207 -> "Document expired";

            default -> "Unknown reason";
        };
        return "Veriff - " + reasonDescription;
    }
}
