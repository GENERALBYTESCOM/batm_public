package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotabeneTransferPublisherTest {

    private NotabeneTransferPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = NotabeneTransferPublisher.getInstance();
    }

    @Test
    void testPublishEvent() {
        NotabeneTransferUpdateListener listener = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferInfo transferInfo = createTransferInfo("originatorDid", "beneficiaryDid");
        publisher.registerListener("originatorDid", listener);

        publisher.publishEvent(transferInfo);

        verify(listener, times(1)).onTransferUpdate(transferInfo);
    }

    @Test
    void testPublishEvent_noListener() {
        NotabeneTransferInfo transferInfo = createTransferInfo("originatorDid", "beneficiaryDid");

        publisher.publishEvent(transferInfo);
    }

    @Test
    void testPublishEvent_noListenerForVasps() {
        NotabeneTransferUpdateListener listener = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferInfo transferInfo = createTransferInfo("originatorDid", "beneficiaryDid");
        publisher.registerListener("anotherDid", listener);

        publisher.publishEvent(transferInfo);

        verify(listener, never()).onTransferUpdate(transferInfo);
    }

    @Test
    void testPublishEvent_nullOriginatorDid() {
        NotabeneTransferUpdateListener listener = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferInfo transferInfo = createTransferInfo(null, "beneficiaryDid");
        publisher.registerListener("originatorDid", listener);

        publisher.publishEvent(transferInfo);

        verify(listener, never()).onTransferUpdate(transferInfo);
    }

    @Test
    void testPublishEvent_nullBeneficiaryDid() {
        NotabeneTransferUpdateListener listener = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferInfo transferInfo = createTransferInfo("originatorDid", null);
        publisher.registerListener("beneficiaryDid", listener);

        publisher.publishEvent(transferInfo);

        verify(listener, never()).onTransferUpdate(transferInfo);
    }

    @Test
    void testPublishEvent_equalOriginatorAndBeneficiary() {
        NotabeneTransferUpdateListener listener = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferInfo transferInfo = createTransferInfo("did", "did");
        publisher.registerListener("did", listener);

        publisher.publishEvent(transferInfo);

        verify(listener, times(1)).onTransferUpdate(transferInfo);
    }

    @Test
    void testPublishEvent_unregisterListener() {
        NotabeneTransferUpdateListener listener = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferInfo transferInfo = createTransferInfo("originatorDid", "beneficiaryDid");
        publisher.registerListener("originatorDid", listener);

        publisher.publishEvent(transferInfo);

        verify(listener, times(1)).onTransferUpdate(transferInfo);

        clearInvocations(listener);
        publisher.unregisterListener("originatorDid");

        // Event should not be sent to an unregistered listener
        publisher.publishEvent(transferInfo);

        verify(listener, never()).onTransferUpdate(transferInfo);
    }

    @Test
    void testPublishEvent_multipleListeners() {
        NotabeneTransferUpdateListener listener1 = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferUpdateListener listener2 = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferInfo transferInfo = createTransferInfo("originatorDid", "beneficiaryDid");

        publisher.registerListener("originatorDid", listener1);
        publisher.registerListener("beneficiaryDid", listener2);

        publisher.publishEvent(transferInfo);

        verify(listener1, times(1)).onTransferUpdate(transferInfo);
        verify(listener2, times(1)).onTransferUpdate(transferInfo);
    }

    @Test
    void testPublishEvent_duplicateListener() {
        NotabeneTransferUpdateListener listener = mock(NotabeneTransferUpdateListener.class);
        NotabeneTransferInfo transferInfo = createTransferInfo("originatorDid", "beneficiaryDid");

        publisher.registerListener("originatorDid", listener);
        publisher.registerListener("originatorDid", listener);

        publisher.publishEvent(transferInfo);

        verify(listener, times(1)).onTransferUpdate(transferInfo);
    }

    private NotabeneTransferInfo createTransferInfo(String originatorDid, String beneficiaryDid) {
        NotabeneTransferInfo transferInfo = new NotabeneTransferInfo();
        transferInfo.setOriginatorVaspDid(originatorDid);
        transferInfo.setBeneficiaryVaspDid(beneficiaryDid);
        return transferInfo;
    }

}