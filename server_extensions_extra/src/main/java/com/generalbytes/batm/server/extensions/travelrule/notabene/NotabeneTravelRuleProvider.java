package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.CryptoWalletType;
import com.generalbytes.batm.server.extensions.travelrule.IIdentityWalletEvaluationRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleWalletInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneBeneficiary;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneCryptoAddressType;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNameIdentifier;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNameIdentifierType;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneOriginator;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabenePerson;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabenePersonName;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransactionBlockchainInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferCreateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneVaspInfoSimple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Travel Rule provider for Notabene.
 */
@Slf4j
@AllArgsConstructor
public class NotabeneTravelRuleProvider implements ITravelRuleProvider {

    public static final String NAME = "Notabene Travel Rule Provider";

    private ITravelRuleProviderCredentials credentials;
    private final NotabeneConfiguration configuration;
    private final NotabeneAuthService notabeneAuthService;
    private final NotabeneService notabeneService;
    private final NotabeneTransferPublisher notabeneTransferPublisher;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ITravelRuleWalletInfo getWalletInfo(IIdentityWalletEvaluationRequest walletEvaluationRequest) {
        NotabeneAddressOwnershipInfoRequest request = createNotabeneAddressOwnershipInfoRequest(walletEvaluationRequest);
        NotabeneAddressOwnershipInfoResponse ownershipInformation = notabeneService.getAddressOwnershipInformation(credentials, request);
        return mapToTravelRuleWalletInfo(ownershipInformation);
    }

    @Override
    public List<ITravelRuleVasp> getAllVasps() {
        List<NotabeneVaspInfoSimple> vaspInfoList = notabeneService.getAllVasps(credentials);
        return mapToTravelRuleVasps(vaspInfoList);
    }

    @Override
    public ITravelRuleTransferInfo createTransfer(ITravelRuleTransferData outgoingTransferData) {
        NotabeneTransferCreateRequest request = createNotabeneTransferCreateRequest(outgoingTransferData);
        NotabeneTransferInfo notabeneTransferInfo = notabeneService.createTransfer(credentials, request);
        NotabeneTransferInfo latestTransferInfo = attemptApproveTransfer(outgoingTransferData, notabeneTransferInfo);
        if (latestTransferInfo != null) {
            return mapToTravelRuleTransferInfo(latestTransferInfo);
        }

        return null;
    }

    private NotabeneTransferInfo attemptApproveTransfer(ITravelRuleTransferData outgoingTransferData,
                                                        NotabeneTransferInfo notabeneTransferInfo) {
        if (notabeneTransferInfo == null) {
            return null;
        }

        if (transferNeedsApproval(notabeneTransferInfo)) {
            NotabeneTransferInfo approvedTransferInfo = notabeneService.approveTransfer(credentials, notabeneTransferInfo.getId());
            if (approvedTransferInfo != null) {
                log.info("Auto-approved new transfer at Notabene. publicId: {}, externalId: {}",
                    outgoingTransferData.getPublicId(), notabeneTransferInfo.getId());
                return approvedTransferInfo;
            } else {
                log.error("Failed to approve new transfer at Notabene. publicId: {}, externalId: {}",
                    outgoingTransferData.getPublicId(), notabeneTransferInfo.getId());
            }
        }

        return notabeneTransferInfo;
    }

    private boolean transferNeedsApproval(NotabeneTransferInfo notabeneTransferInfo) {
        return configuration.isAutomaticApprovalOfOutgoingTransfersEnabled()
            && notabeneTransferInfo.getStatus() == NotabeneTransferStatus.NEW;
    }

    @Override
    public boolean registerStatusUpdateListener(ITravelRuleTransferUpdateListener listener) {
        if (notabeneService.registerWebhook(credentials)) {
            NotabeneTransferUpdateListener notabeneListener = new NotabeneTransferStatusUpdateListener(listener);
            notabeneTransferPublisher.registerListener(credentials.getVaspDid(), notabeneListener);
            return true;
        }
        return false;
    }

    @Override
    public boolean unregisterStatusUpdateListener() {
        notabeneTransferPublisher.unregisterListener(credentials.getVaspDid());
        return true;
    }

