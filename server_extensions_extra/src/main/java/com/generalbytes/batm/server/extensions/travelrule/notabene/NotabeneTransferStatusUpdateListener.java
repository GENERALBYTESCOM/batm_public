package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPersonName;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderIdentification;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferStatusUpdateEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNameIdentifier;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNameIdentifierType;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransactionBlockchainInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * This listener is responsible for mapping transfer status updates received from Notabene and sending them to a designated
 * {@link ITravelRuleTransferListener} for further processing on server side.
 */
@EqualsAndHashCode
@Slf4j
public class NotabeneTransferStatusUpdateListener implements NotabeneTransferUpdateListener {

    private static final Set<NotabeneTransferStatus> ALLOWED_INCOMING_STATUSES = Set.of(
        // SENT -> The beneficiary address must be confirmed/rejected before the transfer resolution -> ACK/REJECTED
        NotabeneTransferStatus.SENT,
        // ACK -> The transfer can now be resolved -> accepted/declined
        NotabeneTransferStatus.ACK
    );

    private final ITravelRuleTransferListener transferHandler;
    private final NotabeneIncomingTransferService incomingTransferService;
    private final ITravelRuleProviderCredentials credentials;

    public NotabeneTransferStatusUpdateListener(ITravelRuleTransferListener transferHandler,
                                                NotabeneIncomingTransferService incomingTransferService,
                                                ITravelRuleProviderCredentials credentials) {
        this.transferHandler = transferHandler;
        this.incomingTransferService = incomingTransferService;
        this.credentials = credentials;
    }

    @Override
    public void onTransferUpdate(NotabeneTransferInfo updatedTransferInfo) {
        if (isIncomingTransfer(updatedTransferInfo)) {
            log.info("Received transfer status update for incoming transfer {}, status: {}", updatedTransferInfo.getId(), updatedTransferInfo.getStatus());
            if (ALLOWED_INCOMING_STATUSES.contains(updatedTransferInfo.getStatus())) {
                NotabeneTransferInfo notabeneTransferInfo = incomingTransferService.processIncomingTransfer(credentials, updatedTransferInfo);
                if (notabeneTransferInfo != null && notabeneTransferInfo.getStatus() == NotabeneTransferStatus.ACK) {
                    ITravelRuleIncomingTransferEvent event = mapToIncomingTransferEvent(notabeneTransferInfo);
                    transferHandler.onIncomingTransferReceived(event);
                }
            }
        } else {
            ITravelRuleTransferStatusUpdateEvent event = mapToTransferStatusUpdateEvent(updatedTransferInfo);
            transferHandler.onTransferStatusUpdate(event);
        }
    }

    private boolean isIncomingTransfer(NotabeneTransferInfo transferInfo) {
        return credentials.getVaspDid().equals(transferInfo.getBeneficiaryVaspDid());
    }

    private ITravelRuleTransferStatusUpdateEvent mapToTransferStatusUpdateEvent(NotabeneTransferInfo updatedTransferInfo) {
        TravelRuleProviderTransferStatus status = mapToTravelRuleTransferStatus(updatedTransferInfo.getStatus());
        return new ITravelRuleTransferStatusUpdateEvent() {
            @Override
            public String getTransferPublicId() {
                return updatedTransferInfo.getTransactionRef();
            }

            @Override
            public TravelRuleProviderTransferStatus getNewTransferStatus() {
                return status;
            }
        };
    }

    private TravelRuleProviderTransferStatus mapToTravelRuleTransferStatus(NotabeneTransferStatus notabeneStatus) {
        return switch (notabeneStatus) {
            case ACCEPTED, SAVED -> TravelRuleProviderTransferStatus.APPROVED;
            case REJECTED, DECLINED, NOT_READY, CANCELLED -> TravelRuleProviderTransferStatus.REJECTED;
            default -> TravelRuleProviderTransferStatus.IN_PROGRESS;
        };
    }

