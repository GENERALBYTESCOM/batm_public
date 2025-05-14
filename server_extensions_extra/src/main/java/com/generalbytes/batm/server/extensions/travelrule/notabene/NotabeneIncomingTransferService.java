package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneBeneficiary;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneIvms;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNameIdentifier;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneOriginator;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabenePerson;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabenePersonName;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransactionBlockchainInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfoWithIvms;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
public class NotabeneIncomingTransferService {

    private final NotabeneService notabeneService;

    /**
     * Processes an incoming transfer by verifying its status and performing appropriate actions to handle it.
     * <p>
     * If the transfer has already been acknowledged, it is returned as is.
     * Otherwise, further processing is performed to confirm or reject the transfer based on its details.
     *
     * @param credentials  the credentials required to authenticate with the travel rule provider
     * @param transferInfo the information about the incoming transfer to be processed
     * @return the updated transfer information (for ACK original input), returns {@code null} in case of failure
     */
    public NotabeneTransferInfo processIncomingTransfer(ITravelRuleProviderCredentials credentials,
                                                        NotabeneTransferInfo transferInfo) {
        log.debug("Processing incoming transfer {} from Notabene", transferInfo.getId());
        if (isAcknowledgedTransfer(transferInfo)) {
            log.debug("Transfer {} has already been acknowledged", transferInfo.getId());
            return transferInfo;
        } else {
            return handleSentTransfer(credentials, transferInfo);
        }
    }

    private boolean isAcknowledgedTransfer(NotabeneTransferInfo transferInfo) {
        return transferInfo.getStatus() == NotabeneTransferStatus.ACK;
    }

    private boolean isBeneficiaryAddressKnown(NotabeneTransferInfo transferInfo) {
        NotabeneTransactionBlockchainInfo transactionBlockchainInfo = transferInfo.getTransactionBlockchainInfo();
        if (transactionBlockchainInfo == null) {
            return false;
        }
        String destinationAddress = transactionBlockchainInfo.getDestination();
        if (destinationAddress == null) {
            return false;
        }
        //TODO: BATM-7383 call extensions to validate address
        return true;
    }

    private NotabeneTransferInfo handleSentTransfer(ITravelRuleProviderCredentials credentials,
                                                    NotabeneTransferInfo transferInfo) {
        if (isBeneficiaryAddressKnown(transferInfo)) {
            log.debug("Transfer {} has been sent to a known beneficiary address -> confirming", transferInfo.getId());
            return notabeneService.confirmTransfer(credentials, transferInfo.getId());
        } else {
            log.debug("Transfer {} has been sent to an unknown beneficiary address -> rejecting", transferInfo.getId());
            return notabeneService.rejectTransfer(credentials, transferInfo.getId());
        }
    }

    /**
     * Retrieves the originator and beneficiary name identifiers for a transfer based on the provided credentials and transfer ID.
     *
     * @param credentials the credentials required to authenticate with the travel rule provider
     * @param transferId  the unique identifier of the transfer to retrieve the beneficiary identifier for
     * @return wrapper containing name identifiers of the beneficiary and originator
     */
    public PersonNameIdentifiers getPersonIdentifiers(ITravelRuleProviderCredentials credentials,
                                                      String transferId) {
        NotabeneTransferInfoWithIvms transferInfo = notabeneService.getTransferInfo(credentials, transferId);
        Optional<NotabeneNameIdentifier> beneficiaryIdentifier = getBeneficiaryNameIdentifier(transferInfo);
        Optional<NotabeneNameIdentifier> originatorIdentifier = getOriginatorNameIdentifier(transferInfo);
        return new PersonNameIdentifiers(originatorIdentifier, beneficiaryIdentifier);
    }

    private Optional<NotabeneNameIdentifier> getBeneficiaryNameIdentifier(NotabeneTransferInfoWithIvms transferInfo) {
        return getNameIdentifier(transferInfo, NotabeneIvms::getBeneficiary, NotabeneBeneficiary::getBeneficiaryPersons);
    }

    private Optional<NotabeneNameIdentifier> getOriginatorNameIdentifier(NotabeneTransferInfoWithIvms transferInfo) {
        return getNameIdentifier(transferInfo, NotabeneIvms::getOriginator, NotabeneOriginator::getOriginatorPersons);
    }

    private <T> Optional<NotabeneNameIdentifier> getNameIdentifier(NotabeneTransferInfoWithIvms transferInfo,
                                                                   Function<NotabeneIvms, T> getCounterParty,
                                                                   Function<T, List<NotabenePerson>> getCounterPartyPersons) {
        return Optional.ofNullable(transferInfo)
            .map(NotabeneTransferInfoWithIvms::getIvms101)
            .map(getCounterParty)
            .map(getCounterPartyPersons)
            .map(getFirstElementFromList())
            .map(NotabenePerson::getNaturalPerson)
            .map(NotabeneNaturalPerson::getName)
            .map(getFirstElementFromList())
            .map(NotabenePersonName::getNameIdentifier)
            .map(getFirstElementFromList());
    }

    private <T> Function<List<T>, T> getFirstElementFromList() {
        return list -> !list.isEmpty() ? list.get(0) : null;
    }

    public record PersonNameIdentifiers(Optional<NotabeneNameIdentifier> originatorIdentifier,
                                        Optional<NotabeneNameIdentifier> beneficiaryIdentifier
    ) {
    }

}