    @Override
    public ITravelRuleTransferInfo updateTransfer(ITravelRuleTransferUpdateRequest request) {
        NotabeneTransferUpdateRequest notabeneRequest = mapToNotabeneUpdateRequest(request);
        NotabeneTransferInfo notabeneTransferInfo = notabeneService.updateTransfer(credentials, notabeneRequest);
        if (notabeneTransferInfo == null) {
            return null;
        }

        return mapToTravelRuleTransferInfo(notabeneTransferInfo);
    }

    @Override
    public void notifyProviderConfigurationChanged() {
        // Handled by the NotabeneProviderFactory and #updateCredentials
    }

    @Override
    public boolean testProviderConfiguration() {
        log.info("A configuration test was requested for {}, clientId: {}", NAME, credentials.getClientId());

        return notabeneService.testProviderCredentials(credentials);
    }

    /**
     * Updates the current credentials if they differ from the provided ones.
     *
     * <p>If the new credentials are identical to the current credentials, no action is taken.
     * Otherwise, the method invalidates the existing access token, and then sets the new credentials.</p>
     *
     * @param credentials The new credentials
     */
    void updateCredentials(ITravelRuleProviderCredentials credentials) {
        if (credentialsMatch(credentials, this.credentials)) {
            return;
        }
        log.debug("Notabene Travel Rule Provider credentials changed for VASP {}.", credentials.getVaspDid());
        // Invalidate the current access token using the old credentials
        notabeneAuthService.removeAccessToken(this.credentials);
        // Start using the new credentials
        this.credentials = credentials;
    }

    private boolean credentialsMatch(ITravelRuleProviderCredentials credentials1, ITravelRuleProviderCredentials credentials2) {
        return credentials1.getClientId().equals(credentials2.getClientId())
            && credentials1.getClientSecret().equals(credentials2.getClientSecret());
    }

    private NotabeneTransferUpdateRequest mapToNotabeneUpdateRequest(ITravelRuleTransferUpdateRequest request) {
        NotabeneTransferUpdateRequest notabeneRequest = new NotabeneTransferUpdateRequest();
        notabeneRequest.setId(request.getId());
        notabeneRequest.setTxHash(request.getTransactionHash());
        return notabeneRequest;
    }

    private ITravelRuleTransferInfo mapToTravelRuleTransferInfo(NotabeneTransferInfo notabeneTransferInfo) {
        return notabeneTransferInfo::getId;
    }

    private NotabeneTransferCreateRequest createNotabeneTransferCreateRequest(ITravelRuleTransferData outgoingTransferData) {
        NotabeneTransferCreateRequest request = new NotabeneTransferCreateRequest();
        request.setTransactionRef(outgoingTransferData.getPublicId());
        request.setTransactionAsset(outgoingTransferData.getTransactionAsset());
        request.setTransactionAmount(String.valueOf(outgoingTransferData.getTransactionAmount()));
        request.setTransactionBlockchainInfo(createBlockchainInfo(outgoingTransferData));
        request.setOriginatorVaspDid(outgoingTransferData.getOriginatorVasp().getDid());
        request.setBeneficiaryVaspDid(outgoingTransferData.getBeneficiaryVasp().getDid());
        request.setOriginator(createOriginator(outgoingTransferData.getOriginator()));
        request.setBeneficiary(createBeneficiary(outgoingTransferData.getBeneficiary()));
        return request;
    }

    private NotabeneBeneficiary createBeneficiary(ITravelRuleNaturalPerson beneficiaryNaturalPerson) {
        NotabenePerson person = createPerson(beneficiaryNaturalPerson);
        NotabeneBeneficiary beneficiary = new NotabeneBeneficiary();
        beneficiary.setAccountNumber(List.of(beneficiaryNaturalPerson.getIdentityPublicId()));
        beneficiary.setBeneficiaryPersons(List.of(person));
        return beneficiary;
    }

    private NotabeneOriginator createOriginator(ITravelRuleNaturalPerson originatorNaturalPerson) {
        NotabenePerson person = createPerson(originatorNaturalPerson);
        NotabeneOriginator originator = new NotabeneOriginator();
        originator.setAccountNumber(List.of(originatorNaturalPerson.getIdentityPublicId()));
        originator.setOriginatorPersons(List.of(person));
        return originator;
    }

