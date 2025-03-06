package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.CryptoWalletType;
import com.generalbytes.batm.server.extensions.travelrule.IIdentityWalletEvaluationRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPerson;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleNaturalPersonName;
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
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneOriginator;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabenePerson;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferCreateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneVaspInfoSimple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotabeneTravelRuleProviderTest {

    @Mock
    private NotabeneConfiguration configuration;
    @Mock
    private NotabeneAuthService notabeneAuthService;
    @Mock
    private NotabeneService notabeneService;
    @Mock
    private NotabeneTransferPublisher notabeneTransferPublisher;

    private ITravelRuleProviderCredentials credentials;
    private NotabeneTravelRuleProvider provider;

    @BeforeEach
    void setUp() {
        credentials = createITravelRuleProviderCredentials();
        provider = new NotabeneTravelRuleProvider(credentials, configuration, notabeneAuthService, notabeneService, notabeneTransferPublisher);
    }

    @Test
    void testGetName() {
        assertEquals("Notabene Travel Rule Provider", provider.getName());
    }

    private static Stream<Arguments> provideTestGetWalletInfo() {
        return Stream.of(
            Arguments.arguments(NotabeneCryptoAddressType.HOSTED, "ownerVaspDid", CryptoWalletType.CUSTODIAL),
            Arguments.arguments(NotabeneCryptoAddressType.UNHOSTED, null, CryptoWalletType.UNHOSTED),
            Arguments.arguments(NotabeneCryptoAddressType.UNKNOWN, null, CryptoWalletType.UNKNOWN),
            Arguments.arguments(null, null, CryptoWalletType.UNKNOWN)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestGetWalletInfo")
    void testGetWalletInfo(NotabeneCryptoAddressType cryptoAddressType, String ownerVaspDid, CryptoWalletType expectedWalletType) {
        IIdentityWalletEvaluationRequest walletContext = createWalletContext();
        NotabeneAddressOwnershipInfoResponse response = new NotabeneAddressOwnershipInfoResponse();
        response.setAddressType(cryptoAddressType);
        response.setOwnerVaspDid(ownerVaspDid);

        when(notabeneService.getAddressOwnershipInformation(any(), any())).thenReturn(response);

        ITravelRuleWalletInfo walletInfo = provider.getWalletInfo(walletContext);

        assertNotNull(walletInfo);
        assertEquals(expectedWalletType, walletInfo.getCryptoWalletType());
        assertEquals(ownerVaspDid, walletInfo.getOwnerVaspDid());
        verifyGetAddressOwnershipCalled(walletContext);
    }

    @Test
    void testGetWalletInfo_nullAddressOwnershipInfo() {
        IIdentityWalletEvaluationRequest walletContext = createWalletContext();

        when(notabeneService.getAddressOwnershipInformation(any(), any())).thenReturn(null);

        ITravelRuleWalletInfo walletInfo = provider.getWalletInfo(walletContext);

        assertNotNull(walletInfo);
        assertEquals(CryptoWalletType.UNKNOWN, walletInfo.getCryptoWalletType());
        assertNull(walletInfo.getOwnerVaspDid());
    }

    private static Stream<Arguments> provideTestGetAllVaspsValid() {
        return Stream.of(
            Arguments.arguments(List.of(
                createVaspInfoSimple(1),
                createVaspInfoSimple(2),
                createVaspInfoSimple(3)
            ), new int[]{1, 2, 3}),
            Arguments.arguments(List.of(), new int[0])
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestGetAllVaspsValid")
    void testGetAllVasps_valid(List<NotabeneVaspInfoSimple> notabeneVasps, int[] expectedVasps) {
        when(notabeneService.getAllVasps(any())).thenReturn(notabeneVasps);

        List<ITravelRuleVasp> result = provider.getAllVasps();
        assertNotNull(result);
        assertEquals(notabeneVasps.size(), result.size());
        for (int i = 0; i < expectedVasps.length; i++) {
            ITravelRuleVasp vasp = result.get(i);
            assertVasp(expectedVasps[i], vasp);
        }
    }

    @Test
    void testCreateTransfer_valid() {
        ITravelRuleTransferData outgoingTransferData = createTravelRuleMessageData();
        NotabeneTransferInfo notabeneTransferInfo = createNotabeneTransferInfo(NotabeneTransferStatus.NEW);

        when(configuration.isAutomaticApprovalOfOutgoingTransfersEnabled()).thenReturn(false);
        when(notabeneService.createTransfer(any(), any())).thenReturn(notabeneTransferInfo);

        ITravelRuleTransferInfo result = provider.createTransfer(outgoingTransferData);

        assertNotNull(result);
        assertEquals("id", result.getId());
        verify(notabeneService).createTransfer(eq(credentials), argThat(request -> {
            assertCreateRequest(outgoingTransferData, request);
            return true;
        }));
    }

    @Test
    void testCreateTransfer_exception() {
        ITravelRuleTransferData outgoingTransferData = createTravelRuleMessageData();

        when(notabeneService.createTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> provider.createTransfer(outgoingTransferData));

        assertEquals("Test Exception", exception.getMessage());
        verify(notabeneService).createTransfer(eq(credentials), argThat(request -> {
            assertCreateRequest(outgoingTransferData, request);
            return true;
        }));
    }

    @Test
    void testCreateTransfer_automaticApproval() {
        ITravelRuleTransferData outgoingTransferData = createTravelRuleMessageData();
        NotabeneTransferInfo notabeneTransferInfo = createNotabeneTransferInfo(NotabeneTransferStatus.NEW);

        when(configuration.isAutomaticApprovalOfOutgoingTransfersEnabled()).thenReturn(true);
        when(notabeneService.createTransfer(any(), any())).thenReturn(notabeneTransferInfo);
        when(notabeneService.approveTransfer(any(), any())).thenAnswer(invocation -> {
            notabeneTransferInfo.setStatus(NotabeneTransferStatus.SENT);
            return notabeneTransferInfo;
        });

        ITravelRuleTransferInfo result = provider.createTransfer(outgoingTransferData);

        assertNotNull(result);
        assertEquals("id", result.getId());
        verify(notabeneService, times(1)).approveTransfer(credentials, "id");
    }

    @Test
    void testCreateTransfer_automaticApprovalSkip() {
        ITravelRuleTransferData outgoingTransferData = createTravelRuleMessageData();
        NotabeneTransferInfo notabeneTransferInfo = createNotabeneTransferInfo(NotabeneTransferStatus.SENT);

        when(configuration.isAutomaticApprovalOfOutgoingTransfersEnabled()).thenReturn(true);
        when(notabeneService.createTransfer(any(), any())).thenReturn(notabeneTransferInfo);

        ITravelRuleTransferInfo result = provider.createTransfer(outgoingTransferData);

        assertNotNull(result);
        assertEquals("id", result.getId());
        verify(notabeneService, never()).approveTransfer(any(), any());
    }

    @Test
    void testCreateTransfer_automaticApprovalFail() {
        ITravelRuleTransferData outgoingTransferData = createTravelRuleMessageData();
        NotabeneTransferInfo notabeneTransferInfo = createNotabeneTransferInfo(NotabeneTransferStatus.NEW);

        when(configuration.isAutomaticApprovalOfOutgoingTransfersEnabled()).thenReturn(true);
        when(notabeneService.createTransfer(any(), any())).thenReturn(notabeneTransferInfo);
        when(notabeneService.approveTransfer(any(), any())).thenReturn(null);

        ITravelRuleTransferInfo result = provider.createTransfer(outgoingTransferData);

        assertNotNull(result);
        assertEquals("id", result.getId());
        verify(notabeneService, times(1)).approveTransfer(credentials, "id");
    }

    @Test
    void testCreateTransfer_automaticApprovalException() {
        ITravelRuleTransferData outgoingTransferData = createTravelRuleMessageData();
        NotabeneTransferInfo notabeneTransferInfo = createNotabeneTransferInfo(NotabeneTransferStatus.NEW);

        when(configuration.isAutomaticApprovalOfOutgoingTransfersEnabled()).thenReturn(true);
        when(notabeneService.createTransfer(any(), any())).thenReturn(notabeneTransferInfo);
        when(notabeneService.approveTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> provider.createTransfer(outgoingTransferData));

        assertEquals("Test Exception", exception.getMessage());
        verify(notabeneService, times(1)).approveTransfer(credentials, "id");
    }

    @Test
    void testCreateTransfer_fail() {
        ITravelRuleTransferData outgoingTransferData = createTravelRuleMessageData();

        when(notabeneService.createTransfer(any(), any())).thenReturn(null);

        ITravelRuleTransferInfo result = provider.createTransfer(outgoingTransferData);

        assertNull(result);
        verify(notabeneService).createTransfer(eq(credentials), argThat(request -> {
            assertCreateRequest(outgoingTransferData, request);
            return true;
        }));
    }

    @Test
    void testRegisterListener_valid() {
        ITravelRuleTransferUpdateListener listener = mock(ITravelRuleTransferUpdateListener.class);
        when(notabeneService.registerWebhook(any())).thenReturn(true);

        try (MockedConstruction<NotabeneTransferStatusUpdateListener> listenerMockedConstruction = mockNotabeneListenerConstruction(listener)) {
            provider.registerStatusUpdateListener(listener);

            assertEquals(1, listenerMockedConstruction.constructed().size());
            NotabeneTransferStatusUpdateListener notabeneListener = listenerMockedConstruction.constructed().get(0);
            verify(notabeneTransferPublisher, times(1)).registerListener("vaspDid", notabeneListener);
        }
    }

    @Test
    void testRegisterListener_fail() {
        ITravelRuleTransferUpdateListener listener = mock(ITravelRuleTransferUpdateListener.class);
        when(notabeneService.registerWebhook(any())).thenReturn(false);

        try (MockedConstruction<NotabeneTransferStatusUpdateListener> listenerMockedConstruction = mockNotabeneListenerConstruction(listener)) {
            provider.registerStatusUpdateListener(listener);

            assertEquals(0, listenerMockedConstruction.constructed().size());
            verify(notabeneTransferPublisher, never()).registerListener(any(), any());
        }
    }

    @Test
    void testUnregisterListener_valid() {
        provider.unregisterStatusUpdateListener();

        verify(notabeneTransferPublisher, times(1)).unregisterListener("vaspDid");
    }

    @Test
    void testUpdateTransfer_nullResult() {
        ITravelRuleTransferUpdateRequest request = mock(ITravelRuleTransferUpdateRequest.class);
        when(request.getId()).thenReturn("publicId");
        when(request.getTransactionHash()).thenReturn("transactionHash");

        when(notabeneService.updateTransfer(any(), any())).thenReturn(null);

        ITravelRuleTransferInfo result = provider.updateTransfer(request);

        assertNull(result);
        verify(notabeneService, times(1)).updateTransfer(eq(credentials), argThat(notabeneRequest -> {
            assertEquals("publicId", notabeneRequest.getId());
            assertEquals("transactionHash", notabeneRequest.getTxHash());
            return true;
        }));
    }

    @Test
    void testUpdateTransfer_valid() {
        ITravelRuleTransferUpdateRequest request = mock(ITravelRuleTransferUpdateRequest.class);
        when(request.getId()).thenReturn("publicId");
        when(request.getTransactionHash()).thenReturn("transactionHash");
        NotabeneTransferInfo notabeneTransferInfo = new NotabeneTransferInfo();
        notabeneTransferInfo.setId("id");

        when(notabeneService.updateTransfer(any(), any())).thenReturn(notabeneTransferInfo);

        ITravelRuleTransferInfo result = provider.updateTransfer(request);

        assertNotNull(result);
        assertEquals("id", result.getId());
        verify(notabeneService, times(1)).updateTransfer(eq(credentials), argThat(notabeneRequest -> {
            assertEquals("publicId", notabeneRequest.getId());
            assertEquals("transactionHash", notabeneRequest.getTxHash());
            return true;
        }));
    }

    @Test
    void testProviderConfiguration_valid() {
        when(notabeneService.testProviderCredentials(credentials)).thenReturn(true);

        boolean validationResult = provider.testProviderConfiguration();

        assertTrue(validationResult);
    }

    @Test
    void testProviderConfiguration_invalid() {
        when(notabeneService.testProviderCredentials(credentials)).thenReturn(false);

        boolean validationResult = provider.testProviderConfiguration();

        assertFalse(validationResult);
    }

    @Test
    void testUpdateCredentials_unchanged() {
        provider.updateCredentials(credentials);

        verify(notabeneAuthService, never()).removeAccessToken(any());
    }

    private static Stream<Arguments> provideDifferentCredentials() {
        return Stream.of(
            Arguments.arguments("clientId", "differentClientSecret"),
            Arguments.arguments("differentClientId", "clientSecret"),
            Arguments.arguments("differentClientId", "differentClientSecret")
        );
    }

    @ParameterizedTest
    @MethodSource("provideDifferentCredentials")
    void testUpdateCredentials_changed(String clientId, String clientSecret) {
        ITravelRuleProviderCredentials newCredentials = new TestTravelRuleProviderCredentials(clientId, clientSecret, "vaspDid");

        provider.updateCredentials(newCredentials);

        verify(notabeneAuthService).removeAccessToken(credentials);
        clearInvocations(notabeneAuthService);

        // Calling again to make sure the new credentials are now used
        provider.updateCredentials(newCredentials);

        verify(notabeneAuthService, never()).removeAccessToken(any());
    }

    private MockedConstruction<NotabeneTransferStatusUpdateListener> mockNotabeneListenerConstruction(
        ITravelRuleTransferUpdateListener listener) {
        return mockConstruction(NotabeneTransferStatusUpdateListener.class, (mock, context) -> {
            assertInstanceOf(ITravelRuleTransferUpdateListener.class, context.arguments().get(0));
            assertEquals(listener, context.arguments().get(0));
        });
    }

    private void assertCreateRequest(ITravelRuleTransferData expected, NotabeneTransferCreateRequest actual) {
        assertEquals(expected.getPublicId(), actual.getTransactionRef());
        assertOriginator(expected.getOriginator(), actual.getOriginator());
        assertBeneficiary(expected, actual.getBeneficiary());
        assertEquals(expected.getOriginatorVasp().getDid(), actual.getOriginatorVaspDid());
        assertEquals(expected.getBeneficiaryVasp().getDid(), actual.getBeneficiaryVaspDid());
        assertEquals(expected.getTransactionAsset(), actual.getTransactionAsset());
        assertEquals(String.valueOf(expected.getTransactionAmount()), actual.getTransactionAmount());
        assertEquals(expected.getDestinationAddress(), actual.getTransactionBlockchainInfo().getDestination());
        assertEquals(expected.getTransactionHash(), actual.getTransactionBlockchainInfo().getTxHash());
    }

    private void assertOriginator(ITravelRuleNaturalPerson expected, NotabeneOriginator originator) {
        NotabenePerson person = originator.getOriginatorPersons().get(0);
        assertPerson(expected, person);
        assertNotNull(originator.getAccountNumber());
        assertEquals(1, originator.getAccountNumber().size());
        assertEquals(expected.getIdentityPublicId(), originator.getAccountNumber().get(0));
    }

    private void assertBeneficiary(ITravelRuleTransferData outgoingTransferData, NotabeneBeneficiary beneficiary) {
        NotabenePerson person = beneficiary.getBeneficiaryPersons().get(0);
        assertNotNull(beneficiary.getAccountNumber());
        assertEquals(1, beneficiary.getAccountNumber().size());
        assertEquals(outgoingTransferData.getBeneficiary().getIdentityPublicId(), beneficiary.getAccountNumber().get(0));
        assertPerson(outgoingTransferData.getBeneficiary(), person);
    }

    private void assertPerson(ITravelRuleNaturalPerson expected, NotabenePerson person) {
        NotabeneNameIdentifier nameIdentifier = person.getNaturalPerson().getName().get(0).getNameIdentifier().get(0);
        assertEquals(expected.getName().getPrimaryName(), nameIdentifier.getPrimaryIdentifier());
        assertEquals(expected.getName().getSecondaryName(), nameIdentifier.getSecondaryIdentifier());
        if (expected.getName().getNameType() != null) {
            assertEquals(expected.getName().getNameType(), nameIdentifier.getNameIdentifierType().name());
        } else {
            assertNull(nameIdentifier.getNameIdentifierType());
        }
        assertEquals(expected.getIdentityPublicId(), person.getNaturalPerson().getCustomerIdentification());
    }

    private ITravelRuleTransferData createTravelRuleMessageData() {
        return new ITravelRuleTransferData() {
            @Override
            public String getPublicId() {
                return "publicId";
            }

            @Override
            public ITravelRuleNaturalPerson getOriginator() {
                return createCounterParty("originator", "ORIGINATOR", "LEGL");
            }

            @Override
            public ITravelRuleNaturalPerson getBeneficiary() {
                return createCounterParty("beneficiary", "BENEFICIARY", null);
            }

            @Override
            public ITravelRuleVasp getOriginatorVasp() {
                return createVasp("originatorVaspDid");
            }

            @Override
            public ITravelRuleVasp getBeneficiaryVasp() {
                return createVasp("beneficiaryVaspDid");
            }

            @Override
            public String getTransactionAsset() {
                return "BTC";
            }

            @Override
            public long getTransactionAmount() {
                return 1_000_000_000;
            }

            @Override
            public String getDestinationAddress() {
                return "destinationAddress";
            }

            @Override
            public BigDecimal getFiatAmount() {
                return BigDecimal.valueOf(100);
            }

            @Override
            public String getFiatCurrency() {
                return "CZK";
            }

            @Override
            public String getTransactionHash() {
                return "transactionHash";
            }
        };
    }

    private ITravelRuleVasp createVasp(String did) {
        return new ITravelRuleVasp() {
            @Override
            public String getDid() {
                return did;
            }

            @Override
            public String getName() {
                return null;
            }
        };
    }

    private ITravelRuleNaturalPerson createCounterParty(String primaryName, String secondaryName, String nameType) {
        ITravelRuleNaturalPersonName name = new ITravelRuleNaturalPersonName() {
            @Override
            public String getPrimaryName() {
                return primaryName;
            }

            @Override
            public String getSecondaryName() {
                return secondaryName;
            }

            @Override
            public String getNameType() {
                return nameType;
            }
        };

        return new ITravelRuleNaturalPerson() {
            @Override
            public ITravelRuleNaturalPersonName getName() {
                return name;
            }

            @Override
            public String getIdentityPublicId() {
                return primaryName + "identityPublicId";
            }
        };
    }

    private void verifyGetAddressOwnershipCalled(IIdentityWalletEvaluationRequest walletContext) {
        ArgumentCaptor<NotabeneAddressOwnershipInfoRequest> captor = ArgumentCaptor.forClass(NotabeneAddressOwnershipInfoRequest.class);
        verify(notabeneService).getAddressOwnershipInformation(eq(credentials), captor.capture());
        NotabeneAddressOwnershipInfoRequest request = captor.getValue();
        assertEquals(walletContext.getCryptoAddress(), request.getAddress());
        assertEquals(walletContext.getCryptocurrency(), request.getAsset());
        assertEquals(credentials.getVaspDid(), request.getVaspDid());
    }

    private IIdentityWalletEvaluationRequest createWalletContext() {
        return new IIdentityWalletEvaluationRequest() {
            @Override
            public String getIdentityPublicId() {
                return "identityPublicId";
            }

            @Override
            public String getIdentityExternalId() {
                return "identityExternalId";
            }

            @Override
            public String getCryptoAddress() {
                return "cryptoAddress";
            }

            @Override
            public String getCryptocurrency() {
                return "cryptocurrency";
            }
        };
    }

    private void assertVasp(int id, ITravelRuleVasp vasp) {
        assertNotNull(vasp);
        assertEquals("did" + id, vasp.getDid());
        assertEquals("name" + id, vasp.getName());
    }

    private static NotabeneVaspInfoSimple createVaspInfoSimple(int id) {
        NotabeneVaspInfoSimple vaspInfoSimple = new NotabeneVaspInfoSimple();
        vaspInfoSimple.setDid("did" + id);
        vaspInfoSimple.setName("name" + id);
        return vaspInfoSimple;
    }

    private static NotabeneTransferInfo createNotabeneTransferInfo(NotabeneTransferStatus status) {
        NotabeneTransferInfo notabeneTransferInfo = new NotabeneTransferInfo();
        notabeneTransferInfo.setId("id");
        notabeneTransferInfo.setStatus(status);
        return notabeneTransferInfo;
    }

    private static ITravelRuleProviderCredentials createITravelRuleProviderCredentials() {
        return new TestTravelRuleProviderCredentials("clientId", "clientSecret", "vaspDid");
    }

    private static class TestTravelRuleProviderCredentials implements ITravelRuleProviderCredentials {
        private final String clientId;
        private final String clientSecret;
        private final String vaspDid;

        public TestTravelRuleProviderCredentials(String clientId, String clientSecret, String vaspDid) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.vaspDid = vaspDid;
        }

        @Override
        public String getClientId() {
            return clientId;
        }

        @Override
        public String getClientSecret() {
            return clientSecret;
        }

        @Override
        public String getVaspDid() {
            return vaspDid;
        }
    }
}