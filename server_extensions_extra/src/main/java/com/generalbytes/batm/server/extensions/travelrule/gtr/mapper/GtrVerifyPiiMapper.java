package com.generalbytes.batm.server.extensions.travelrule.gtr.mapper;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
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
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrOriginator;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101Payload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrPerson;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrPersonName;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrPersonNameIdentifier;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.Curve25519Encryptor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * GTR mapper for mapping request for PII verification.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GtrVerifyPiiMapper {

    /**
     * Maps {@link ITravelRuleTransferData} to {@link GtrIvms101Payload}.
     *
     * @param transferData {@link ITravelRuleTransferData}
     * @return {@link GtrIvms101Payload}
     */
    public static GtrIvms101Payload toGtrIvms101Payload(ITravelRuleTransferData transferData) {
        GtrIvms101 ivms101 = createIvms101(transferData);

        GtrIvms101Payload payload = new GtrIvms101Payload();
        payload.setIvms101(ivms101);

        return payload;
    }

    private static GtrIvms101 createIvms101(ITravelRuleTransferData transferData) {
        GtrOriginator originator = createOriginator(transferData);
        GtrBeneficiary beneficiary = createBeneficiary(transferData);

        GtrIvms101 ivms101 = new GtrIvms101();
        ivms101.setOriginator(originator);
        ivms101.setBeneficiary(beneficiary);

        return ivms101;
    }

    private static GtrOriginator createOriginator(ITravelRuleTransferData transferData) {
        GtrPerson originatorPerson = createGtrPerson(transferData.getOriginator().getName());

        GtrOriginator originator = new GtrOriginator();
        originator.setOriginatorPersons(List.of(originatorPerson));

        return originator;
    }

    private static GtrBeneficiary createBeneficiary(ITravelRuleTransferData transferData) {
        GtrPerson beneficiaryPerson = createGtrPerson(transferData.getBeneficiary().getName());

        GtrBeneficiary beneficiary = new GtrBeneficiary();
        beneficiary.setBeneficiaryPersons(List.of(beneficiaryPerson));

        return beneficiary;
    }

    private static GtrPerson createGtrPerson(ITravelRuleNaturalPersonName naturalPersonName) {
        GtrPersonNameIdentifier nameIdentifier = new GtrPersonNameIdentifier();
        nameIdentifier.setNameIdentifierType(naturalPersonName.getNameType());
        nameIdentifier.setPrimaryIdentifier(naturalPersonName.getPrimaryName());
        nameIdentifier.setSecondaryIdentifier(naturalPersonName.getSecondaryName());

        GtrPersonName personName = new GtrPersonName();
        personName.setNameIdentifiers(List.of(nameIdentifier));

        GtrNaturalPerson naturalPerson = new GtrNaturalPerson();
        naturalPerson.setName(personName);

        GtrPerson person = new GtrPerson();
        person.setNaturalPerson(naturalPerson);

        return person;
    }

    /**
     * Maps values to {@link GtrVerifyPiiRequest} object.
     *
     * @param transferData           {@link ITravelRuleTransferData}
     * @param requestId              Request ID.
     * @param initiatorVaspPublicKey Public key of initiator VASP.
     * @param targetVaspPublicKey    Public key of target VASP.
     * @param encryptedPayload       Encrypted payload using {@link Curve25519Encryptor#encrypt(String, String, String)}.
     * @return {@link GtrVerifyPiiRequest}
     */
    public static GtrVerifyPiiRequest toGtrVerifyPiiRequest(ITravelRuleTransferData transferData,
                                                            String requestId,
                                                            String initiatorVaspPublicKey,
                                                            String targetVaspPublicKey,
                                                            String encryptedPayload
    ) {
        GtrVerifyPiiRequest request = new GtrVerifyPiiRequest();
        request.setRequestId(requestId);
        request.setInitiatorPublicKey(initiatorVaspPublicKey);
        request.setTargetVaspCode(transferData.getBeneficiaryVasp().getDid());
        request.setTargetVaspPublicKey(targetVaspPublicKey);
        request.setEncryptedPayload(encryptedPayload);
        request.setPiiSpecVersion("ivms101-2020");
        request.setSecretType(GtrApiConstants.SecretType.CURVE_25519);
        request.setAmount(Long.toString(transferData.getTransactionAmount()));
        request.setFiatPrice(transferData.getFiatAmount().toString());
        request.setFiatName(transferData.getFiatCurrency());
        request.setLawThresholdEnabled(false);
        request.setExpectVerifyFields(List.of(
                GtrApiConstants.VerifyField.Originator.NaturalPerson.NAME,
                GtrApiConstants.VerifyField.Beneficiary.NaturalPerson.NAME
        ));

        return request;
    }

    /**
     * Maps values to {@link ITravelRuleIncomingTransferEvent}.
     *
     * @param payload Callback data for the webhook used for PII verification.
     * @param ivms101 An object containing sensitive PII.
     * @param rawData Raw data received on a webhook.
     * @return {@link ITravelRuleIncomingTransferEvent} used to notify the transfer listener about an incoming transfer.
     */
    public static ITravelRuleIncomingTransferEvent toIncomingTransferEvent(GtrPiiVerifyWebhookPayload payload,
                                                                           GtrIvms101Payload ivms101,
                                                                           String rawData
    ) {
        return new ITravelRuleIncomingTransferEvent() {
            @Override
            public String getId() {
                return payload.getRequestId();
            }

            @Override
            public String getBeneficiaryVaspDid() {
                return payload.getBeneficiaryVasp();
            }

            @Override
            public ITravelRuleVasp getOriginatorVasp() {
                return createVasp(payload.getOriginatorVasp());
            }

            @Override
            public ITravelRuleNaturalPersonName getOriginatorName() {
                return createNaturalPersonName(ivms101.getIvms101().getOriginator().getOriginatorPersons().get(0));
            }

            @Override
            public ITravelRuleNaturalPersonName getBeneficiaryName() {
                return createNaturalPersonName(ivms101.getIvms101().getBeneficiary().getBeneficiaryPersons().get(0));
            }

            @Override
            public String getDestinationAddress() {
                return payload.getAddress();
            }

            @Override
            public String getRawData() {
                return rawData;
            }
        };
    }

    private static ITravelRuleVasp createVasp(String vaspDid) {
        return new ITravelRuleVasp() {
            @Override
            public String getDid() {
                return vaspDid;
            }

            @Override
            public String getName() {
                return null;
            }
        };
    }

    private static ITravelRuleNaturalPersonName createNaturalPersonName(GtrPerson person) {
        GtrPersonNameIdentifier nameIdentifier = person.getNaturalPerson().getName().getNameIdentifiers().get(0);
        return new ITravelRuleNaturalPersonName() {
            @Override
            public String getPrimaryName() {
                return nameIdentifier.getPrimaryIdentifier();
            }

            @Override
            public String getSecondaryName() {
                return nameIdentifier.getSecondaryIdentifier();
            }

            @Override
            public String getNameType() {
                return nameIdentifier.getNameIdentifierType();
            }
        };
    }

    /**
     * Creates a response for successful PII verification.
     *
     * @param piiVerifyPayload Payload.
     * @return Successful response.
     */
    public static GtrWebhookVerifyPiiResponse toSuccessVerifyPiiWebhookResponse(GtrPiiVerifyWebhookPayload piiVerifyPayload) {
        GtrVerifyField originatorName = createMatchedVerifyField(GtrApiConstants.VerifyField.Originator.NaturalPerson.NAME);
        GtrVerifyField beneficiaryName = createMatchedVerifyField(GtrApiConstants.VerifyField.Beneficiary.NaturalPerson.NAME);

        List<GtrVerifyField> verifyFields = new ArrayList<>();
        verifyFields.add(originatorName);
        verifyFields.add(beneficiaryName);

        GtrWebhookVerifyPiiResponse response = createGtrWebhookVerifyPiiResponse(piiVerifyPayload);
        response.setVerifyStatus(GtrApiConstants.VerifyStatus.SUCCESS);
        response.setVerifyMessage("Verification Success");
        response.getData().setVerifyFields(verifyFields);

        return response;
    }

    /**
     * Creates a response for failed PII verification.
     *
     * @param piiVerifyPayload Payload.
     * @return Failed response.
     */
    public static GtrWebhookVerifyPiiResponse toFailedVerifyPiiWebhookResponse(GtrPiiVerifyWebhookPayload piiVerifyPayload) {
        GtrVerifyField originatorName = createMismatchedGtrVerifyField(GtrApiConstants.VerifyField.Originator.NaturalPerson.NAME);
        GtrVerifyField beneficiaryName = createMismatchedGtrVerifyField(GtrApiConstants.VerifyField.Beneficiary.NaturalPerson.NAME);

        List<GtrVerifyField> verifyFields = new ArrayList<>();
        verifyFields.add(originatorName);
        verifyFields.add(beneficiaryName);

        GtrWebhookVerifyPiiResponse response = createGtrWebhookVerifyPiiResponse(piiVerifyPayload);
        response.setVerifyStatus(GtrApiConstants.VerifyStatus.PII_VERIFICATION_FAILED);
        response.setVerifyMessage("Verification Failed");
        response.getData().setVerifyFields(verifyFields);

        return response;
    }

    private static GtrWebhookVerifyPiiResponse createGtrWebhookVerifyPiiResponse(GtrPiiVerifyWebhookPayload piiVerifyPayload) {
        GtrWebhookVerifyPiiResponse response = new GtrWebhookVerifyPiiResponse();

        GtrWebhookVerifyPiiResponse.VerifyPiiData data = response.getData();
        data.setEncryptedPayload(piiVerifyPayload.getEncryptedPayload());
        data.setInitiatorPublicKey(piiVerifyPayload.getInitiatorPublicKey());
        data.setReceiverPublicKey(piiVerifyPayload.getReceiverPublicKey());
        data.setSecretType(GtrApiConstants.SecretType.CURVE_25519);

        return response;
    }

    private static GtrVerifyField createMatchedVerifyField(String type) {
        GtrVerifyField gtrVerifyField = new GtrVerifyField();
        gtrVerifyField.setMessage("matched");
        gtrVerifyField.setStatus(GtrApiConstants.PiiStatus.MATCH);
        gtrVerifyField.setType(type);

        return gtrVerifyField;
    }

    private static GtrVerifyField createMismatchedGtrVerifyField(String type) {
        GtrVerifyField gtrVerifyField = new GtrVerifyField();
        gtrVerifyField.setMessage("not matched");
        gtrVerifyField.setStatus(GtrApiConstants.PiiStatus.MISMATCH);
        gtrVerifyField.setType(type);

        return gtrVerifyField;
    }

}
