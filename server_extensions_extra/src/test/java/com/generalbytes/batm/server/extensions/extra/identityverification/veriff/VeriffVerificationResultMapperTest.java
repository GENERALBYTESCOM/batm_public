package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.CheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.DocumentType;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.webhook.VerificationDecisionWebhookRequest.Verification.Person;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class VeriffVerificationResultMapperTest {
    private static final VeriffVerificationResultMapper mapper = new VeriffVerificationResultMapper();

    @Test
    public void testMapping() {
        ApplicantCheckResult result = mapper.mapResult(createRequest());

        assertNotNull(result);
        assertEquals("applicantId", result.getCheckId());
        assertEquals(CheckResult.CLEAR, result.getResult());
        assertEquals("test-firstName", result.getFirstName());
        assertEquals("test-lastName", result.getLastName());
        assertEquals(Date.from(LocalDate.of(2020, 2, 29).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), result.getBirthDate());

        assertEquals(DocumentType.national_identity_card, result.getDocumentType());
        assertEquals(Date.from(LocalDate.of(2027, 1, 23).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), result.getExpirationDate());
        assertEquals("1234ABC", result.getDocumentNumber());

        assertEquals("4360 Lone Wolf Ranch Road, Navasota TX US", result.getRawAddress());

        assertEquals("4360 Lone Wolf Ranch Road", result.getStreetAddress());
        assertEquals("Navasota", result.getCity());
        assertEquals("TX", result.getState());
        assertEquals("123 45", result.getZip());
        assertEquals("USA", result.getCountry());
    }

    @Test
    public void testStreetAddress() {
        assertNull(mapStreetAddress(null, null, null));
        assertNull(mapStreetAddress("us", null, null));

        assertEquals("Sněmovní 176/4", mapStreetAddress("cz", "Sněmovní", "176/4"));
        assertEquals("10 Downing Street", mapStreetAddress("gb", "Downing Street", "10"));
        assertEquals("4360 Lone Wolf Ranch Road", mapStreetAddress("us", "Lone Wolf Ranch Road", "4360"));
        assertEquals("streetname housenumber", mapStreetAddress(null, "streetname", "housenumber"));
    }

    private String mapStreetAddress(String country, String street, String houseNumber) {
        VerificationDecisionWebhookRequest request = createRequest();
        request.verification.person.addresses.get(0).parsedAddress.country = country;
        request.verification.person.addresses.get(0).parsedAddress.street = street;
        request.verification.person.addresses.get(0).parsedAddress.houseNumber = houseNumber;
        return mapper.mapResult(request).getStreetAddress();
    }

    private VerificationDecisionWebhookRequest createRequest() {
        VerificationDecisionWebhookRequest request = new VerificationDecisionWebhookRequest();
        request.verification = createVerification();

        return request;
    }

    private VerificationDecisionWebhookRequest.Verification createVerification() {
        VerificationDecisionWebhookRequest.Verification verification = new VerificationDecisionWebhookRequest.Verification();
        verification.id = "applicantId";
        verification.status = VerificationDecisionWebhookRequest.Verification.Status.approved;

        verification.person = new Person();
        verification.person.firstName = "test-firstName";
        verification.person.lastName = "test-lastName";
        verification.person.dateOfBirth = "2020-02-29";

        verification.person.addresses = Collections.singletonList(getAddress());

        verification.document = new VerificationDecisionWebhookRequest.Verification.Document();
        verification.document.type = VerificationDecisionWebhookRequest.Verification.Document.Type.ID_CARD;
        verification.document.validUntil = "2027-01-23";
        verification.document.country = "CA";
        verification.document.number = "1234ABC";

        return verification;
    }

    private Person.Address getAddress() {
        Person.Address address = new Person.Address();
        address.fullAddress = "4360 Lone Wolf Ranch Road, Navasota TX US";
        address.parsedAddress = new Person.Address.ParsedAddress();
        address.parsedAddress.city = "Navasota";
        address.parsedAddress.houseNumber = "4360";
        address.parsedAddress.street = "Lone Wolf Ranch Road";
        address.parsedAddress.postcode = "123 45";
        address.parsedAddress.country = "US";
        // according to CF-470 parsedAddress.state comes from veriff as lowercase,
        // but we need to save it as uppercase. State in fullAddress comes as uppercase.
        address.parsedAddress.state = "tx";
        return address;
    }
}