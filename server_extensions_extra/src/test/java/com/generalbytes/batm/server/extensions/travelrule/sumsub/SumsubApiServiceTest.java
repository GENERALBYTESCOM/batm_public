package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.SumsubVaspListResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction.SumsubSubmitTxWithoutApplicantRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactionownershipresolution.SumsubTransactionOwnershipResolutionResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.walletownershipconfirmation.SumsubConfirmWalletOwnershipRequest;
import com.generalbytes.batm.server.extensions.common.sumsub.api.SumsubApiFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumsubApiServiceTest {

    @Mock
    private SumsubApiFactory apiFactory;
    @InjectMocks
    private SumsubApiService apiService;

    @Test
    void testGetAllVasps_sameToken() {
        ITravelRuleProviderCredentials credentials = createCredentials("token", "secret");
        SumsubTravelRuleApi api = mock(SumsubTravelRuleApi.class);
        SumsubVaspListResponse expectedResponse = mock(SumsubVaspListResponse.class);

        when(api.listVasps(Integer.MAX_VALUE)).thenReturn(expectedResponse);
        when(apiFactory.createSumsubTravelRuleApi("token", "secret")).thenReturn(api);

        SumsubVaspListResponse response1 = apiService.getAllVasps(credentials);
        SumsubVaspListResponse response2 = apiService.getAllVasps(credentials);

        assertSame(expectedResponse, response1);
        assertSame(expectedResponse, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi(anyString(), anyString());
    }

    @Test
    void testGetAllVasps_differentToken() {
        ITravelRuleProviderCredentials credentials1 = createCredentials("token_1", "secret_1");
        SumsubTravelRuleApi api1 = mock(SumsubTravelRuleApi.class);
        SumsubVaspListResponse expectedResponse1 = mock(SumsubVaspListResponse.class);

        when(api1.listVasps(Integer.MAX_VALUE)).thenReturn(expectedResponse1);
        when(apiFactory.createSumsubTravelRuleApi("token_1", "secret_1")).thenReturn(api1);

        ITravelRuleProviderCredentials credentials2 = createCredentials("token_2", "secret_2");
        SumsubTravelRuleApi api2 = mock(SumsubTravelRuleApi.class);
        SumsubVaspListResponse expectedResponse2 = mock(SumsubVaspListResponse.class);

        when(api2.listVasps(Integer.MAX_VALUE)).thenReturn(expectedResponse2);
        when(apiFactory.createSumsubTravelRuleApi("token_2", "secret_2")).thenReturn(api2);

        SumsubVaspListResponse response1 = apiService.getAllVasps(credentials1);
        SumsubVaspListResponse response2 = apiService.getAllVasps(credentials2);

        assertSame(expectedResponse1, response1);
        assertSame(expectedResponse2, response2);
        assertNotSame(response1, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_1", "secret_1");
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_2", "secret_2");
    }

    @Test
    void testSubmitTransactionWithoutApplicant_sameToken() {
        ITravelRuleProviderCredentials credentials = createCredentials("token", "secret");
        SumsubTravelRuleApi api = mock(SumsubTravelRuleApi.class);
        SumsubSubmitTxWithoutApplicantRequest request = mock(SumsubSubmitTxWithoutApplicantRequest.class);
        SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);

        when(api.submitTransactionWithoutApplicant(request)).thenReturn(expectedResponse);
        when(apiFactory.createSumsubTravelRuleApi("token", "secret")).thenReturn(api);

        SumsubTransactionInformationResponse response1 = apiService.submitTransactionWithoutApplicant(credentials, request);
        SumsubTransactionInformationResponse response2 = apiService.submitTransactionWithoutApplicant(credentials, request);

        assertSame(expectedResponse, response1);
        assertSame(expectedResponse, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi(anyString(), anyString());
    }

    @Test
    void testSubmitTransactionWithoutApplicant_differentToken() {
        ITravelRuleProviderCredentials credentials1 = createCredentials("token_1", "secret_1");
        SumsubTravelRuleApi api1 = mock(SumsubTravelRuleApi.class);
        SumsubSubmitTxWithoutApplicantRequest request1 = mock(SumsubSubmitTxWithoutApplicantRequest.class);
        SumsubTransactionInformationResponse expectedResponse1 = mock(SumsubTransactionInformationResponse.class);

        when(api1.submitTransactionWithoutApplicant(request1)).thenReturn(expectedResponse1);
        when(apiFactory.createSumsubTravelRuleApi("token_1", "secret_1")).thenReturn(api1);

        ITravelRuleProviderCredentials credentials2 = createCredentials("token_2", "secret_2");
        SumsubTravelRuleApi api2 = mock(SumsubTravelRuleApi.class);
        SumsubSubmitTxWithoutApplicantRequest request2 = mock(SumsubSubmitTxWithoutApplicantRequest.class);
        SumsubTransactionInformationResponse expectedResponse2 = mock(SumsubTransactionInformationResponse.class);

        when(api2.submitTransactionWithoutApplicant(request2)).thenReturn(expectedResponse2);
        when(apiFactory.createSumsubTravelRuleApi("token_2", "secret_2")).thenReturn(api2);

        SumsubTransactionInformationResponse response1 = apiService.submitTransactionWithoutApplicant(credentials1, request1);
        SumsubTransactionInformationResponse response2 = apiService.submitTransactionWithoutApplicant(credentials2, request2);

        assertSame(expectedResponse1, response1);
        assertSame(expectedResponse2, response2);
        assertNotSame(response1, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_1", "secret_1");
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_2", "secret_2");
    }

    @Test
    void testUpdateTransactionHash_sameToken() {
        ITravelRuleProviderCredentials credentials = createCredentials("token", "secret");
        SumsubTravelRuleApi api = mock(SumsubTravelRuleApi.class);
        SumsubUpdateTransactionHashRequest request = mock(SumsubUpdateTransactionHashRequest.class);
        SumsubUpdateTransactionHashResponse expectedResponse = mock(SumsubUpdateTransactionHashResponse.class);

        when(api.updateTransactionHash("sumsubId", request)).thenReturn(expectedResponse);
        when(apiFactory.createSumsubTravelRuleApi("token", "secret")).thenReturn(api);

        SumsubUpdateTransactionHashResponse response1 = apiService.updateTransactionHash(credentials, "sumsubId", request);
        SumsubUpdateTransactionHashResponse response2 = apiService.updateTransactionHash(credentials, "sumsubId", request);

        assertSame(expectedResponse, response1);
        assertSame(expectedResponse, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi(anyString(), anyString());
    }

    @Test
    void testUpdateTransactionHash_differentToken() {
        ITravelRuleProviderCredentials credentials1 = createCredentials("token_1", "secret_1");
        SumsubTravelRuleApi api1 = mock(SumsubTravelRuleApi.class);
        SumsubUpdateTransactionHashRequest request1 = mock(SumsubUpdateTransactionHashRequest.class);
        SumsubUpdateTransactionHashResponse expectedResponse1 = mock(SumsubUpdateTransactionHashResponse.class);

        when(api1.updateTransactionHash("sumsubId1", request1)).thenReturn(expectedResponse1);
        when(apiFactory.createSumsubTravelRuleApi("token_1", "secret_1")).thenReturn(api1);

        ITravelRuleProviderCredentials credentials2 = createCredentials("token_2", "secret_2");
        SumsubTravelRuleApi api2 = mock(SumsubTravelRuleApi.class);
        SumsubUpdateTransactionHashRequest request2 = mock(SumsubUpdateTransactionHashRequest.class);
        SumsubUpdateTransactionHashResponse expectedResponse2 = mock(SumsubUpdateTransactionHashResponse.class);

        when(api2.updateTransactionHash("sumsubId2", request2)).thenReturn(expectedResponse2);
        when(apiFactory.createSumsubTravelRuleApi("token_2", "secret_2")).thenReturn(api2);

        SumsubUpdateTransactionHashResponse response1 = apiService.updateTransactionHash(credentials1, "sumsubId1", request1);
        SumsubUpdateTransactionHashResponse response2 = apiService.updateTransactionHash(credentials2, "sumsubId2", request2);

        assertSame(expectedResponse1, response1);
        assertSame(expectedResponse2, response2);
        assertNotSame(response1, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_1", "secret_1");
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_2", "secret_2");
    }

    @Test
    void testGetTransactionInformation_sameToken() {
        ITravelRuleProviderCredentials credentials = createCredentials("token", "secret");
        SumsubTravelRuleApi api = mock(SumsubTravelRuleApi.class);
        SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);

        when(api.getTransactionInformation("sumsub_id")).thenReturn(expectedResponse);
        when(apiFactory.createSumsubTravelRuleApi("token", "secret")).thenReturn(api);

        SumsubTransactionInformationResponse response1 = apiService.getTransactionInformation(credentials, "sumsub_id");
        SumsubTransactionInformationResponse response2 = apiService.getTransactionInformation(credentials, "sumsub_id");

        assertSame(expectedResponse, response1);
        assertSame(expectedResponse, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi(anyString(), anyString());
    }

    @Test
    void testGetTransactionInformation_differentToken() {
        ITravelRuleProviderCredentials credentials1 = createCredentials("token_1", "secret_1");
        SumsubTravelRuleApi api1 = mock(SumsubTravelRuleApi.class);
        SumsubTransactionInformationResponse expectedResponse1 = mock(SumsubTransactionInformationResponse.class);

        when(api1.getTransactionInformation("sumsub_id_1")).thenReturn(expectedResponse1);
        when(apiFactory.createSumsubTravelRuleApi("token_1", "secret_1")).thenReturn(api1);

        ITravelRuleProviderCredentials credentials2 = createCredentials("token_2", "secret_2");
        SumsubTravelRuleApi api2 = mock(SumsubTravelRuleApi.class);
        SumsubTransactionInformationResponse expectedResponse2 = mock(SumsubTransactionInformationResponse.class);

        when(api2.getTransactionInformation("sumsub_id_2")).thenReturn(expectedResponse2);
        when(apiFactory.createSumsubTravelRuleApi("token_2", "secret_2")).thenReturn(api2);

        SumsubTransactionInformationResponse response1 = apiService.getTransactionInformation(credentials1, "sumsub_id_1");
        SumsubTransactionInformationResponse response2 = apiService.getTransactionInformation(credentials2, "sumsub_id_2");

        assertSame(expectedResponse1, response1);
        assertSame(expectedResponse2, response2);
        assertNotSame(response1, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_1", "secret_1");
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_2", "secret_2");
    }

    @Test
    void testConfirmTransactionOwnership_sameToken() {
        ITravelRuleProviderCredentials credentials = createCredentials("token", "secret");
        SumsubTravelRuleApi api = mock(SumsubTravelRuleApi.class);
        SumsubTransactionOwnershipResolutionResponse expectedResponse = mock(SumsubTransactionOwnershipResolutionResponse.class);

        when(api.confirmTransactionOwnership("sumsub_id")).thenReturn(expectedResponse);
        when(apiFactory.createSumsubTravelRuleApi("token", "secret")).thenReturn(api);

        SumsubTransactionOwnershipResolutionResponse response1 = apiService.confirmTransactionOwnership(credentials, "sumsub_id");
        SumsubTransactionOwnershipResolutionResponse response2 = apiService.confirmTransactionOwnership(credentials, "sumsub_id");

        assertSame(expectedResponse, response1);
        assertSame(expectedResponse, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi(anyString(), anyString());
    }

    @Test
    void testConfirmTransactionOwnership_differentToken() {
        ITravelRuleProviderCredentials credentials1 = createCredentials("token_1", "secret_1");
        SumsubTravelRuleApi api1 = mock(SumsubTravelRuleApi.class);
        SumsubTransactionOwnershipResolutionResponse expectedResponse1 = mock(SumsubTransactionOwnershipResolutionResponse.class);

        when(api1.confirmTransactionOwnership("sumsub_id_1")).thenReturn(expectedResponse1);
        when(apiFactory.createSumsubTravelRuleApi("token_1", "secret_1")).thenReturn(api1);

        ITravelRuleProviderCredentials credentials2 = createCredentials("token_2", "secret_2");
        SumsubTravelRuleApi api2 = mock(SumsubTravelRuleApi.class);
        SumsubTransactionOwnershipResolutionResponse expectedResponse2 = mock(SumsubTransactionOwnershipResolutionResponse.class);

        when(api2.confirmTransactionOwnership("sumsub_id_2")).thenReturn(expectedResponse2);
        when(apiFactory.createSumsubTravelRuleApi("token_2", "secret_2")).thenReturn(api2);

        SumsubTransactionOwnershipResolutionResponse response1 = apiService.confirmTransactionOwnership(credentials1, "sumsub_id_1");
        SumsubTransactionOwnershipResolutionResponse response2 = apiService.confirmTransactionOwnership(credentials2, "sumsub_id_2");

        assertSame(expectedResponse1, response1);
        assertSame(expectedResponse2, response2);
        assertNotSame(response1, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_1", "secret_1");
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_2", "secret_2");
    }

    @Test
    void testRejectTransactionOwnership_sameToken() {
        ITravelRuleProviderCredentials credentials = createCredentials("token", "secret");
        SumsubTravelRuleApi api = mock(SumsubTravelRuleApi.class);
        SumsubTransactionOwnershipResolutionResponse expectedResponse = mock(SumsubTransactionOwnershipResolutionResponse.class);

        when(api.rejectTransactionOwnership("sumsub_id")).thenReturn(expectedResponse);
        when(apiFactory.createSumsubTravelRuleApi("token", "secret")).thenReturn(api);

        SumsubTransactionOwnershipResolutionResponse response1 = apiService.rejectTransactionOwnership(credentials, "sumsub_id");
        SumsubTransactionOwnershipResolutionResponse response2 = apiService.rejectTransactionOwnership(credentials, "sumsub_id");

        assertSame(expectedResponse, response1);
        assertSame(expectedResponse, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi(anyString(), anyString());
    }

    @Test
    void testRejectTransactionOwnership_differentToken() {
        ITravelRuleProviderCredentials credentials1 = createCredentials("token_1", "secret_1");
        SumsubTravelRuleApi api1 = mock(SumsubTravelRuleApi.class);
        SumsubTransactionOwnershipResolutionResponse expectedResponse1 = mock(SumsubTransactionOwnershipResolutionResponse.class);

        when(api1.rejectTransactionOwnership("sumsub_id_1")).thenReturn(expectedResponse1);
        when(apiFactory.createSumsubTravelRuleApi("token_1", "secret_1")).thenReturn(api1);

        ITravelRuleProviderCredentials credentials2 = createCredentials("token_2", "secret_2");
        SumsubTravelRuleApi api2 = mock(SumsubTravelRuleApi.class);
        SumsubTransactionOwnershipResolutionResponse expectedResponse2 = mock(SumsubTransactionOwnershipResolutionResponse.class);

        when(api2.rejectTransactionOwnership("sumsub_id_2")).thenReturn(expectedResponse2);
        when(apiFactory.createSumsubTravelRuleApi("token_2", "secret_2")).thenReturn(api2);

        SumsubTransactionOwnershipResolutionResponse response1 = apiService.rejectTransactionOwnership(credentials1, "sumsub_id_1");
        SumsubTransactionOwnershipResolutionResponse response2 = apiService.rejectTransactionOwnership(credentials2, "sumsub_id_2");

        assertSame(expectedResponse1, response1);
        assertSame(expectedResponse2, response2);
        assertNotSame(response1, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_1", "secret_1");
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_2", "secret_2");
    }

    @Test
    void testConfirmWalletOwnership_sameToken() {
        ITravelRuleProviderCredentials credentials = createCredentials("token", "secret");
        SumsubTravelRuleApi api = mock(SumsubTravelRuleApi.class);
        SumsubConfirmWalletOwnershipRequest request = mock(SumsubConfirmWalletOwnershipRequest.class);
        SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);

        when(api.confirmWalletOwnership("sumsub_id", request)).thenReturn(expectedResponse);
        when(apiFactory.createSumsubTravelRuleApi("token", "secret")).thenReturn(api);

        SumsubTransactionInformationResponse response1 = apiService.confirmWalletOwnership(credentials, "sumsub_id", request);
        SumsubTransactionInformationResponse response2 = apiService.confirmWalletOwnership(credentials, "sumsub_id", request);

        assertSame(expectedResponse, response1);
        assertSame(expectedResponse, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi(anyString(), anyString());
    }

    @Test
    void testConfirmWalletOwnership_differentToken() {
        ITravelRuleProviderCredentials credentials1 = createCredentials("token_1", "secret_1");
        SumsubTravelRuleApi api1 = mock(SumsubTravelRuleApi.class);
        SumsubConfirmWalletOwnershipRequest request1 = mock(SumsubConfirmWalletOwnershipRequest.class);
        SumsubTransactionInformationResponse expectedResponse1 = mock(SumsubTransactionInformationResponse.class);

        when(api1.confirmWalletOwnership("sumsub_id_1", request1)).thenReturn(expectedResponse1);
        when(apiFactory.createSumsubTravelRuleApi("token_1", "secret_1")).thenReturn(api1);

        ITravelRuleProviderCredentials credentials2 = createCredentials("token_2", "secret_2");
        SumsubTravelRuleApi api2 = mock(SumsubTravelRuleApi.class);
        SumsubConfirmWalletOwnershipRequest request2 = mock(SumsubConfirmWalletOwnershipRequest.class);
        SumsubTransactionInformationResponse expectedResponse2 = mock(SumsubTransactionInformationResponse.class);

        when(api2.confirmWalletOwnership("sumsub_id_2", request2)).thenReturn(expectedResponse2);
        when(apiFactory.createSumsubTravelRuleApi("token_2", "secret_2")).thenReturn(api2);

        SumsubTransactionInformationResponse response1 = apiService.confirmWalletOwnership(credentials1, "sumsub_id_1", request1);
        SumsubTransactionInformationResponse response2 = apiService.confirmWalletOwnership(credentials2, "sumsub_id_2", request2);

        assertSame(expectedResponse1, response1);
        assertSame(expectedResponse2, response2);
        assertNotSame(response1, response2);
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_1", "secret_1");
        verify(apiFactory, times(1)).createSumsubTravelRuleApi("token_2", "secret_2");
    }

    @Test
    void testCleanApiConfig_apiNotExist() {
        ITravelRuleProviderCredentials credentials = createCredentials("token");

        assertDoesNotThrow(() -> apiService.cleanApiConfig(credentials));
    }

    @Test
    void testCleanApiConfig_apiExist() {
        ITravelRuleProviderCredentials credentials = createCredentials("token", "secret");
        SumsubTravelRuleApi api = mock(SumsubTravelRuleApi.class);
        SumsubVaspListResponse expectedResponse = mock(SumsubVaspListResponse.class);

        when(api.listVasps(Integer.MAX_VALUE)).thenReturn(expectedResponse);
        when(apiFactory.createSumsubTravelRuleApi("token", "secret")).thenReturn(api);

        apiService.getAllVasps(credentials);
        apiService.cleanApiConfig(credentials);
        apiService.getAllVasps(credentials);

        verify(apiFactory, times(2)).createSumsubTravelRuleApi("token", "secret");
    }

    private ITravelRuleProviderCredentials createCredentials(String clientId) {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        when(credentials.getClientId()).thenReturn(clientId);

        return credentials;
    }

    private ITravelRuleProviderCredentials createCredentials(String clientId, String secret) {
        ITravelRuleProviderCredentials credentials = createCredentials(clientId);
        when(credentials.getClientSecret()).thenReturn(secret);

        return credentials;
    }

}