    private NotabenePerson createPerson(ITravelRuleNaturalPerson travelRuleNaturalPerson) {
        NotabeneNameIdentifier notabenePersonNameIdentifier = new NotabeneNameIdentifier();
        notabenePersonNameIdentifier.setPrimaryIdentifier(travelRuleNaturalPerson.getName().getPrimaryName());
        notabenePersonNameIdentifier.setSecondaryIdentifier(travelRuleNaturalPerson.getName().getSecondaryName());
        notabenePersonNameIdentifier.setNameIdentifierType(getNameIdentifierType(travelRuleNaturalPerson));

        NotabenePersonName personName = new NotabenePersonName();
        personName.setNameIdentifier(List.of(notabenePersonNameIdentifier));

        NotabeneNaturalPerson naturalPerson = new NotabeneNaturalPerson();
        naturalPerson.setName(List.of(personName));
        naturalPerson.setCustomerIdentification(travelRuleNaturalPerson.getIdentityPublicId());

        NotabenePerson person = new NotabenePerson();
        person.setNaturalPerson(naturalPerson);
        return person;
    }

    private NotabeneNameIdentifierType getNameIdentifierType(ITravelRuleNaturalPerson travelRuleNaturalPerson) {
        try {
            if (travelRuleNaturalPerson.getName().getNameType() != null) {
                return NotabeneNameIdentifierType.valueOf(travelRuleNaturalPerson.getName().getNameType());
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }
        return null;
    }

    private NotabeneTransactionBlockchainInfo createBlockchainInfo(ITravelRuleTransferData outgoingTransferData) {
        NotabeneTransactionBlockchainInfo blockchainInfo = new NotabeneTransactionBlockchainInfo();
        blockchainInfo.setDestination(outgoingTransferData.getDestinationAddress());
        blockchainInfo.setTxHash(outgoingTransferData.getTransactionHash());
        return blockchainInfo;
    }

    private List<ITravelRuleVasp> mapToTravelRuleVasps(List<NotabeneVaspInfoSimple> vaspInfoList) {
        List<ITravelRuleVasp> vasps = new ArrayList<>(vaspInfoList.size());
        for (NotabeneVaspInfoSimple vaspInfo : vaspInfoList) {
            vasps.add(initializeITravelRuleVasp(vaspInfo));
        }
        return vasps;
    }

    private ITravelRuleVasp initializeITravelRuleVasp(NotabeneVaspInfoSimple vaspInfo) {
        return new ITravelRuleVasp() {
            @Override
            public String getDid() {
                return vaspInfo.getDid();
            }

            @Override
            public String getName() {
                return vaspInfo.getName();
            }
        };
    }

    private NotabeneAddressOwnershipInfoRequest createNotabeneAddressOwnershipInfoRequest(IIdentityWalletEvaluationRequest walletEvaluationRequest) {
        NotabeneAddressOwnershipInfoRequest request = new NotabeneAddressOwnershipInfoRequest();
        request.setVaspDid(credentials.getVaspDid());
        request.setAddress(walletEvaluationRequest.getCryptoAddress());
        request.setAsset(walletEvaluationRequest.getCryptocurrency());
        return request;
    }

    private ITravelRuleWalletInfo mapToTravelRuleWalletInfo(NotabeneAddressOwnershipInfoResponse ownershipInformation) {
        if (ownershipInformation == null) {
            return new TravelRuleWalletInfo(CryptoWalletType.UNKNOWN);
        }
        CryptoWalletType walletType = mapToCryptoWalletType(ownershipInformation.getAddressType());
        return new TravelRuleWalletInfo(walletType, ownershipInformation.getOwnerVaspDid());
    }

    private CryptoWalletType mapToCryptoWalletType(NotabeneCryptoAddressType cryptoAddressType) {
        if (cryptoAddressType == null) {
            return CryptoWalletType.UNKNOWN;
        }
        return switch (cryptoAddressType) {
            case HOSTED -> CryptoWalletType.CUSTODIAL;
            case UNHOSTED -> CryptoWalletType.UNHOSTED;
            case UNKNOWN -> CryptoWalletType.UNKNOWN;
        };
    }

    @Getter
    private static class TravelRuleWalletInfo implements ITravelRuleWalletInfo {
        private final CryptoWalletType cryptoWalletType;
        private final String ownerVaspDid;

        public TravelRuleWalletInfo(CryptoWalletType cryptoWalletType) {
            this.cryptoWalletType = cryptoWalletType;
            this.ownerVaspDid = null;
        }

        public TravelRuleWalletInfo(CryptoWalletType cryptoWalletType, String ownerVaspDid) {
            this.cryptoWalletType = cryptoWalletType;
            this.ownerVaspDid = ownerVaspDid;
        }
    }

}
