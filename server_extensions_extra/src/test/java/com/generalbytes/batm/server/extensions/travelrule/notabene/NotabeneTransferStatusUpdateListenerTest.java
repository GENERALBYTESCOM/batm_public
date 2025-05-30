package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleIncomingTransferEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNameIdentifier;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNameIdentifierType;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransactionBlockchainInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotabeneTransferStatusUpdateListenerTest {

    @Mock
    private ITravelRuleTransferListener travelRuleTransferListener;
    @Mock
    private NotabeneIncomingTransferService incomingTransferService;
    @Mock
    private ITravelRuleProviderCredentials credentials;
    @InjectMocks
    private NotabeneTransferStatusUpdateListener listener;

    private static Stream<Arguments> provideValidStatuses() {
        return Stream.of(
            Arguments.arguments(NotabeneTransferStatus.NEW, TravelRuleProviderTransferStatus.IN_PROGRESS),
            Arguments.arguments(NotabeneTransferStatus.MISSING_BENEFICIARY_DATA, TravelRuleProviderTransferStatus.IN_PROGRESS),
            Arguments.arguments(NotabeneTransferStatus.WAITING_FOR_INFORMATION, TravelRuleProviderTransferStatus.IN_PROGRESS),
            Arguments.arguments(NotabeneTransferStatus.CANCELLED, TravelRuleProviderTransferStatus.REJECTED),
            Arguments.arguments(NotabeneTransferStatus.INCOMPLETE, TravelRuleProviderTransferStatus.IN_PROGRESS),
            Arguments.arguments(NotabeneTransferStatus.SENT, TravelRuleProviderTransferStatus.IN_PROGRESS),
            Arguments.arguments(NotabeneTransferStatus.ACK, TravelRuleProviderTransferStatus.IN_PROGRESS),
            Arguments.arguments(NotabeneTransferStatus.ACCEPTED, TravelRuleProviderTransferStatus.APPROVED),
            Arguments.arguments(NotabeneTransferStatus.DECLINED, TravelRuleProviderTransferStatus.REJECTED),
            Arguments.arguments(NotabeneTransferStatus.REJECTED, TravelRuleProviderTransferStatus.REJECTED),
            Arguments.arguments(NotabeneTransferStatus.NOT_READY, TravelRuleProviderTransferStatus.REJECTED),
            Arguments.arguments(NotabeneTransferStatus.SAVED, TravelRuleProviderTransferStatus.APPROVED)
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidStatuses")
    void testOnTransferUpdate_completed(NotabeneTransferStatus notabeneTransferStatus, TravelRuleProviderTransferStatus expectedTransferStatus) {
        String transferId = UUID.randomUUID().toString();
        NotabeneTransferInfo updatedTransferInfo = new NotabeneTransferInfo();
        updatedTransferInfo.setTransactionRef(transferId);
        updatedTransferInfo.setStatus(notabeneTransferStatus);
        when(credentials.getVaspDid()).thenReturn("vaspDid");
        listener.onTransferUpdate(updatedTransferInfo);

        verify(travelRuleTransferListener, times(1)).onTransferStatusUpdate(argThat(event -> {
            assertEquals(transferId, event.getTransferPublicId());
            assertEquals(expectedTransferStatus, event.getNewTransferStatus());
            return true;
        }));
    }

    @ParameterizedTest
    @EnumSource(value = NotabeneTransferStatus.class, names = {"ACK", "SENT"}, mode = EnumSource.Mode.INCLUDE)
    void testIncomingTransfer(NotabeneTransferStatus transferStatus) {
        when(credentials.getVaspDid()).thenReturn("beneficiaryVaspDid");
        NotabeneTransferInfo transferInfo = createNotabeneTransferInfo(transferStatus);
        NotabeneTransferInfo processedInfo = createNotabeneTransferInfo(NotabeneTransferStatus.ACK);

        when(incomingTransferService.processIncomingTransfer(credentials, transferInfo)).thenReturn(processedInfo);

        NotabeneIncomingTransferService.PersonNameIdentifiers personNameIdentifiers
            = new NotabeneIncomingTransferService.PersonNameIdentifiers(
            Optional.of(createNameIdentifier("originator")), Optional.of(createNameIdentifier("beneficiary"))
        );
        when(incomingTransferService.getPersonIdentifiers(credentials, processedInfo.getId())).thenReturn(personNameIdentifiers);

        listener.onTransferUpdate(transferInfo);

        verify(incomingTransferService).processIncomingTransfer(credentials, transferInfo);
        ArgumentCaptor<ITravelRuleIncomingTransferEvent> eventCaptor = ArgumentCaptor.forClass(ITravelRuleIncomingTransferEvent.class);
        verify(travelRuleTransferListener).onIncomingTransferReceived(eventCaptor.capture());

        ITravelRuleIncomingTransferEvent event = eventCaptor.getValue();
        assertNotNull(event);
        assertEquals(processedInfo.getId(), event.getId());
        assertEquals("beneficiaryVaspDid", event.getBeneficiaryVaspDid());
        assertEquals("originatorVaspDid", event.getOriginatorVasp().getDid());
        assertEquals("destinationAddress", event.getDestinationAddress());
        assertNull(event.getOriginatorVasp().getName());

        assertEquals("originatorPrimaryIdentifier", event.getOriginatorName().getPrimaryName());
        assertEquals("originatorSecondaryIdentifier", event.getOriginatorName().getSecondaryName());
        assertEquals("LEGL", event.getOriginatorName().getNameType());

        assertEquals("beneficiaryPrimaryIdentifier", event.getBeneficiaryName().getPrimaryName());
        assertEquals("beneficiarySecondaryIdentifier", event.getBeneficiaryName().getSecondaryName());
        assertEquals("LEGL", event.getBeneficiaryName().getNameType());

        assertEquals("NotabeneTransferInfo(id=someTransferId, transactionRef=someTransactionRef, status=ACK, " +
            "transactionType=TRAVELRULE, transactionAsset=BTC, transactionAmount=100000, chargedQuantity=100, " +
            "originatorDid=originatorDid, beneficiaryDid=beneficiaryDid, originatorVaspDid=originatorVaspDid, " +
            "beneficiaryVaspDid=beneficiaryVaspDid, transactionBlockchainInfo=NotabeneTransactionBlockchainInfo(" +
            "txHash=transactionHash, origin=origin, destination=destinationAddress)), " +
            "OriginatorName(primaryName=originatorPrimaryIdentifier, secondaryName=originatorSecondaryIdentifier, nameType=LEGL), " +
            "BeneficiaryName(primaryName=beneficiaryPrimaryIdentifier, secondaryName=beneficiarySecondaryIdentifier, nameType=LEGL)",
            event.getRawData());
    }

    @ParameterizedTest
    @EnumSource(value = NotabeneTransferStatus.class, names = "ACK", mode = EnumSource.Mode.EXCLUDE)
    @NullSource
    void testIncomingTransfer_notAck(NotabeneTransferStatus transferStatus) {
        when(credentials.getVaspDid()).thenReturn("beneficiaryVaspDid");
        NotabeneTransferInfo transferInfo = createNotabeneTransferInfo(NotabeneTransferStatus.SENT);
        NotabeneTransferInfo processedInfo = createNotabeneTransferInfo(transferStatus);

        when(incomingTransferService.processIncomingTransfer(credentials, transferInfo)).thenReturn(processedInfo);

        listener.onTransferUpdate(transferInfo);

        verify(incomingTransferService).processIncomingTransfer(credentials, transferInfo);
        verifyNoInteractions(travelRuleTransferListener);
    }

    @ParameterizedTest
    @EnumSource(value = NotabeneTransferStatus.class, names = {"ACK", "SENT"}, mode = EnumSource.Mode.EXCLUDE)
    void testIncomingTransfer_notInAllowedState(NotabeneTransferStatus transferStatus) {
        when(credentials.getVaspDid()).thenReturn("beneficiaryVaspDid");
        NotabeneTransferInfo transferInfo = createNotabeneTransferInfo(transferStatus);

        listener.onTransferUpdate(transferInfo);

        verifyNoInteractions(incomingTransferService);
        verifyNoInteractions(travelRuleTransferListener);
    }

    private static NotabeneTransferInfo createNotabeneTransferInfo(NotabeneTransferStatus status) {
        if (status == null) {
            return null;
        }
        NotabeneTransferInfo transferInfo = new NotabeneTransferInfo();
        transferInfo.setId("someTransferId");
        transferInfo.setTransactionRef("someTransactionRef");
        transferInfo.setBeneficiaryVaspDid("beneficiaryVaspDid");
        transferInfo.setOriginatorVaspDid("originatorVaspDid");
        transferInfo.setStatus(status);
        transferInfo.setTransactionType(NotabeneTransferType.TRAVELRULE);

        transferInfo.setTransactionAsset("BTC");
        transferInfo.setTransactionAmount("100000");
        transferInfo.setChargedQuantity(100);
        transferInfo.setOriginatorDid("originatorDid");
        transferInfo.setBeneficiaryDid("beneficiaryDid");

        NotabeneTransactionBlockchainInfo blockchainInfo = new NotabeneTransactionBlockchainInfo();
        blockchainInfo.setDestination("destinationAddress");
        blockchainInfo.setTxHash("transactionHash");
        blockchainInfo.setOrigin("origin");
        transferInfo.setTransactionBlockchainInfo(blockchainInfo);

        return transferInfo;
    }

    private NotabeneNameIdentifier createNameIdentifier(String prefix) {
        NotabeneNameIdentifier identifier = new NotabeneNameIdentifier();
        identifier.setPrimaryIdentifier(prefix + "PrimaryIdentifier");
        identifier.setSecondaryIdentifier(prefix + "SecondaryIdentifier");
        identifier.setNameIdentifierType(NotabeneNameIdentifierType.LEGL);
        return identifier;
    }
}