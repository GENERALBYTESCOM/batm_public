package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.mapper;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPersonName;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.SumsubVaspListResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubIdentity;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubInstitutionInfo;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubPaymentMethod;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction.SumsubSubmitTxWithoutApplicantRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInfo;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.walletownershipconfirmation.SumsubConfirmWalletOwnershipRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Sumsub mapper for mapping request and response objects to/from Sumsub API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SumsubTravelRuleApiMapper {

    /**
     * Maps {@link SumsubVaspListResponse.VaspDetail} to {@link ITravelRuleVasp}.
     *
     * @param vaspDetail {@link SumsubVaspListResponse.VaspDetail}
     * @return {@link ITravelRuleVasp}
     */
    public static ITravelRuleVasp toITravelRuleVasp(SumsubVaspListResponse.VaspDetail vaspDetail) {
        return new ITravelRuleVasp() {
            @Override
            public String getDid() {
                return vaspDetail.getId();
            }

            @Override
            public String getName() {
                return vaspDetail.getName();
            }
        };
    }

    /**
     * Maps {@link ITravelRuleTransferData} to {@link SumsubSubmitTxWithoutApplicantRequest}.
     *
     * @param transferData {@link ITravelRuleTransferData}
     * @param cryptoAmount Crypto amount in standard units.
     * @return {@link SumsubSubmitTxWithoutApplicantRequest}
     */
    public static SumsubSubmitTxWithoutApplicantRequest toSumsubSubmitTxWithoutApplicantRequest(ITravelRuleTransferData transferData,
                                                                                                BigDecimal cryptoAmount
    ) {
        SumsubSubmitTxWithoutApplicantRequest request = new SumsubSubmitTxWithoutApplicantRequest();
        request.setTxnId(transferData.getPublicId());
        request.setType(SumsubTravelRuleApiConstants.TRAVEL_RULE_REQUEST_TYPE);
        request.setInfo(toSumsubTransactionInfo(transferData, cryptoAmount));
        request.setApplicant(toSumsubApplicant(transferData));
        request.setCounterparty(toSumsubCounterparty(transferData));

        return request;
    }

    private static SumsubTransactionInfo toSumsubTransactionInfo(ITravelRuleTransferData transferData, BigDecimal cryptoAmount) {
        SumsubTransactionInfo transactionInfo = new SumsubTransactionInfo();
        transactionInfo.setCurrencyType(SumsubTravelRuleApiConstants.TransactionInfo.CRYPTO_CURRENCY_TYPE);
        transactionInfo.setDirection(SumsubTravelRuleApiConstants.TransactionInfo.OUT_DIRECTION);
        transactionInfo.setCurrencyCode(transferData.getTransactionAsset());
        transactionInfo.setAmount(cryptoAmount);

        return transactionInfo;
    }

    private static SumsubIdentity toSumsubApplicant(ITravelRuleTransferData transferData) {
        SumsubIdentity applicant = new SumsubIdentity();
        mapSumsubIdentity(applicant, transferData.getOriginator(), transferData.getOriginatorVasp());

        return applicant;
    }

    private static SumsubIdentity toSumsubCounterparty(ITravelRuleTransferData transferData) {
        SumsubIdentity counterparty = new SumsubIdentity();
        mapSumsubIdentity(counterparty, transferData.getBeneficiary(), transferData.getBeneficiaryVasp());
        counterparty.setPaymentMethod(toSumsubPaymentMethod(transferData));

        return counterparty;
    }

    private static void mapSumsubIdentity(SumsubIdentity identity, ITravelRuleNaturalPerson naturalPerson, ITravelRuleVasp vasp) {
        identity.setType(SumsubTravelRuleApiConstants.IdentityInfo.INDIVIDUAL_TYPE);
        identity.setExternalUserId(naturalPerson.getIdentityPublicId());
        identity.setFirstName(naturalPerson.getName().getSecondaryName());
        identity.setLastName(naturalPerson.getName().getPrimaryName());
        identity.setFullName(getFullName(naturalPerson.getName()));
        identity.setInstitutionInfo(createSumsubInstitutionInfo(vasp));
    }

    private static SumsubPaymentMethod toSumsubPaymentMethod(ITravelRuleTransferData transferData) {
        AddressWithTag addressWithTag = getAddressWithTag(transferData.getDestinationAddress());

        SumsubPaymentMethod paymentMethod = new SumsubPaymentMethod();
        paymentMethod.setType(SumsubTravelRuleApiConstants.PaymentMethod.CRYPTO_TYPE);
        paymentMethod.setAccountId(addressWithTag.address());
        paymentMethod.setMemo(addressWithTag.tag());

        return paymentMethod;
    }

    private static SumsubInstitutionInfo createSumsubInstitutionInfo(ITravelRuleVasp vasp) {
        SumsubInstitutionInfo institutionInfo = new SumsubInstitutionInfo();
        institutionInfo.setInternalId(vasp.getDid());
        institutionInfo.setName(vasp.getName());

        return institutionInfo;
    }

    private static String getFullName(ITravelRuleNaturalPersonName naturalPersonName) {
        return naturalPersonName.getSecondaryName() + " " + naturalPersonName.getPrimaryName();
    }

    private static AddressWithTag getAddressWithTag(String address) {
        String[] addressAndTag = address.split(":");
        if (addressAndTag.length == 2) {
            return new AddressWithTag(addressAndTag[0], addressAndTag[1]);
        }

        return new AddressWithTag(addressAndTag[0], null);
    }

    private record AddressWithTag(String address, String tag) {

    }

    /**
     * Maps {@link SumsubTransactionInformationResponse} to {@link ITravelRuleTransferInfo}.
     *
     * @param response {@link SumsubTransactionInformationResponse}
     * @return {@link ITravelRuleTransferInfo}
     */
    public static ITravelRuleTransferInfo toITravelRuleTransferInfo(SumsubTransactionInformationResponse response) {
        return response::getId;
    }

    /**
     * Maps {@link SumsubUpdateTransactionHashResponse} to {@link ITravelRuleTransferInfo}.
     *
     * @param response {@link SumsubUpdateTransactionHashResponse}
     * @return {@link ITravelRuleTransferInfo}
     */
    public static ITravelRuleTransferInfo toITravelRuleTransferInfo(SumsubUpdateTransactionHashResponse response) {
        return response::getId;
    }

    /**
     * Maps {@link ITravelRuleTransferUpdateRequest} to {@link SumsubUpdateTransactionHashRequest}.
     *
     * @param updateRequest {@link ITravelRuleTransferUpdateRequest}
     * @return {@link SumsubUpdateTransactionHashRequest}
     */
    public static SumsubUpdateTransactionHashRequest toSumsubUpdateTransactionHashRequest(ITravelRuleTransferUpdateRequest updateRequest) {
        SumsubUpdateTransactionHashRequest request = new SumsubUpdateTransactionHashRequest();
        request.setPaymentTxnId(updateRequest.getTransactionHash());

        return request;
    }

    /**
     * Maps {@link SumsubTransactionInformationResponse} to {@link ITravelRuleIncomingTransferEvent}.
     *
     * @param response {@link SumsubTransactionInformationResponse}
     * @return {@link ITravelRuleIncomingTransferEvent}
     */
    public static ITravelRuleIncomingTransferEvent toITravelRuleIncomingTransferEvent(SumsubWebhookMessage message,
                                                                                      SumsubTransactionInformationResponse response
    ) {
        SumsubIdentity originator = response.getData().getCounterparty();
        SumsubIdentity beneficiary = response.getData().getApplicant();
        return new ITravelRuleIncomingTransferEvent() {
            @Override
            public String getId() {
                return response.getId();
            }

            @Override
            public String getBeneficiaryVaspDid() {
                return beneficiary.getInstitutionInfo().getInternalId();
            }

            @Override
            public ITravelRuleVasp getOriginatorVasp() {
                return createVasp(originator);
            }

            @Override
            public ITravelRuleNaturalPersonName getOriginatorName() {
                return createNaturalPersonName(originator);
            }

            @Override
            public ITravelRuleNaturalPersonName getBeneficiaryName() {
                return createNaturalPersonName(beneficiary);
            }

            @Override
            public String getDestinationAddress() {
                return beneficiary.getPaymentMethod().getAccountId();
            }

            @Override
            public String getRawData() {
                return message.toString();
            }
        };
    }

    /**
     * Maps {@link ITravelRuleTransferData} to {@link SumsubConfirmWalletOwnershipRequest}.
     *
     * @param transferData {@link ITravelRuleTransferData}
     * @return {@link SumsubConfirmWalletOwnershipRequest}
     */
    public static SumsubConfirmWalletOwnershipRequest toSumsubConfirmWalletOwnershipRequest(ITravelRuleTransferData transferData) {
        SumsubConfirmWalletOwnershipRequest.ApplicantParticipant participant
                = new SumsubConfirmWalletOwnershipRequest.ApplicantParticipant();
        participant.setFullName(getFullName(transferData.getOriginator().getName()));
        participant.setExternalUserId(transferData.getOriginator().getIdentityPublicId());
        participant.setType(SumsubTravelRuleApiConstants.IdentityInfo.INDIVIDUAL_TYPE);

        SumsubConfirmWalletOwnershipRequest request = new SumsubConfirmWalletOwnershipRequest();
        request.setApplicantParticipant(participant);

        return request;
    }

    private static ITravelRuleVasp createVasp(SumsubIdentity counterparty) {
        SumsubInstitutionInfo institutionInfo = counterparty.getInstitutionInfo();
        return new ITravelRuleVasp() {
            @Override
            public String getDid() {
                return institutionInfo.getInternalId();
            }

            @Override
            public String getName() {
                return institutionInfo.getName();
            }
        };
    }

    private static ITravelRuleNaturalPersonName createNaturalPersonName(SumsubIdentity identity) {
        return new ITravelRuleNaturalPersonName() {
            @Override
            public String getPrimaryName() {
                return identity.getLastName();
            }

            @Override
            public String getSecondaryName() {
                return identity.getFirstName();
            }

            @Override
            public String getNameType() {
                return identity.getType();
            }
        };
    }

}
