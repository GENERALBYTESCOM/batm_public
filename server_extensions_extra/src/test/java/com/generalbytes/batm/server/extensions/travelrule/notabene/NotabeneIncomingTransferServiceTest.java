package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransactionBlockchainInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotabeneIncomingTransferServiceTest {
    @Mock
    private NotabeneService notabeneService;
    @InjectMocks
    private NotabeneIncomingTransferService incomingTransferService;

    @Test
    void processIncomingTransfer_ack() {
        NotabeneTransferInfo transferInfo = createTransferInfo(NotabeneTransferStatus.ACK);
        NotabeneTransferInfo processed = incomingTransferService.processIncomingTransfer(null, transferInfo);

        assertSame(transferInfo, processed);
        verifyNoInteractions(notabeneService);
    }

    static Object[] testUnknownAddressSource() {
        return new Object[]{
            new Object[]{null},
            new Object[]{new NotabeneTransactionBlockchainInfo()},
        };
    }

    @ParameterizedTest
    @MethodSource("testUnknownAddressSource")
    void processIncomingTransfer_sent_unknownAddress(NotabeneTransactionBlockchainInfo blockchainInfo) {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);

        NotabeneTransferInfo transferInfo = createTransferInfo(NotabeneTransferStatus.SENT);
        transferInfo.setTransactionBlockchainInfo(blockchainInfo);

        NotabeneTransferInfo processed = incomingTransferService.processIncomingTransfer(credentials, transferInfo);

        assertNull(processed);
        verify(notabeneService).rejectTransfer(credentials, transferInfo.getId());
    }

    @Test
    void processIncomingTransfer_sent_knownAddress() {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);

        NotabeneTransferInfo transferInfo = createTransferInfo(NotabeneTransferStatus.SENT);
        NotabeneTransactionBlockchainInfo blockchainInfo = new NotabeneTransactionBlockchainInfo();
        blockchainInfo.setDestination("address");
        transferInfo.setTransactionBlockchainInfo(blockchainInfo);

        NotabeneTransferInfo processed = incomingTransferService.processIncomingTransfer(credentials, transferInfo);

        assertNull(processed);
        verify(notabeneService).confirmTransfer(credentials, transferInfo.getId());
    }

    private NotabeneTransferInfo createTransferInfo(NotabeneTransferStatus status) {
        NotabeneTransferInfo transferInfo = new NotabeneTransferInfo();
        transferInfo.setId(UUID.randomUUID().toString());
        transferInfo.setStatus(status);
        return transferInfo;
    }
}