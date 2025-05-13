package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

        listener.onTransferUpdate(transferInfo);

        verify(incomingTransferService).processIncomingTransfer(credentials, transferInfo);
        verify(travelRuleTransferListener).onIncomingTransferReceived(any()); //TODO: BATM-7383 eq when event mapping is implemented
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
        NotabeneTransferInfo updatedTransferInfo = new NotabeneTransferInfo();
        updatedTransferInfo.setBeneficiaryVaspDid("beneficiaryVaspDid");
        updatedTransferInfo.setStatus(status);
        return updatedTransferInfo;
    }
}