package com.generalbytes.batm.server.services.amlkyc.verification.veriff;

import com.generalbytes.batm.server.common.data.amlkyc.ApplicantCheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.CheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.IdentityApplicant;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VeriffVerificationResultMapperTest {

    @Test
    public void testMapping() {
        VeriffVerificationResultMapper mapper = new VeriffVerificationResultMapper();
        ApplicantCheckResult result = mapper.mapResult(createRequest(), new IdentityApplicant());

        assertNotNull(result);
        assertEquals("applicantId", result.getCheckId());
        assertEquals(CheckResult.CLEAR, result.getResult());
        assertEquals("test-firstName", result.getFisrtName());
        assertEquals("test-lastName", result.getLastName());
        assertEquals(Date.from(LocalDate.of(2020, 2, 29).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), result.getBirthDate());

        assertEquals(ApplicantCheckResult.DocumentType.national_identity_card, result.getDocumentType());
        assertEquals(Date.from(LocalDate.of(2027, 1, 23).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()), result.getExpirationDate());
        assertEquals("USA", result.getCountry());
        assertEquals("1234ABC", result.getDocumentNumber());
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

        verification.person = new VerificationDecisionWebhookRequest.Verification.Person();
        verification.person.firstName = "test-firstName";
        verification.person.lastName = "test-lastName";
        verification.person.dateOfBirth = "2020-02-29";

        verification.document = new VerificationDecisionWebhookRequest.Verification.Document();
        verification.document.type = VerificationDecisionWebhookRequest.Verification.Document.Type.ID_CARD;
        verification.document.validUntil = "2027-01-23";
        verification.document.country = "US";
        verification.document.number = "1234ABC";

        return verification;
    }
}