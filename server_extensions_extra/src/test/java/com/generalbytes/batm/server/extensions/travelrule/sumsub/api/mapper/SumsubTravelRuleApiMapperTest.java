package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.mapper;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPersonName;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.SumsubVaspListResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction.SumsubSubmitTxWithoutApplicantRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubApplicant;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubCounterparty;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubInstitutionInfo;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubPaymentMethod;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.walletownershipconfirmation.SumsubConfirmWalletOwnershipRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SumsubTravelRuleApiMapperTest {

    @Test
    void testToITravelRuleVasp() {
        SumsubVaspListResponse.VaspDetail vaspDetail = mock(SumsubVaspListResponse.VaspDetail.class);
        when(vaspDetail.getId()).thenReturn("vasp_did");
        when(vaspDetail.getName()).thenReturn("vasp_name");

        ITravelRuleVasp vasp = SumsubTravelRuleApiMapper.toITravelRuleVasp(vaspDetail);

        assertEquals("vasp_did", vasp.getDid());
        assertEquals("vasp_name", vasp.getName());
    }

    private static Stream<Arguments> testToSumsubSubmitTxWithoutApplicantRequest_arguments() {
        return Stream.of(
                arguments("address:tag", "address", "tag"),
                arguments("address:", "address", null),
                arguments("address", "address", null),
                arguments("address:tag:any", "address", null)
        );
    }

    @ParameterizedTest
    @MethodSource("testToSumsubSubmitTxWithoutApplicantRequest_arguments")
    void testToSumsubSubmitTxWithoutApplicantRequest(String cryptoAddress, String expectedAddress, String expectedTag) {
        ITravelRuleTransferData transferData = createITravelRuleTransferData(cryptoAddress);
        BigDecimal cryptoAmount = BigDecimal.valueOf(0.00000021);

        SumsubSubmitTxWithoutApplicantRequest request
                = SumsubTravelRuleApiMapper.toSumsubSubmitTxWithoutApplicantRequest(transferData, cryptoAmount);

        assertEquals("envelope_public_id", request.getTxnId());
        assertEquals("travelRule", request.getType());
        assertEquals("crypto", request.getInfo().getCurrencyType());
        assertEquals("out", request.getInfo().getDirection());
        assertEquals("BTC", request.getInfo().getCurrencyCode());
        assertEquals(BigDecimal.valueOf(0.00000021), request.getInfo().getAmount());
        assertEquals("individual", request.getApplicant().getType());
        assertEquals("originator_public_id", request.getApplicant().getExternalUserId());
        assertEquals("originator_secondary_name", request.getApplicant().getFirstName());
        assertEquals("originator_primary_name", request.getApplicant().getLastName());
        assertEquals("originator_secondary_name originator_primary_name", request.getApplicant().getFullName());
        assertEquals("originator_vasp_did", request.getApplicant().getInstitutionInfo().getInternalId());
        assertEquals("originator_vasp_name", request.getApplicant().getInstitutionInfo().getName());
        assertEquals("individual", request.getCounterparty().getType());
        assertEquals("beneficiary_public_id", request.getCounterparty().getExternalUserId());
        assertEquals("beneficiary_secondary_name", request.getCounterparty().getFirstName());
        assertEquals("beneficiary_primary_name", request.getCounterparty().getLastName());
        assertEquals("beneficiary_secondary_name beneficiary_primary_name", request.getCounterparty().getFullName());
        assertEquals("beneficiary_vasp_did", request.getCounterparty().getInstitutionInfo().getInternalId());
        assertEquals("crypto", request.getCounterparty().getPaymentMethod().getType());
        assertEquals(expectedAddress, request.getCounterparty().getPaymentMethod().getAccountId());
        assertEquals(expectedTag, request.getCounterparty().getPaymentMethod().getMemo());
    }

    @Test
    void testToITravelRuleTransferInfo_submitTxWithoutApplicantResponse() {
        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("sumsub_id");

        ITravelRuleTransferInfo transferInfo = SumsubTravelRuleApiMapper.toITravelRuleTransferInfo(response);

        assertEquals("sumsub_id", transferInfo.getId());
    }

    @Test
    void testToITravelRuleTransferInfo_updateTransactionHashResponse() {
        SumsubUpdateTransactionHashResponse response = mock(SumsubUpdateTransactionHashResponse.class);
        when(response.getId()).thenReturn("sumsub_id");

        ITravelRuleTransferInfo transferInfo = SumsubTravelRuleApiMapper.toITravelRuleTransferInfo(response);

        assertEquals("sumsub_id", transferInfo.getId());
    }

    @Test
    void testToSumsubUpdateTransactionHashRequest() {
        ITravelRuleTransferUpdateRequest updateRequest = mock(ITravelRuleTransferUpdateRequest.class);
        when(updateRequest.getTransactionHash()).thenReturn("transaction_hash");

        SumsubUpdateTransactionHashRequest request = SumsubTravelRuleApiMapper.toSumsubUpdateTransactionHashRequest(updateRequest);

        assertEquals("transaction_hash", request.getPaymentTxnId());
    }

    @Test
    void testToITravelRuleIncomingTransferEvent() {
        SumsubWebhookMessage message = createSumsubWebhookMessage();
        SumsubTransactionInformationResponse response = createSumsubTransactionInformationResponse();

        ITravelRuleIncomingTransferEvent event = SumsubTravelRuleApiMapper.toITravelRuleIncomingTransferEvent(message, response);
        assertEquals("sumsub_id", event.getId());
        assertEquals("beneficiary_vasp_did", event.getBeneficiaryVaspDid());
        assertEquals("originator_vasp_did", event.getOriginatorVasp().getDid());
        assertEquals("originator_vasp_name", event.getOriginatorVasp().getName());
        assertEquals("originator_last_name", event.getOriginatorName().getPrimaryName());
        assertEquals("originator_first_name", event.getOriginatorName().getSecondaryName());
        assertEquals("originator_type", event.getOriginatorName().getNameType());
        assertEquals("beneficiary_last_name", event.getBeneficiaryName().getPrimaryName());
        assertEquals("beneficiary_first_name", event.getBeneficiaryName().getSecondaryName());
        assertEquals("beneficiary_type", event.getBeneficiaryName().getNameType());
        assertEquals("destination_address", event.getDestinationAddress());
        assertEquals("SumsubWebhookMessage("
                + "applicantId=applicant_id,"
                + " applicantType=applicant_type,"
                + " correlationId=correlation_id,"
                + " sandboxMode=true,"
                + " externalUserId=external_user_id,"
                + " type=type,"
                + " reviewResult=SumsubWebhookMessage.ReviewResult(reviewAnswer=review_answer, reviewRejectType=review_rejected_type),"
                + " reviewStatus=review_status,"
                + " createdAt=2025-09-17T12:00:00Z,"
                + " createdAtMs=1694952000000,"
                + " clientId=client_id,"
                + " kytTxnId=kyt_txn_id,"
                + " kytDataTxnId=kyt_data_txn_id,"
                + " kytTxnType=kyt_txn_type)", event.getRawData()
        );
    }

    @Test
    void testToSumsubConfirmWalletOwnershipRequest() {
        ITravelRuleTransferData transferData = createITravelRuleTransferData("crypto_address");

        SumsubConfirmWalletOwnershipRequest request = SumsubTravelRuleApiMapper.toSumsubConfirmWalletOwnershipRequest(transferData);

        assertNotNull(request.getApplicantParticipant());
        assertEquals("originator_secondary_name originator_primary_name", request.getApplicantParticipant().getFullName());
        assertEquals("originator_public_id", request.getApplicantParticipant().getExternalUserId());
        assertEquals("individual", request.getApplicantParticipant().getType());
    }

    private ITravelRuleTransferData createITravelRuleTransferData(String cryptoAddress) {
        return new ITravelRuleTransferData() {
            @Override
            public String getPublicId() {
                return "envelope_public_id";
            }

            @Override
            public ITravelRuleNaturalPerson getOriginator() {
                return createITravelRuleNaturalPerson(
                        "originator_primary_name",
                        "originator_secondary_name",
                        "originator_name_type",
                        "originator_public_id"
                );
            }

            @Override
            public ITravelRuleNaturalPerson getBeneficiary() {
                return createITravelRuleNaturalPerson(
                        "beneficiary_primary_name",
                        "beneficiary_secondary_name",
                        "beneficiary_name_type",
                        "beneficiary_public_id"
                );
            }

            @Override
            public ITravelRuleVasp getOriginatorVasp() {
                return createITravelRuleVasp("originator_vasp_did", "originator_vasp_name");
            }

            @Override
            public ITravelRuleVasp getBeneficiaryVasp() {
                return createITravelRuleVasp("beneficiary_vasp_did", "beneficiary_vasp_name");
            }

            @Override
            public String getTransactionAsset() {
                return "BTC";
            }

            @Override
            public long getTransactionAmount() {
                return -1;
            }

            @Override
            public String getDestinationAddress() {
                return cryptoAddress;
            }

            @Override
            public BigDecimal getFiatAmount() {
                return null;
            }

            @Override
            public String getFiatCurrency() {
                return null;
            }

            @Override
            public String getTransactionHash() {
                return null;
            }
        };
    }

    private ITravelRuleNaturalPerson createITravelRuleNaturalPerson(String primaryName,
                                                                    String secondaryName,
                                                                    String nameType,
                                                                    String identityPublicId
    ) {
        return new ITravelRuleNaturalPerson() {
            @Override
            public ITravelRuleNaturalPersonName getName() {
                return new ITravelRuleNaturalPersonName() {
                    @Override
                    public String getPrimaryName() {
                        return primaryName;
                    }

                    @Override
                    public String getSecondaryName() {
                        return secondaryName;
                    }

                    @Override
                    public String getNameType() {
                        return nameType;
                    }
                };
            }

            @Override
            public String getIdentityPublicId() {
                return identityPublicId;
            }
        };
    }

    private ITravelRuleVasp createITravelRuleVasp(String did, String name) {
        return new ITravelRuleVasp() {
            @Override
            public String getDid() {
                return did;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    private SumsubWebhookMessage createSumsubWebhookMessage() {
        SumsubWebhookMessage.ReviewResult reviewResult = new SumsubWebhookMessage.ReviewResult();
        reviewResult.setReviewAnswer("review_answer");
        reviewResult.setReviewRejectType("review_rejected_type");

        SumsubWebhookMessage message = new SumsubWebhookMessage();
        message.setApplicantId("applicant_id");
        message.setApplicantType("applicant_type");
        message.setCorrelationId("correlation_id");
        message.setSandboxMode(true);
        message.setExternalUserId("external_user_id");
        message.setType("type");
        message.setReviewResult(reviewResult);
        message.setReviewStatus("review_status");
        message.setCreatedAt("2025-09-17T12:00:00Z");
        message.setCreatedAtMs("1694952000000");
        message.setClientId("client_id");
        message.setKytTxnId("kyt_txn_id");
        message.setKytDataTxnId("kyt_data_txn_id");
        message.setKytTxnType("kyt_txn_type");

        return message;
    }

    private SumsubTransactionInformationResponse createSumsubTransactionInformationResponse() {
        SumsubInstitutionInfo applicantInstitutionInfo = new SumsubInstitutionInfo();
        applicantInstitutionInfo.setInternalId("originator_vasp_did");
        applicantInstitutionInfo.setName("originator_vasp_name");

        SumsubApplicant applicant = new SumsubApplicant();
        applicant.setFirstName("originator_first_name");
        applicant.setLastName("originator_last_name");
        applicant.setType("originator_type");
        applicant.setInstitutionInfo(applicantInstitutionInfo);

        SumsubInstitutionInfo counterpartyInstitutionInfo = new SumsubInstitutionInfo();
        counterpartyInstitutionInfo.setInternalId("beneficiary_vasp_did");

        SumsubPaymentMethod counterpartyPaymentMethod = new SumsubPaymentMethod();
        counterpartyPaymentMethod.setAccountId("destination_address");

        SumsubCounterparty counterparty = new SumsubCounterparty();
        counterparty.setFirstName("beneficiary_first_name");
        counterparty.setLastName("beneficiary_last_name");
        counterparty.setType("beneficiary_type");
        counterparty.setInstitutionInfo(counterpartyInstitutionInfo);
        counterparty.setPaymentMethod(counterpartyPaymentMethod);

        SumsubTransactionInformationResponse.TransactionData transactionData = new SumsubTransactionInformationResponse.TransactionData();
        transactionData.setApplicant(applicant);
        transactionData.setCounterparty(counterparty);

        SumsubTransactionInformationResponse response = new SumsubTransactionInformationResponse();
        response.setId("sumsub_id");
        response.setData(transactionData);

        return response;
    }

}