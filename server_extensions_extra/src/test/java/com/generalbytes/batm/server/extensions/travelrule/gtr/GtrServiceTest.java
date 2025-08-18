package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.IIdentityWalletEvaluationRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrNotifyTxIdRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrNotifyTxIdResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspBasicInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspListResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrTransferHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrProviderMapper;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.Curve25519Encryptor;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.RequestIdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrServiceTest {

    @Mock
    private GtrApiWrapper gtrApiWrapper;
    @Mock
    private RequestIdGenerator requestIdGenerator;
    @Mock
    private GtrVerifyAddressService verifyAddressService;
    @Mock
    private GtrVerifyPiiService verifyPiiService;
    @Mock
    private GtrTransferHandler transferHandler;
    @Mock
    private GtrValidator gtrValidator;
    @Mock
    private Curve25519Encryptor curve25519Encryptor;
    @InjectMocks
    private GtrService gtrService;

    @Mock
    private GtrCredentials credentials;

    @Test
    void testGetAllVasps() {
        List<GtrVaspBasicInfo> basicInfoList = new ArrayList<>();

        when(gtrApiWrapper.listVasps(credentials)).thenReturn(createGtrVaspListResponse(basicInfoList));

        List<GtrVaspBasicInfo> vasps = gtrService.getAllVasps(credentials);

        assertEquals(basicInfoList, vasps);
    }

    @Test
    void testGetAllVasps_throwsException() {
        when(gtrApiWrapper.listVasps(credentials)).thenThrow(new RuntimeException("test-unexpected-api-error"));

        TravelRuleProviderException exception = assertThrows(TravelRuleProviderException.class, () -> gtrService.getAllVasps(credentials));

        assertEquals("Failed to fetch VASPs from GTR.", exception.getMessage());
    }

    @Test
    void testGetVaspDetail() {
        GtrVaspInfo data = new GtrVaspInfo();

        when(gtrApiWrapper.vaspDetail(credentials, "vasp_code")).thenReturn(createGtrVaspResponse(data));

        GtrVaspInfo vaspInfo = gtrService.getVaspDetail(credentials, "vasp_code");

        assertEquals(data, vaspInfo);
        verify(gtrValidator, times(1)).validateVaspCode("vasp_code");
    }

    @Test
    void testGetVaspDetail_invalidVaspCode() {
        doThrow(new TravelRuleProviderException("test-get-vasp-detail-invalid-vasp-code"))
                .when(gtrValidator).validateVaspCode("invalid_vasp_code");

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrService.getVaspDetail(credentials, "invalid_vasp_code")
        );

        assertEquals("Failed to get VASP detail from GTR.", exception.getMessage());
        verifyNoInteractions(gtrApiWrapper);
    }

    @Test
    void testGetVaspDetail_throwsException() {
        when(gtrApiWrapper.vaspDetail(credentials, "vasp_code")).thenThrow(new RuntimeException("test-unexpected-api-error"));

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrService.getVaspDetail(credentials, "vasp_code")
        );

        assertEquals("Failed to get VASP detail from GTR.", exception.getMessage());
    }

    @Test
    void testRegisterTravelRuleRequest() {
        when(requestIdGenerator.generateRequestId()).thenReturn("request_id");

        when(credentials.getVaspCode()).thenReturn("vasp_code");

        GtrRegisterTravelRuleResponse expectedResponse = mock(GtrRegisterTravelRuleResponse.class);
        when(expectedResponse.getRequestId()).thenReturn("request_id");
        when(expectedResponse.getTravelRuleId()).thenReturn("travel_rule_id");

        when(gtrApiWrapper.registerTravelRuleRequest(eq(credentials), any())).thenReturn(expectedResponse);

        GtrRegisterTravelRuleResponse response = gtrService.registerTravelRuleRequest(credentials);

        assertEquals("request_id", response.getRequestId());
        assertEquals("travel_rule_id", response.getTravelRuleId());
        assertTestRegisterTravelRuleRequest();
        verify(gtrValidator, times(1)).validateRegisterTravelRuleResponse(expectedResponse, "request_id");
    }

    @Test
    void testRegisterTravelRuleRequest_invalidResponse() {
        when(requestIdGenerator.generateRequestId()).thenReturn("request_id");

        when(credentials.getVaspCode()).thenReturn("vasp_code");
        GtrRegisterTravelRuleResponse response = mock(GtrRegisterTravelRuleResponse.class);
        when(gtrApiWrapper.registerTravelRuleRequest(eq(credentials), any())).thenReturn(response);
        doThrow(new TravelRuleProviderException("test-register-request-invalid-response"))
                .when(gtrValidator).validateRegisterTravelRuleResponse(response, "request_id");

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrService.registerTravelRuleRequest(credentials)
        );

        assertEquals("Failed to register GTR request.", exception.getMessage());
        assertTestRegisterTravelRuleRequest();
        verify(gtrValidator, times(1)).validateRegisterTravelRuleResponse(response, "request_id");
    }

    private void assertTestRegisterTravelRuleRequest() {
        ArgumentCaptor<GtrRegisterTravelRuleRequest> requestCaptor = ArgumentCaptor.forClass(GtrRegisterTravelRuleRequest.class);
        verify(gtrApiWrapper, times(1)).registerTravelRuleRequest(eq(credentials), requestCaptor.capture());
        GtrRegisterTravelRuleRequest registerTravelRuleRequest = requestCaptor.getValue();

        assertEquals("request_id", registerTravelRuleRequest.getRequestId());
        assertEquals("vasp_code", registerTravelRuleRequest.getSourceVaspCode());
        assertEquals(4, registerTravelRuleRequest.getVerifyType());
    }

    @Test
    void testRegisterTravelRuleRequest_throwsException_requestIdGenerator() {
        when(requestIdGenerator.generateRequestId()).thenThrow(new RuntimeException("test-unexpected-request-id-generator-error"));

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrService.registerTravelRuleRequest(credentials)
        );

        assertEquals("Failed to register GTR request.", exception.getMessage());
        verify(credentials, never()).getVaspCode();
        verifyNoInteractions(gtrApiWrapper);
    }

    @Test
    void testRegisterTravelRuleRequest_throwsException_api() {
        when(requestIdGenerator.generateRequestId()).thenReturn("request_id");
        when(credentials.getVaspCode()).thenReturn("vasp_code");
        when(gtrApiWrapper.registerTravelRuleRequest(eq(credentials), any())).thenThrow(new RuntimeException("test-unexpected-api-error"));

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrService.registerTravelRuleRequest(credentials)
        );

        assertEquals("Failed to register GTR request.", exception.getMessage());
        assertTestRegisterTravelRuleRequest();
    }

    @Test
    void testGetWalletInfo() {
        GtrVerifyAddressResponse expectedResponse = new GtrVerifyAddressResponse();
        when(credentials.getCurvePublicKey()).thenReturn("initiator_vasp_public_key");
        when(verifyAddressService.verifyAddress(eq(credentials), any(), eq(GtrCryptoNetwork.BTC))).thenReturn(expectedResponse);

        GtrRegisterTravelRuleResponse registerTravelRuleResponse = mockRegisterTravelRuleRequest();
        mockGetVaspPublicKey();

        IIdentityWalletEvaluationRequest request = createIIdentityWalletEvaluationRequest();
        GtrVerifyAddressResponse response = gtrService.getWalletInfo(credentials, request);

        assertEquals(expectedResponse, response);
        verify(gtrValidator, times(1)).validateRegisterTravelRuleResponse(registerTravelRuleResponse, "request_id");
        verify(gtrValidator, times(1)).validateWalletEvaluationRequest(request);
        verify(gtrValidator, times(1)).validateVaspCode("hosting_vasp_did");
        assertVerifyAddressResponse();
    }

    @Test
    void testGetWalletInfo_throwsException_validator() {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);

        doThrow(new TravelRuleProviderException("test-get-wallet-info-validator-exception"))
                .when(gtrValidator).validateWalletEvaluationRequest(request);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrService.getWalletInfo(credentials, request)
        );

        assertEquals("Failed to get wallet information from GTR.", exception.getMessage());
        verifyNoInteractions(gtrApiWrapper);
    }

    @Test
    void testGetWalletInfo_throwsException_verifyAddressService() {
        mockRegisterTravelRuleRequest();
        mockGetVaspPublicKey();

        doThrow(new TravelRuleProviderException("test-get-wallet-info-verify-address-service-exception"))
                .when(verifyAddressService).verifyAddress(eq(credentials), any(), eq(GtrCryptoNetwork.BTC));

        IIdentityWalletEvaluationRequest request = createIIdentityWalletEvaluationRequest();
        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrService.getWalletInfo(credentials, request)
        );

        assertEquals("Failed to get wallet information from GTR.", exception.getMessage());
        verify(gtrValidator, times(1)).validateWalletEvaluationRequest(request);
    }

    @Test
    void testCreateTransfer() {
        GtrVerifyAddressResponse verifyAddressResponse = mock(GtrVerifyAddressResponse.class);
        when(verifyAddressResponse.isSuccess()).thenReturn(true);

        ITravelRuleTransferData transferData = createITravelRuleTransferData();
        when(transferData.getPublicId()).thenReturn("transfer_public_id");

        GtrVerifyPiiResponse verifyPiiResponse = mock(GtrVerifyPiiResponse.class);

        when(credentials.getCurvePublicKey()).thenReturn("initiator_vasp_public_key");
        when(verifyAddressService.verifyAddress(eq(credentials), any(), eq(GtrCryptoNetwork.BTC))).thenReturn(verifyAddressResponse);
        when(verifyPiiService.verifyPii(credentials, transferData, "request_id", "hosting_vasp_public_key"))
                .thenReturn(verifyPiiResponse);

        mockGetVaspPublicKey();
        GtrRegisterTravelRuleResponse registerResponse = mockRegisterTravelRuleRequest();

        GtrVerifyPiiResponse response = gtrService.createTransfer(credentials, transferData);

        assertEquals(verifyPiiResponse, response);
        verify(gtrValidator, times(1)).validateRegisterTravelRuleResponse(registerResponse, "request_id");
        verify(gtrValidator, times(1)).validateVaspCode("hosting_vasp_did");
        verify(transferHandler, times(1)).handleVerifyPiiResponse("transfer_public_id", response);
        assertVerifyAddressResponse();
    }

    @Test
    void testCreateTransfer_verifyAddress_failed() {
        GtrVerifyAddressResponse verifyAddressResponse = mock(GtrVerifyAddressResponse.class);
        when(verifyAddressResponse.isSuccess()).thenReturn(false);

        when(credentials.getCurvePublicKey()).thenReturn("initiator_vasp_public_key");
        when(verifyAddressService.verifyAddress(eq(credentials), any(), eq(GtrCryptoNetwork.BTC))).thenReturn(verifyAddressResponse);

        mockGetVaspPublicKey();
        GtrRegisterTravelRuleResponse registerResponse = mockRegisterTravelRuleRequest();

        ITravelRuleTransferData transferData = createITravelRuleTransferData();

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrService.createTransfer(credentials, transferData)
        );

        assertEquals("Failed to create GTR transfer.", exception.getMessage());
        verify(gtrValidator, times(1)).validateRegisterTravelRuleResponse(registerResponse, "request_id");
        verify(gtrValidator, times(1)).validateVaspCode("hosting_vasp_did");
        verifyNoInteractions(verifyPiiService);
        verifyNoInteractions(transferHandler);
        assertVerifyAddressResponse();
    }

    @Test
    void testNotifyTxId() {
        try (MockedStatic<GtrProviderMapper> gtrProviderMapperMock = mockStatic(GtrProviderMapper.class)) {
            ITravelRuleTransferUpdateRequest transferUpdateRequest = mock(ITravelRuleTransferUpdateRequest.class);
            GtrNotifyTxIdRequest notifyTxIdRequest = mock(GtrNotifyTxIdRequest.class);

            GtrNotifyTxIdResponse response = mock(GtrNotifyTxIdResponse.class);
            when(response.isSuccess()).thenReturn(true);

            gtrProviderMapperMock.when(() -> GtrProviderMapper.toGtrNotifyTxIdRequest(transferUpdateRequest)).thenReturn(notifyTxIdRequest);
            when(gtrApiWrapper.notifyTxId(credentials, notifyTxIdRequest)).thenReturn(response);

            gtrService.notifyTxId(credentials, transferUpdateRequest);

            verify(transferUpdateRequest, times(1)).getTransactionHash();
            verify(response, never()).getStatusCode();
            verify(response, never()).getMessage();
        }
    }

    @Test
    void testNotifyTxId_notSuccess() {
        try (MockedStatic<GtrProviderMapper> gtrProviderMapperMock = mockStatic(GtrProviderMapper.class)) {
            ITravelRuleTransferUpdateRequest transferUpdateRequest = mock(ITravelRuleTransferUpdateRequest.class);
            GtrNotifyTxIdRequest notifyTxIdRequest = mock(GtrNotifyTxIdRequest.class);

            GtrNotifyTxIdResponse response = mock(GtrNotifyTxIdResponse.class);
            when(response.isSuccess()).thenReturn(false);

            gtrProviderMapperMock.when(() -> GtrProviderMapper.toGtrNotifyTxIdRequest(transferUpdateRequest)).thenReturn(notifyTxIdRequest);
            when(gtrApiWrapper.notifyTxId(credentials, notifyTxIdRequest)).thenReturn(response);

            gtrService.notifyTxId(credentials, transferUpdateRequest);

            verify(transferUpdateRequest, times(1)).getTransactionHash();
            verify(response, times(1)).getStatusCode();
            verify(response, times(1)).getMessage();
        }
    }

    @Test
    void testNotifyTxId_throwsException_notifyTxId() {
        try (MockedStatic<GtrProviderMapper> gtrProviderMapperMock = mockStatic(GtrProviderMapper.class)) {
            ITravelRuleTransferUpdateRequest transferUpdateRequest = mock(ITravelRuleTransferUpdateRequest.class);
            GtrNotifyTxIdRequest notifyTxIdRequest = mock(GtrNotifyTxIdRequest.class);

            gtrProviderMapperMock.when(() -> GtrProviderMapper.toGtrNotifyTxIdRequest(transferUpdateRequest)).thenReturn(notifyTxIdRequest);
            when(gtrApiWrapper.notifyTxId(credentials, notifyTxIdRequest)).thenThrow(new RuntimeException("test-notify-tx-id-exception"));

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> gtrService.notifyTxId(credentials, transferUpdateRequest)
            );

            assertEquals("Failed to notify the beneficiary VASP via GTR about the on-chain transaction hash.", exception.getMessage());
            verify(transferUpdateRequest, times(1)).getTransactionHash();
        }
    }

    @Test
    void testTestProviderCredentials() {
        when(credentials.getVaspCode()).thenReturn("vasp_code");
        when(credentials.getCurvePublicKey()).thenReturn("curve_public_key");
        when(credentials.getCurvePrivateKey()).thenReturn("curve_private_key");

        when(curve25519Encryptor.validateKeyPair("curve_public_key", "curve_private_key")).thenReturn(true);

        boolean valid = gtrService.testProviderCredentials(credentials);

        verify(gtrApiWrapper, times(1)).listVasps(credentials);
        assertTrue(valid);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testTestProviderCredentials_vaspCodeIsBlank(String vaspCode) {
        when(credentials.getVaspCode()).thenReturn(vaspCode);

        boolean valid = gtrService.testProviderCredentials(credentials);

        verifyNoInteractions(curve25519Encryptor, gtrApiWrapper);
        assertFalse(valid);
    }

    @Test
    void testTestProviderCredentials_encryptorValidationFailed() {
        when(credentials.getVaspCode()).thenReturn("vasp_code");
        when(credentials.getCurvePublicKey()).thenReturn("curve_public_key");
        when(credentials.getCurvePrivateKey()).thenReturn("curve_private_key");

        when(curve25519Encryptor.validateKeyPair("curve_public_key", "curve_private_key")).thenReturn(false);

        boolean valid = gtrService.testProviderCredentials(credentials);

        verifyNoInteractions(gtrApiWrapper);
        assertFalse(valid);
    }

    @Test
    void testTestProviderCredentials_encryptorException() {
        when(credentials.getVaspCode()).thenReturn("vasp_code");
        when(credentials.getCurvePublicKey()).thenReturn("curve_public_key");
        when(credentials.getCurvePrivateKey()).thenReturn("curve_private_key");

        when(curve25519Encryptor.validateKeyPair("curve_public_key", "curve_private_key"))
                .thenThrow(new RuntimeException("test-credentials-encryptor-exception"));

        boolean valid = gtrService.testProviderCredentials(credentials);

        verifyNoInteractions(gtrApiWrapper);
        assertFalse(valid);
    }

    @Test
    void testTestProviderCredentials_apiException() {
        when(credentials.getVaspCode()).thenReturn("vasp_code");
        when(credentials.getCurvePublicKey()).thenReturn("curve_public_key");
        when(credentials.getCurvePrivateKey()).thenReturn("curve_private_key");
        when(curve25519Encryptor.validateKeyPair("curve_public_key", "curve_private_key")).thenReturn(true);
        when(gtrApiWrapper.listVasps(credentials)).thenThrow(new RuntimeException("test-credentials-api-exception"));

        boolean valid = gtrService.testProviderCredentials(credentials);

        assertFalse(valid);
    }

    private void assertVerifyAddressResponse() {
        ArgumentCaptor<GtrVerifyAddressRequest> verifyAddressRequestCaptor = ArgumentCaptor.forClass(GtrVerifyAddressRequest.class);
        verify(verifyAddressService, times(1))
                .verifyAddress(eq(credentials), verifyAddressRequestCaptor.capture(), eq(GtrCryptoNetwork.BTC));
        GtrVerifyAddressRequest registerTravelRuleRequest = verifyAddressRequestCaptor.getValue();

        assertEquals("request_id", registerTravelRuleRequest.getRequestId());
        assertEquals("cryptoAddress", registerTravelRuleRequest.getAddress());
        assertEquals("destination_tag", registerTravelRuleRequest.getTag());
        assertEquals("initiator_vasp_public_key", registerTravelRuleRequest.getInitiatorPublicKey());
        assertEquals("hosting_vasp_did", registerTravelRuleRequest.getTargetVaspCode());
        assertEquals("hosting_vasp_public_key", registerTravelRuleRequest.getTargetVaspPublicKey());
        assertEquals("BTC", registerTravelRuleRequest.getTicker());
    }

    private GtrRegisterTravelRuleResponse mockRegisterTravelRuleRequest() {
        doReturn("request_id").when(requestIdGenerator).generateRequestId();

        GtrRegisterTravelRuleResponse registerTravelRuleResponse = mock(GtrRegisterTravelRuleResponse.class);
        when(registerTravelRuleResponse.getRequestId()).thenReturn("request_id");
        when(gtrApiWrapper.registerTravelRuleRequest(eq(credentials), any())).thenReturn(registerTravelRuleResponse);

        return registerTravelRuleResponse;
    }

    private void mockGetVaspPublicKey() {
        GtrVaspInfo vaspInfo = mock(GtrVaspInfo.class);
        when(vaspInfo.getPublicKey()).thenReturn("hosting_vasp_public_key");

        GtrVaspResponse vaspResponse = mock(GtrVaspResponse.class);
        when(vaspResponse.getData()).thenReturn(vaspInfo);

        when(gtrApiWrapper.vaspDetail(credentials, "hosting_vasp_did")).thenReturn(vaspResponse);
    }

    private ITravelRuleTransferData createITravelRuleTransferData() {
        ITravelRuleVasp beneficiaryVasp = mock(ITravelRuleVasp.class);
        when(beneficiaryVasp.getDid()).thenReturn("hosting_vasp_did");

        ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
        when(transferData.getBeneficiaryVasp()).thenReturn(beneficiaryVasp);
        when(transferData.getTransactionAsset()).thenReturn("BTC");
        when(transferData.getDestinationAddress()).thenReturn("cryptoAddress:destination_tag");

        return transferData;
    }

    private IIdentityWalletEvaluationRequest createIIdentityWalletEvaluationRequest() {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);
        when(request.getCryptoAddress()).thenReturn("cryptoAddress:destination_tag");
        when(request.getCryptocurrency()).thenReturn("BTC");
        when(request.getDidOfVaspHostingCustodialWallet()).thenReturn("hosting_vasp_did");

        return request;
    }

    private GtrVaspListResponse createGtrVaspListResponse(List<GtrVaspBasicInfo> data) {
        GtrVaspListResponse response = new GtrVaspListResponse();
        response.setData(data);

        return response;
    }

    private GtrVaspResponse createGtrVaspResponse(GtrVaspInfo data) {
        GtrVaspResponse response = new GtrVaspResponse();
        response.setData(data);

        return response;
    }

}