package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateListener;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotabeneTransferStatusUpdateListenerTest {

    @Mock
    private ITravelRuleTransferUpdateListener travelRuleTransferUpdateListener;

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

        listener.onTransferUpdate(updatedTransferInfo);

        verify(travelRuleTransferUpdateListener, times(1)).onTransferStatusUpdate(argThat(event -> {
            assertEquals(transferId, event.getTransferPublicId());
            assertEquals(expectedTransferStatus, event.getNewTransferStatus());
            return true;
        }));
    }

}