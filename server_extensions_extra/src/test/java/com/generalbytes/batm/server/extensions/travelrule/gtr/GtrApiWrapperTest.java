package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiCall;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrNotifyTxIdRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrNotifyTxIdResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspListResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrApiWrapperTest {

    @Mock
    private GtrApi gtrApi;
    @Mock
    private GtrApiService gtrApiService;
    @InjectMocks
    private GtrApiWrapper gtrApiWrapper;

    @Mock
    private GtrCredentials credentials;

    @Test
    void testListVasps() {
        GtrVaspListResponse expectedResponse = new GtrVaspListResponse();

        when(gtrApiService.callApi(eq(credentials), any())).thenReturn(expectedResponse);

        GtrVaspListResponse response = gtrApiWrapper.listVasps(credentials);

        assertEquals(expectedResponse, response);

        ArgumentCaptor<GtrApiCall<GtrVaspListResponse>> apiCallCaptor = ArgumentCaptor.forClass(GtrApiCall.class);
        verify(gtrApiService, times(1)).callApi(eq(credentials), apiCallCaptor.capture());
        GtrApiCall<GtrVaspListResponse> apiCall = apiCallCaptor.getValue();
        apiCall.execute("Bearer token");

        verify(gtrApi, times(1)).listVasps("Bearer token");
    }

    @Test
    void testVaspDetail() {
        GtrVaspResponse expectedResponse = new GtrVaspResponse();

        when(gtrApiService.callApi(eq(credentials), any())).thenReturn(expectedResponse);

        GtrVaspResponse response = gtrApiWrapper.vaspDetail(credentials, "vasp_code");

        assertEquals(expectedResponse, response);

        ArgumentCaptor<GtrApiCall<GtrVaspResponse>> apiCallCaptor = ArgumentCaptor.forClass(GtrApiCall.class);
        verify(gtrApiService, times(1)).callApi(eq(credentials), apiCallCaptor.capture());
        GtrApiCall<GtrVaspResponse> apiCall = apiCallCaptor.getValue();
        apiCall.execute("Bearer token");

        verify(gtrApi, times(1)).vaspDetail("Bearer token", "vasp_code");
    }

    @Test
    void testRegisterTravelRuleRequest() {
        GtrRegisterTravelRuleRequest request = new GtrRegisterTravelRuleRequest();
        GtrRegisterTravelRuleResponse expectedResponse = new GtrRegisterTravelRuleResponse();

        when(gtrApiService.callApi(eq(credentials), any())).thenReturn(expectedResponse);

        GtrRegisterTravelRuleResponse response = gtrApiWrapper.registerTravelRuleRequest(credentials, request);

        assertEquals(expectedResponse, response);

        ArgumentCaptor<GtrApiCall<GtrRegisterTravelRuleResponse>> apiCallCaptor = ArgumentCaptor.forClass(GtrApiCall.class);
        verify(gtrApiService, times(1)).callApi(eq(credentials), apiCallCaptor.capture());
        GtrApiCall<GtrRegisterTravelRuleResponse> apiCall = apiCallCaptor.getValue();
        apiCall.execute("Bearer token");

        verify(gtrApi, times(1)).registerTravelRuleRequest("Bearer token", request);
    }

    @Test
    void testVerifyAddress() {
        GtrVerifyAddressRequest request = new GtrVerifyAddressRequest();
        GtrVerifyAddressResponse expectedResponse = new GtrVerifyAddressResponse();

        when(gtrApiService.callApi(eq(credentials), any())).thenReturn(expectedResponse);

        GtrVerifyAddressResponse response = gtrApiWrapper.verifyAddress(credentials, request);

        assertEquals(expectedResponse, response);

        ArgumentCaptor<GtrApiCall<GtrVerifyAddressResponse>> apiCallCaptor = ArgumentCaptor.forClass(GtrApiCall.class);
        verify(gtrApiService, times(1)).callApi(eq(credentials), apiCallCaptor.capture());
        GtrApiCall<GtrVerifyAddressResponse> apiCall = apiCallCaptor.getValue();
        apiCall.execute("Bearer token");

        verify(gtrApi, times(1)).verifyAddress("Bearer token", request);
    }

    @Test
    void testVerifyPii() {
        GtrVerifyPiiRequest request = new GtrVerifyPiiRequest();
        GtrVerifyPiiResponse expectedResponse = new GtrVerifyPiiResponse();

        when(gtrApiService.callApi(eq(credentials), any())).thenReturn(expectedResponse);

        GtrVerifyPiiResponse response = gtrApiWrapper.verifyPii(credentials, request);

        assertEquals(expectedResponse, response);

        ArgumentCaptor<GtrApiCall<GtrVerifyPiiResponse>> apiCallCaptor = ArgumentCaptor.forClass(GtrApiCall.class);
        verify(gtrApiService, times(1)).callApi(eq(credentials), apiCallCaptor.capture());
        GtrApiCall<GtrVerifyPiiResponse> apiCall = apiCallCaptor.getValue();
        apiCall.execute("Bearer token");

        verify(gtrApi, times(1)).verifyPii("Bearer token", request);
    }

    @Test
    void testNotifyTxId() {
        GtrNotifyTxIdRequest request = new GtrNotifyTxIdRequest();
        GtrNotifyTxIdResponse expectedResponse = new GtrNotifyTxIdResponse();

        when(gtrApiService.callApi(eq(credentials), any())).thenReturn(expectedResponse);

        GtrNotifyTxIdResponse response = gtrApiWrapper.notifyTxId(credentials, request);

        assertEquals(expectedResponse, response);

        ArgumentCaptor<GtrApiCall<GtrNotifyTxIdResponse>> apiCallCaptor = ArgumentCaptor.forClass(GtrApiCall.class);
        verify(gtrApiService, times(1)).callApi(eq(credentials), apiCallCaptor.capture());
        GtrApiCall<GtrNotifyTxIdResponse> apiCall = apiCallCaptor.getValue();
        apiCall.execute("Bearer token");

        verify(gtrApi, times(1)).notifyTxId("Bearer token", request);
    }

}