package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressResponse;
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
class GtrVerifyAddressServiceTest {

    @Mock
    private GtrApiWrapper gtrApiWrapper;
    @InjectMocks
    private GtrVerifyAddressService verifyAddressService;

    @Mock
    private GtrCredentials credentials;

    @Test
    void testVerifyAddress_oneNetwork() {
        GtrVerifyAddressRequest request = new GtrVerifyAddressRequest();

        when(gtrApiWrapper.verifyAddress(eq(credentials), any())).thenReturn(createSuccessGtrVerifyAddressResponse());

        GtrVerifyAddressResponse response = verifyAddressService.verifyAddress(credentials, request, GtrCryptoNetwork.BTC);

        assertEquals(100000, response.getStatusCode());
        assertEquals("address exists", response.getMessage());

        ArgumentCaptor<GtrVerifyAddressRequest> requestCaptor = ArgumentCaptor.forClass(GtrVerifyAddressRequest.class);
        verify(gtrApiWrapper, times(1)).verifyAddress(eq(credentials), requestCaptor.capture());
        GtrVerifyAddressRequest verifyAddressRequest = requestCaptor.getValue();

        assertEquals("BTC", verifyAddressRequest.getNetwork());
    }

    @Test
    void testVerifyAddress_multipleNetworks() {
        GtrVerifyAddressRequest request = new GtrVerifyAddressRequest();

        when(gtrApiWrapper.verifyAddress(eq(credentials), any())).thenReturn(
                createNotFoundGtrVerifyAddressResponse(),
                createSuccessGtrVerifyAddressResponse()
        );

        GtrVerifyAddressResponse response = verifyAddressService.verifyAddress(credentials, request, GtrCryptoNetwork.BTC);

        assertEquals(100000, response.getStatusCode());
        assertEquals("address exists", response.getMessage());

        ArgumentCaptor<GtrVerifyAddressRequest> requestCaptor = ArgumentCaptor.forClass(GtrVerifyAddressRequest.class);
        verify(gtrApiWrapper, times(2)).verifyAddress(eq(credentials), requestCaptor.capture());
        GtrVerifyAddressRequest verifyAddressRequest = requestCaptor.getValue();

        assertEquals("SEGWITBTC", verifyAddressRequest.getNetwork());
    }

    @Test
    void testVerifyAddress_multipleNetworks_addressNotFound() {
        GtrVerifyAddressRequest request = new GtrVerifyAddressRequest();

        when(gtrApiWrapper.verifyAddress(eq(credentials), any())).thenReturn(createNotFoundGtrVerifyAddressResponse());

        GtrVerifyAddressResponse response = verifyAddressService.verifyAddress(credentials, request, GtrCryptoNetwork.BTC);

        assertEquals(200001, response.getStatusCode());
        assertEquals("address not found", response.getMessage());

        verify(gtrApiWrapper, times(6)).verifyAddress(eq(credentials), any());
    }

    private GtrVerifyAddressResponse createSuccessGtrVerifyAddressResponse() {
        GtrVerifyAddressResponse response = new GtrVerifyAddressResponse();
        response.setSuccess(true);
        response.setVerifyStatus(100000);
        response.setVerifyMessage("address exists");

        return response;
    }

    private GtrVerifyAddressResponse createNotFoundGtrVerifyAddressResponse() {
        GtrVerifyAddressResponse response = new GtrVerifyAddressResponse();
        response.setSuccess(false);
        response.setVerifyStatus(200001);
        response.setVerifyMessage("address not found");

        return response;
    }

}