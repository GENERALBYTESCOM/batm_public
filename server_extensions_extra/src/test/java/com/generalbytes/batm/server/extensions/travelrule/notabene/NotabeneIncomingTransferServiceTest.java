package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneBeneficiary;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneIvms;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNameIdentifier;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabenePerson;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabenePersonName;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransactionBlockchainInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfoWithIvms;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

        NotabeneTransferInfo serviceResponse = mock(NotabeneTransferInfo.class);
        when(notabeneService.rejectTransfer(credentials, transferInfo.getId())).thenReturn(serviceResponse);

        NotabeneTransferInfo processed = incomingTransferService.processIncomingTransfer(credentials, transferInfo);

        assertEquals(serviceResponse, processed);
        verify(notabeneService).rejectTransfer(credentials, transferInfo.getId());
        verify(notabeneService, never()).confirmTransfer(any(), any());
    }

    @Test
    void processIncomingTransfer_sent_knownAddress() {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);

        NotabeneTransferInfo transferInfo = createTransferInfo(NotabeneTransferStatus.SENT);
        NotabeneTransactionBlockchainInfo blockchainInfo = new NotabeneTransactionBlockchainInfo();
        blockchainInfo.setDestination("address");
        transferInfo.setTransactionBlockchainInfo(blockchainInfo);

        NotabeneTransferInfo serviceResponse = mock(NotabeneTransferInfo.class);
        when(notabeneService.confirmTransfer(credentials, transferInfo.getId())).thenReturn(serviceResponse);

        NotabeneTransferInfo processed = incomingTransferService.processIncomingTransfer(credentials, transferInfo);

        assertEquals(serviceResponse, processed);
        verify(notabeneService).confirmTransfer(credentials, transferInfo.getId());
        verify(notabeneService, never()).rejectTransfer(any(), any());
    }

    private NotabeneTransferInfo createTransferInfo(NotabeneTransferStatus status) {
        NotabeneTransferInfo transferInfo = new NotabeneTransferInfo();
        transferInfo.setId(UUID.randomUUID().toString());
        transferInfo.setStatus(status);
        return transferInfo;
    }


    @Test
    void testGetBeneficiaryIdentifier() {
        NotabeneNameIdentifier expectedIdentifier = new NotabeneNameIdentifier();
        NotabeneTransferInfoWithIvms transferInfo = createTransferInfoWithIvmsWithPersonName(List.of(expectedIdentifier));

        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        String transferId = UUID.randomUUID().toString();
        when(notabeneService.getTransferInfo(credentials, transferId)).thenReturn(transferInfo);

        Optional<NotabeneNameIdentifier> result = incomingTransferService.getBeneficiaryIdentifier(credentials, transferId);

        assertTrue(result.isPresent());
        NotabeneNameIdentifier resulIdentifier = result.get();
        assertEquals(expectedIdentifier, resulIdentifier);

        verify(notabeneService).getTransferInfo(credentials, transferId);
    }

    static Object[][] testBeneficiaryIdentifierNullSource() {
        return new Object[][]{
            {null},
            {new NotabeneTransferInfoWithIvms()},
            {createTransferInfoWithIvms(null)},
            {createTransferInfoWithIvms(new NotabeneBeneficiary())},
            {createTransferInfoWithIvms(createNotabeneBeneficiary(List.of()))},
            {createTransferInfoWithIvmsWithNaturalPerson(null)},
            {createTransferInfoWithIvmsWithNaturalPerson(new NotabeneNaturalPerson())},
            {createTransferInfoWithIvmsWithNaturalPerson(createNotabeneNaturalPerson(List.of()))},
            {createTransferInfoWithIvmsWithPersonName(List.of())},
        };
    }

    @ParameterizedTest
    @MethodSource("testBeneficiaryIdentifierNullSource")
    void getBeneficiaryIdentifier_nullScenarios(NotabeneTransferInfoWithIvms transferInfo) {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        String transferId = UUID.randomUUID().toString();
        when(notabeneService.getTransferInfo(credentials, transferId)).thenReturn(transferInfo);

        Optional<NotabeneNameIdentifier> result = incomingTransferService.getBeneficiaryIdentifier(credentials, transferId);

        assertEquals(Optional.empty(), result);
        verify(notabeneService).getTransferInfo(credentials, transferId);
    }

    private static NotabeneTransferInfoWithIvms createTransferInfoWithIvms(NotabeneBeneficiary beneficiary) {
        NotabeneTransferInfoWithIvms transfer = new NotabeneTransferInfoWithIvms();
        NotabeneIvms ivms = new NotabeneIvms();
        ivms.setBeneficiary(beneficiary);
        transfer.setIvms101(ivms);
        return transfer;
    }

    private static NotabeneTransferInfoWithIvms createTransferInfoWithIvmsWithNaturalPerson(NotabeneNaturalPerson naturalPerson) {
        NotabenePerson person = new NotabenePerson();
        person.setNaturalPerson(naturalPerson);
        NotabeneBeneficiary beneficiary = createNotabeneBeneficiary(List.of(person));
        return createTransferInfoWithIvms(beneficiary);
    }

    private static NotabeneTransferInfoWithIvms createTransferInfoWithIvmsWithPersonName(List<NotabeneNameIdentifier> identifiers) {
        NotabenePersonName personName = new NotabenePersonName();
        personName.setNameIdentifier(identifiers);
        NotabeneNaturalPerson naturalPerson = createNotabeneNaturalPerson(List.of(personName));
        return createTransferInfoWithIvmsWithNaturalPerson(naturalPerson);
    }

    private static NotabeneBeneficiary createNotabeneBeneficiary(List<NotabenePerson> persons) {
        NotabeneBeneficiary beneficiary = new NotabeneBeneficiary();
        beneficiary.setBeneficiaryPersons(persons);
        return beneficiary;
    }

    private static NotabeneNaturalPerson createNotabeneNaturalPerson(List<NotabenePersonName> personNames) {
        NotabeneNaturalPerson naturalPerson = new NotabeneNaturalPerson();
        naturalPerson.setName(personNames);
        return naturalPerson;
    }
}