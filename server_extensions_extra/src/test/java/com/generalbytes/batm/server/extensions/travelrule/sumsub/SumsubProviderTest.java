package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.travelrule.IIdentityWalletEvaluationRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferResolvedEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleWalletInfo;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.SumsubVaspListResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.SumsubTransferHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumsubProviderTest {

    @Mock
    private ITravelRuleProviderCredentials credentials;
    @Mock
    private SumsubService sumsubService;
    @Mock
    private SumsubApiService apiService;
    @InjectMocks
    private SumsubProvider provider;

    @Test
    void testGetWalletInfo_nullInput() {
        ITravelRuleWalletInfo walletInfo = provider.getWalletInfo(null);

        assertNull(walletInfo);
        verifyNoInteractions(credentials, sumsubService, apiService);
    }

    @Test
    void testGetWalletInfo_notNullInput() {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);

        ITravelRuleWalletInfo walletInfo = provider.getWalletInfo(request);

        assertNull(walletInfo);
        verifyNoInteractions(request, credentials, sumsubService, apiService);
    }

    @Test
    void testGetAllVasps() {
        SumsubVaspListResponse.VaspDetail vaspDetail = mock(SumsubVaspListResponse.VaspDetail.class);
        when(vaspDetail.getId()).thenReturn("vasp_did");
        when(vaspDetail.getName()).thenReturn("vasp_name");

        SumsubVaspListResponse vaspListResponse = mock(SumsubVaspListResponse.class);
        when(vaspListResponse.getItems()).thenReturn(List.of(vaspDetail));

        when(sumsubService.getAllVasps(credentials)).thenReturn(vaspListResponse);

        List<ITravelRuleVasp> vasps = provider.getAllVasps();

        assertEquals(1, vasps.size());
        assertEquals("vasp_did", vasps.get(0).getDid());
        assertEquals("vasp_name", vasps.get(0).getName());
        verifyNoInteractions(apiService);
    }

    @Test
    void testCreateTransfer() {
        ITravelRuleTransferData outgoingTransferData = mock(ITravelRuleTransferData.class);

        SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);
        when(expectedResponse.getId()).thenReturn("sumsub_transaction_id");

        when(sumsubService.submitTransactionWithoutApplicant(credentials, outgoingTransferData)).thenReturn(expectedResponse);

        ITravelRuleTransferInfo response = provider.createTransfer(outgoingTransferData);

        assertEquals("sumsub_transaction_id", response.getId());
        verifyNoInteractions(apiService);
    }

    @Test
    void testRegisterTransferListener() {
        try (MockedStatic<SumsubTransferHandler> transferHandlerMock = mockStatic(SumsubTransferHandler.class)) {
            ITravelRuleTransferListener transferListener = mock(ITravelRuleTransferListener.class);
            SumsubTransferHandler transferHandlerInstance = mock(SumsubTransferHandler.class);

            transferHandlerMock.when(() -> SumsubTransferHandler.getInstance().registerTransferListener(transferListener))
                    .thenReturn(transferHandlerInstance);
            when(transferHandlerInstance.registerTransferListener(transferListener)).thenReturn(true);

            boolean result = provider.registerTransferListener(transferListener);

            assertTrue(result);
            verifyNoInteractions(credentials, sumsubService, apiService);
        }
    }

    @Test
    void testUnregisterTransferListener() {
        boolean result = provider.unregisterTransferListener();

        assertTrue(result);
        verifyNoInteractions(credentials, sumsubService, apiService);
    }

    @Test
    void testUpdateTransfer() {
        ITravelRuleTransferUpdateRequest updateRequest = mock(ITravelRuleTransferUpdateRequest.class);

        SumsubUpdateTransactionHashResponse expectedResponse = mock(SumsubUpdateTransactionHashResponse.class);
        when(expectedResponse.getId()).thenReturn("sumsub_id");

        when(sumsubService.updateTransactionHash(credentials, updateRequest)).thenReturn(expectedResponse);

        ITravelRuleTransferInfo response = provider.updateTransfer(updateRequest);

        assertEquals("sumsub_id", response.getId());
        verifyNoInteractions(apiService);
    }

    @Test
    void testNotifyProviderConfigurationChanged() {
        assertDoesNotThrow(() -> provider.notifyProviderConfigurationChanged());

        verifyNoInteractions(credentials, sumsubService, apiService);
    }

    @Test
    void testTestProviderConfiguration() {
        when(sumsubService.testProviderCredentials(credentials)).thenReturn(true);

        boolean result = provider.testProviderConfiguration();

        assertTrue(result);
        verifyNoInteractions(apiService);
    }

    @Test
    void testOnTransferResolved() {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);

        when(sumsubService.handleTransferResolved(credentials, event)).thenReturn(true);

        boolean response = provider.onTransferResolved(event);

        assertTrue(response);
        verifyNoInteractions(apiService);
    }

    @Test
    void testGetTransactionInformation() {
        SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);

        when(sumsubService.getTransactionInformation(credentials, "sumsub_id")).thenReturn(expectedResponse);

        SumsubTransactionInformationResponse response = provider.getTransactionInformation("sumsub_id");

        assertSame(expectedResponse, response);
        verifyNoInteractions(apiService);
    }

    @Test
    void testUpdateCredentials_unchanged() {
        mockCredentials();

        ITravelRuleProviderCredentials newCredentials = createCredentials("client_id", "client_secret", "private_key");

        provider.updateCredentials(newCredentials);

        verifyNoInteractions(sumsubService, apiService);
    }

    private static Stream<Arguments> testUpdateCredentials_changed_arguments() {
        return Stream.of(
                arguments(createCredentials("CLIENT_ID", "client_secret", "private_key")),
                arguments(createCredentials("client_id", "CLIENT_SECRET", "private_key")),
                arguments(createCredentials("CLIENT_ID", "CLIENT_SECRET", "private_key")),
                arguments(createCredentials("client_id", "client_secret", "PRIVATE_KEY")),
                arguments(createCredentials("client_id", "CLIENT_SECRET", "PRIVATE_KEY")),
                arguments(createCredentials("CLIENT_ID", "client_secret", "PRIVATE_KEY"))
        );
    }

    @ParameterizedTest
    @MethodSource("testUpdateCredentials_changed_arguments")
    void testUpdateCredentials_changed(ITravelRuleProviderCredentials newCredentials) {
        mockCredentials();

        provider.updateCredentials(newCredentials);

        verify(apiService, times(1)).cleanApiConfig(credentials);
        verifyNoInteractions(sumsubService);
    }

    private void mockCredentials() {
        lenient().when(credentials.getClientId()).thenReturn("client_id");
        lenient().when(credentials.getClientSecret()).thenReturn("client_secret");
        lenient().when(credentials.getPrivateKey()).thenReturn("private_key");
    }

    private static ITravelRuleProviderCredentials createCredentials(String clientId, String clientSecret, String privateKey) {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        when(credentials.getClientId()).thenReturn(clientId);
        when(credentials.getClientSecret()).thenReturn(clientSecret);
        when(credentials.getPrivateKey()).thenReturn(privateKey);

        return credentials;
    }

}