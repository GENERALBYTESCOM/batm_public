package com.generalbytes.batm.server.extensions.travelrule.gtr.mapper;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPersonName;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyField;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookVerifyPiiResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrBeneficiary;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101Payload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrOriginator;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrPerson;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrPersonName;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrPersonNameIdentifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GtrVerifyPiiMapperTest {

    @Test
    void testToGtrIvms101Payload() {
        ITravelRuleNaturalPerson originator = createITravelRuleNaturalPerson("Nakamoto", "Satoshi");
        ITravelRuleNaturalPerson beneficiary = createITravelRuleNaturalPerson("Finney", "Hal");

        ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
        when(transferData.getOriginator()).thenReturn(originator);
        when(transferData.getBeneficiary()).thenReturn(beneficiary);

        GtrIvms101Payload payload = GtrVerifyPiiMapper.toGtrIvms101Payload(transferData);

        assertGtrPersons(payload.getIvms101().getOriginator().getOriginatorPersons(), "Nakamoto", "Satoshi");
        assertGtrPersons(payload.getIvms101().getBeneficiary().getBeneficiaryPersons(), "Finney", "Hal");
    }

    private void assertGtrPersons(List<GtrPerson> persons, String expectedPrimaryIdentifier, String expectedSecondaryIdentifier) {
        assertEquals(1, persons.size());

        List<GtrPersonNameIdentifier> nameIdentifiers = persons.get(0).getNaturalPerson().getName().getNameIdentifiers();
        assertEquals(1, nameIdentifiers.size());

        GtrPersonNameIdentifier nameIdentifier = nameIdentifiers.get(0);
        assertEquals("LEGL", nameIdentifier.getNameIdentifierType());
        assertEquals(expectedPrimaryIdentifier, nameIdentifier.getPrimaryIdentifier());
        assertEquals(expectedSecondaryIdentifier, nameIdentifier.getSecondaryIdentifier());
    }

    @Test
    void testToGtrVerifyPiiRequest() {
        ITravelRuleVasp beneficiaryVasp = mock(ITravelRuleVasp.class);
        when(beneficiaryVasp.getDid()).thenReturn("beneficiary_vasp_did");

        ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
        when(transferData.getBeneficiaryVasp()).thenReturn(beneficiaryVasp);
        when(transferData.getFiatAmount()).thenReturn(BigDecimal.valueOf(5000));
        when(transferData.getFiatCurrency()).thenReturn("CZK");

        GtrVerifyPiiRequest request = GtrVerifyPiiMapper.toGtrVerifyPiiRequest(
                transferData,
                "request_id",
                "initiator_vasp_public_key",
                "target_vasp_public_key",
                "encrypted_payload",
                BigDecimal.valueOf(21L)
        );

        assertEquals("request_id", request.getRequestId());
        assertNull(request.getTxId());
        assertEquals("initiator_vasp_public_key", request.getInitiatorPublicKey());
        assertEquals("beneficiary_vasp_did", request.getTargetVaspCode());
        assertEquals("target_vasp_public_key", request.getTargetVaspPublicKey());
        assertEquals("encrypted_payload", request.getEncryptedPayload());
        assertEquals("ivms101-2020", request.getPiiSpecVersion());
        assertEquals(GtrApiConstants.SecretType.CURVE_25519, request.getSecretType());
        assertEquals("21", request.getAmount());
        assertEquals("5000", request.getFiatPrice());
        assertEquals("CZK", request.getFiatName());
        assertFalse(request.isLawThresholdEnabled());
        assertEquals(2, request.getExpectVerifyFields().size());
        assertTrue(request.getExpectVerifyFields().contains("100026"));
        assertTrue(request.getExpectVerifyFields().contains("110026"));
    }

    @Test
    void testToIncomingTransferEvent() {
        GtrPiiVerifyWebhookPayload verifyWebhookPayload = createGtrPiiVerifyWebhookPayload();
        GtrIvms101Payload ivms101Payload = createGtrIvms101Payload();
        String rawData = "raw_data";

        ITravelRuleIncomingTransferEvent event = GtrVerifyPiiMapper.toIncomingTransferEvent(verifyWebhookPayload, ivms101Payload, rawData);

        assertEquals("request_id", event.getId());
        assertEquals("beneficiary_vasp", event.getBeneficiaryVaspDid());
        assertEquals("originator_vasp", event.getOriginatorVasp().getDid());
        assertNull(event.getOriginatorVasp().getName());
        assertEquals("type_1", event.getOriginatorName().getNameType());
        assertEquals("Nakamoto", event.getOriginatorName().getPrimaryName());
        assertEquals("Satoshi", event.getOriginatorName().getSecondaryName());
        assertEquals("type_2", event.getBeneficiaryName().getNameType());
        assertEquals("Finney", event.getBeneficiaryName().getPrimaryName());
        assertEquals("Hal", event.getBeneficiaryName().getSecondaryName());
        assertEquals("address", event.getDestinationAddress());
        assertEquals("raw_data", event.getRawData());
    }

    @Test
    void testToSuccessVerifyPiiWebhookResponse() {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();

        GtrWebhookVerifyPiiResponse response = GtrVerifyPiiMapper.toSuccessVerifyPiiWebhookResponse(payload);
        GtrWebhookVerifyPiiResponse.VerifyPiiData data = response.getData();

        assertEquals(GtrApiConstants.VerifyStatus.SUCCESS, response.getVerifyStatus());
        assertEquals("Verification Success", response.getVerifyMessage());
        assertEquals("encrypted_payload", data.getEncryptedPayload());
        assertEquals("initiator_public_key", data.getInitiatorPublicKey());
        assertEquals("receiver_public_key", data.getReceiverPublicKey());
        assertEquals(GtrApiConstants.SecretType.CURVE_25519, data.getSecretType());

        GtrVerifyField originatorVerifyField = data.getVerifyFields().get(0);
        assertEquals("matched", originatorVerifyField.getMessage());
        assertEquals(GtrApiConstants.PiiStatus.MATCH, originatorVerifyField.getStatus());
        assertEquals(GtrApiConstants.VerifyField.Originator.NaturalPerson.NAME, originatorVerifyField.getType());

        GtrVerifyField beneficiaryVerifyField = data.getVerifyFields().get(1);
        assertEquals("matched", beneficiaryVerifyField.getMessage());
        assertEquals(GtrApiConstants.PiiStatus.MATCH, beneficiaryVerifyField.getStatus());
        assertEquals(GtrApiConstants.VerifyField.Beneficiary.NaturalPerson.NAME, beneficiaryVerifyField.getType());
    }

    @Test
    void testToFailedVerifyPiiWebhookResponse() {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();

        GtrWebhookVerifyPiiResponse response = GtrVerifyPiiMapper.toFailedVerifyPiiWebhookResponse(payload);
        GtrWebhookVerifyPiiResponse.VerifyPiiData data = response.getData();

        assertEquals(GtrApiConstants.VerifyStatus.PII_VERIFICATION_FAILED, response.getVerifyStatus());
        assertEquals("Verification Failed", response.getVerifyMessage());
        assertEquals("encrypted_payload", data.getEncryptedPayload());
        assertEquals("initiator_public_key", data.getInitiatorPublicKey());
        assertEquals("receiver_public_key", data.getReceiverPublicKey());
        assertEquals(GtrApiConstants.SecretType.CURVE_25519, data.getSecretType());

        GtrVerifyField originatorVerifyField = data.getVerifyFields().get(0);
        assertEquals("not matched", originatorVerifyField.getMessage());
        assertEquals(GtrApiConstants.PiiStatus.MISMATCH, originatorVerifyField.getStatus());
        assertEquals(GtrApiConstants.VerifyField.Originator.NaturalPerson.NAME, originatorVerifyField.getType());

        GtrVerifyField beneficiaryVerifyField = data.getVerifyFields().get(1);
        assertEquals("not matched", beneficiaryVerifyField.getMessage());
        assertEquals(GtrApiConstants.PiiStatus.MISMATCH, beneficiaryVerifyField.getStatus());
        assertEquals(GtrApiConstants.VerifyField.Beneficiary.NaturalPerson.NAME, beneficiaryVerifyField.getType());
    }

    private ITravelRuleNaturalPerson createITravelRuleNaturalPerson(String primaryName, String secondaryName) {
        ITravelRuleNaturalPersonName name = mock(ITravelRuleNaturalPersonName.class);
        when(name.getNameType()).thenReturn("LEGL");
        when(name.getPrimaryName()).thenReturn(primaryName);
        when(name.getSecondaryName()).thenReturn(secondaryName);

        ITravelRuleNaturalPerson naturalPerson = mock(ITravelRuleNaturalPerson.class);
        when(naturalPerson.getName()).thenReturn(name);

        return naturalPerson;
    }

    private GtrPiiVerifyWebhookPayload createGtrPiiVerifyWebhookPayload() {
        GtrPiiVerifyWebhookPayload payload = new GtrPiiVerifyWebhookPayload();
        payload.setRequestId("request_id");
        payload.setBeneficiaryVasp("beneficiary_vasp");
        payload.setOriginatorVasp("originator_vasp");
        payload.setAddress("address");
        payload.setEncryptedPayload("encrypted_payload");
        payload.setInitiatorPublicKey("initiator_public_key");
        payload.setReceiverPublicKey("receiver_public_key");

        return payload;
    }

    private GtrIvms101Payload createGtrIvms101Payload() {
        GtrPerson originatorPerson = createGtrPerson("type_1", "Nakamoto", "Satoshi");
        GtrPerson beneficiaryPerson = createGtrPerson("type_2", "Finney", "Hal");

        GtrOriginator originator = new GtrOriginator();
        originator.setOriginatorPersons(List.of(originatorPerson));

        GtrBeneficiary beneficiary = new GtrBeneficiary();
        beneficiary.setBeneficiaryPersons(List.of(beneficiaryPerson));

        GtrIvms101 ivms101 = new GtrIvms101();
        ivms101.setOriginator(originator);
        ivms101.setBeneficiary(beneficiary);

        GtrIvms101Payload payload = new GtrIvms101Payload();
        payload.setIvms101(ivms101);

        return payload;
    }

    private GtrPerson createGtrPerson(String type, String primaryName, String secondaryName) {
        GtrPersonNameIdentifier nameIdentifier = new GtrPersonNameIdentifier();
        nameIdentifier.setNameIdentifierType(type);
        nameIdentifier.setPrimaryIdentifier(primaryName);
        nameIdentifier.setSecondaryIdentifier(secondaryName);

        GtrPersonName personName = new GtrPersonName();
        personName.setNameIdentifiers(List.of(nameIdentifier));

        GtrNaturalPerson naturalPerson = new GtrNaturalPerson();
        naturalPerson.setName(personName);

        GtrPerson person = new GtrPerson();
        person.setNaturalPerson(naturalPerson);

        return person;
    }

}