    private ITravelRuleIncomingTransferEvent mapToIncomingTransferEvent(NotabeneTransferInfo transferInfo) {
        PersonNames personNames = getPersonNames(transferInfo.getId());
        return new ITravelRuleIncomingTransferEvent() {
            @Override
            public ITravelRuleProviderIdentification getTravelRuleProvider() {
                return null;
            }

            @Override
            public String getId() {
                return transferInfo.getId();
            }

            @Override
            public ITravelRuleVasp getOriginatorVasp() {
                return mapOriginatorVasp(transferInfo);
            }

            @Override
            public ITravelRuleNaturalPersonName getOriginatorName() {
                return personNames.originatorName();
            }

            @Override
            public ITravelRuleNaturalPersonName getBeneficiaryName() {
                return personNames.beneficiaryName();
            }

            @Override
            public String getDestinationAddress() {
                return mapDestinationAddress(transferInfo);
            }

            @Override
            public String getRawData() {
                return mapRawData(transferInfo, personNames);
            }
        };
    }

    private PersonNames getPersonNames(String transferId) {
        NotabeneIncomingTransferService.PersonNameIdentifiers personIdentifiers
            = incomingTransferService.getPersonIdentifiers(credentials, transferId);

        ITravelRuleNaturalPersonName originatorName = personIdentifiers.originatorIdentifier().map(mapIdentifierToName()).orElse(null);
        ITravelRuleNaturalPersonName beneficiaryName = personIdentifiers.beneficiaryIdentifier().map(mapIdentifierToName()).orElse(null);
        return new PersonNames(originatorName, beneficiaryName);
    }

    private Function<NotabeneNameIdentifier, ITravelRuleNaturalPersonName> mapIdentifierToName() {
        return notabeneNameIdentifier -> new ITravelRuleNaturalPersonName() {
            @Override
            public String getPrimaryName() {
                return notabeneNameIdentifier.getPrimaryIdentifier();
            }

            @Override
            public String getSecondaryName() {
                return notabeneNameIdentifier.getSecondaryIdentifier();
            }

            @Override
            public String getNameType() {
                NotabeneNameIdentifierType nameIdentifierType = notabeneNameIdentifier.getNameIdentifierType();
                return nameIdentifierType != null ? nameIdentifierType.name() : null;
            }
        };
    }

    private ITravelRuleVasp mapOriginatorVasp(NotabeneTransferInfo transferInfo) {
        return new ITravelRuleVasp() {
            @Override
            public String getDid() {
                return transferInfo.getOriginatorVaspDid();
            }

            @Override
            public String getName() {
                return null;
            }
        };
    }

    private String mapDestinationAddress(NotabeneTransferInfo transferInfo) {
        return Optional.ofNullable(transferInfo.getTransactionBlockchainInfo())
            .map(NotabeneTransactionBlockchainInfo::getDestination)
            .orElse(null);
    }

    private String mapRawData(NotabeneTransferInfo transferInfo, PersonNames personNames) {
        StringBuilder rawDataContent = new StringBuilder(transferInfo.toString());
        appendName(rawDataContent, personNames.originatorName(), ", OriginatorName(");
        appendName(rawDataContent, personNames.beneficiaryName(), ", BeneficiaryName(");
        return rawDataContent.toString();
    }

    private static void appendName(StringBuilder rawDataContent,
                                   ITravelRuleNaturalPersonName personName,
                                   String nameIdentifier) {
        if (personName != null) {
            rawDataContent.append(nameIdentifier)
                .append("primaryName=").append(personName.getPrimaryName()).append(", ")
                .append("secondaryName=").append(personName.getSecondaryName()).append(", ")
                .append("nameType=").append(personName.getNameType())
                .append(")");
        }
    }

    private record PersonNames(ITravelRuleNaturalPersonName originatorName,
                               ITravelRuleNaturalPersonName beneficiaryName) {
    }
}