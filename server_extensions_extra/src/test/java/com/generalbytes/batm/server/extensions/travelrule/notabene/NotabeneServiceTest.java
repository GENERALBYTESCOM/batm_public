package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApiException;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneApiError;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneCryptoAddressType;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsQueryParams;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneRegisterWebhookRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferCreateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfoWithIvms;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneUnregisterWebhookRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneVaspInfoSimple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotabeneServiceTest {
    @Mock
    private NotabeneApiWrapper notabeneApiWrapper;
    @Mock
    private NotabeneConfiguration configuration;
    @InjectMocks
    private NotabeneService service;

    @Test
    void testGetAllVasps_valid() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();
        NotabeneVaspInfoSimple vasp = new NotabeneVaspInfoSimple();
        NotabeneListVaspsResponse response = createNotabeneListVaspsResponse(vasp);

        when(notabeneApiWrapper.listVasps(any(), any())).thenReturn(response);

        List<NotabeneVaspInfoSimple> allVasps = service.getAllVasps(providerCredentials);

        assertNotNull(allVasps);
        assertEquals(1, allVasps.size());
        assertEquals(vasp, allVasps.get(0));
        verifyListAllVaspsCalled(providerCredentials);
    }

    @Test
    void testGetAllVasps_nullResponse_returnEmptyList() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        when(notabeneApiWrapper.listVasps(any(), any())).thenReturn(null);

        TravelRuleProviderException exception = assertThrows(TravelRuleProviderException.class, () -> service.getAllVasps(providerCredentials));

        assertEquals("Failed to fetch VASPs from Notabene.", exception.getMessage());
        verifyListAllVaspsCalled(providerCredentials);
    }

    @Test
    void testGetAllVasps_exceptionThrown() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        when(notabeneApiWrapper.listVasps(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        TravelRuleProviderException exception = assertThrows(TravelRuleProviderException.class, () -> service.getAllVasps(providerCredentials));

        assertEquals("Failed to fetch VASPs from Notabene.", exception.getMessage());
        verifyListAllVaspsCalled(providerCredentials);
    }

    @Test
    void testCreateTransfer_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferCreateRequest request = mock(NotabeneTransferCreateRequest.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);

        when(notabeneApiWrapper.createTransfer(any(), any())).thenReturn(response);

        NotabeneTransferInfo result = service.createTransfer(providerCredentials, request);

        assertEquals(response, result);
        verify(notabeneApiWrapper, times(1)).createTransfer(providerCredentials, request);
        verifyNoInteractions(providerCredentials, request);
    }

    @Test
    void testCreateTransfer_exception() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferCreateRequest request = mock(NotabeneTransferCreateRequest.class);

        when(notabeneApiWrapper.createTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        NotabeneTransferInfo result = service.createTransfer(providerCredentials, request);

        assertNull(result);
        verify(notabeneApiWrapper, times(1)).createTransfer(providerCredentials, request);
        verifyNoInteractions(providerCredentials, request);
    }

    @Test
    void testGetAddressOwnershipInformation_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneAddressOwnershipInfoRequest request = mock(NotabeneAddressOwnershipInfoRequest.class);
        NotabeneAddressOwnershipInfoResponse response = mock(NotabeneAddressOwnershipInfoResponse.class);

        when(notabeneApiWrapper.getAddressOwnershipInformation(any(), any())).thenReturn(response);

        NotabeneAddressOwnershipInfoResponse result = service.getAddressOwnershipInformation(providerCredentials, request);

        assertEquals(response, result);
        verify(notabeneApiWrapper, times(1)).getAddressOwnershipInformation(providerCredentials, request);
        verifyNoInteractions(providerCredentials, request);
    }

    private static Stream<Arguments> provideGetAddressOwnershipInformationException() {
        return Stream.of(
            Arguments.arguments(new RuntimeException("Test Exception")),
            Arguments.arguments(createNotabeneApiException(HttpServletResponse.SC_BAD_REQUEST))
        );
    }

    @ParameterizedTest
    @MethodSource("provideGetAddressOwnershipInformationException")
    void testGetAddressOwnershipInformation_exception(Exception exception) {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneAddressOwnershipInfoRequest request = mock(NotabeneAddressOwnershipInfoRequest.class);

        when(notabeneApiWrapper.getAddressOwnershipInformation(any(), any())).thenThrow(exception);

        NotabeneAddressOwnershipInfoResponse result = service.getAddressOwnershipInformation(providerCredentials, request);

        assertNull(result);
        verify(notabeneApiWrapper, times(1)).getAddressOwnershipInformation(providerCredentials, request);
        verifyNoInteractions(providerCredentials, request);
    }

    @Test
    void testGetAddressOwnershipInformation_notFound() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneAddressOwnershipInfoRequest request = mock(NotabeneAddressOwnershipInfoRequest.class);

        when(notabeneApiWrapper.getAddressOwnershipInformation(any(), any()))
            .thenThrow(createNotabeneApiException(HttpServletResponse.SC_NOT_FOUND));

        NotabeneAddressOwnershipInfoResponse result = service.getAddressOwnershipInformation(providerCredentials, request);

        assertNotNull(result);
        assertNull(result.getOwnerVaspDid());
        assertEquals(NotabeneCryptoAddressType.UNKNOWN, result.getAddressType());
        verify(notabeneApiWrapper, times(1)).getAddressOwnershipInformation(providerCredentials, request);
        verifyNoInteractions(providerCredentials, request);
    }

    @Test
    void testUpdateTransfer_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferUpdateRequest request = mock(NotabeneTransferUpdateRequest.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);

        when(notabeneApiWrapper.updateTransfer(any(), any())).thenReturn(response);

        NotabeneTransferInfo result = service.updateTransfer(providerCredentials, request);

        assertEquals(response, result);
        verify(notabeneApiWrapper, times(1)).updateTransfer(providerCredentials, request);
        verifyNoInteractions(providerCredentials, request);
    }

    @Test
    void testUpdateTransfer_exception() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferUpdateRequest request = mock(NotabeneTransferUpdateRequest.class);

        when(notabeneApiWrapper.updateTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        NotabeneTransferInfo result = service.updateTransfer(providerCredentials, request);

        assertNull(result);
        verify(notabeneApiWrapper, times(1)).updateTransfer(providerCredentials, request);
        verifyNoInteractions(providerCredentials, request);
    }

    @Test
    void testApproveTransfer_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);

        when(notabeneApiWrapper.approveTransfer(any(), any())).thenReturn(response);

        NotabeneTransferInfo result = service.approveTransfer(providerCredentials, "transferId");

        assertEquals(response, result);
        verify(notabeneApiWrapper, times(1)).approveTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testApproveTransfer_exception() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        when(notabeneApiWrapper.approveTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        NotabeneTransferInfo result = service.approveTransfer(providerCredentials, "transferId");

        assertNull(result);
        verify(notabeneApiWrapper, times(1)).approveTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testConfirmTransfer_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);

        when(notabeneApiWrapper.confirmTransfer(any(), any())).thenReturn(response);

        NotabeneTransferInfo result = service.confirmTransfer(providerCredentials, "transferId");

        assertEquals(response, result);
        verify(notabeneApiWrapper).confirmTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testConfirmTransfer_exception() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        when(notabeneApiWrapper.confirmTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        NotabeneTransferInfo result = service.confirmTransfer(providerCredentials, "transferId");

        assertNull(result);
        verify(notabeneApiWrapper).confirmTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testRejectTransfer_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);

        when(notabeneApiWrapper.rejectTransfer(any(), any())).thenReturn(response);

        NotabeneTransferInfo result = service.rejectTransfer(providerCredentials, "transferId");

        assertEquals(response, result);
        verify(notabeneApiWrapper).rejectTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testRejectTransfer_exception() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        when(notabeneApiWrapper.rejectTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        NotabeneTransferInfo result = service.rejectTransfer(providerCredentials, "transferId");

        assertNull(result);
        verify(notabeneApiWrapper).rejectTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testAcceptTransfer_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);

        when(notabeneApiWrapper.acceptTransfer(any(), any())).thenReturn(response);

        NotabeneTransferInfo result = service.acceptTransfer(providerCredentials, "transferId");

        assertEquals(response, result);
        verify(notabeneApiWrapper).acceptTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testAcceptTransfer_exception() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        when(notabeneApiWrapper.acceptTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        NotabeneTransferInfo result = service.acceptTransfer(providerCredentials, "transferId");

        assertNull(result);
        verify(notabeneApiWrapper).acceptTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }


    @Test
    void testDeclineTransfer_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);

        when(notabeneApiWrapper.declineTransfer(any(), any())).thenReturn(response);

        NotabeneTransferInfo result = service.declineTransfer(providerCredentials, "transferId");

        assertEquals(response, result);
        verify(notabeneApiWrapper).declineTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testDeclineTransfer_exception() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        when(notabeneApiWrapper.declineTransfer(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        NotabeneTransferInfo result = service.declineTransfer(providerCredentials, "transferId");

        assertNull(result);
        verify(notabeneApiWrapper).declineTransfer(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testGetTransferInfo_valid() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);
        NotabeneTransferInfoWithIvms response = mock(NotabeneTransferInfoWithIvms.class);

        when(notabeneApiWrapper.getTransferInfo(any(), any())).thenReturn(response);

        NotabeneTransferInfo result = service.getTransferInfo(providerCredentials, "transferId");

        assertEquals(response, result);
        verify(notabeneApiWrapper).getTransferInfo(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testGetTransferInfo_exception() {
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        when(notabeneApiWrapper.getTransferInfo(any(), any())).thenThrow(new RuntimeException("Test Exception"));

        NotabeneTransferInfo result = service.getTransferInfo(providerCredentials, "transferId");

        assertNull(result);
        verify(notabeneApiWrapper).getTransferInfo(providerCredentials, "transferId");
        verifyNoInteractions(providerCredentials);
    }

    @Test
    void testRegisterWebhook_valid() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        when(configuration.getMasterExtensionsUrl()).thenReturn("https://localhost:7743/extensions");

        boolean result = service.registerWebhook(providerCredentials);

        assertTrue(result);
        assertRegisterWebhookRequest(providerCredentials);
    }

    @Test
    void testRegisterWebhook_exception() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        when(configuration.getMasterExtensionsUrl()).thenReturn("https://localhost:7743/extensions");

        doThrow(new RuntimeException("Test Exception")).when(notabeneApiWrapper).registerWebhook(any(), any());

        boolean result = service.registerWebhook(providerCredentials);

        assertFalse(result);
        assertRegisterWebhookRequest(providerCredentials);
    }

    private static Stream<Arguments> provideTestUnregisteredWebhookValid() {
        return Stream.of(
            Arguments.arguments((Answer<Void>) invocation -> null),
            Arguments.arguments((Answer<Void>) invocation -> {
                throw createNotabeneApiException(HttpServletResponse.SC_NOT_FOUND);
            })
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestUnregisteredWebhookValid")
    void testUnregisterWebhook_valid(Answer<Void> answer) {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        doAnswer(answer).when(notabeneApiWrapper).unregisterWebhook(any(), any());

        boolean result = service.unregisterWebhook(providerCredentials);

        assertTrue(result);
        assertUnregisterWebhookRequest(providerCredentials);
    }

    private static Stream<Arguments> provideTestUnregisteredWebhookException() {
        return Stream.of(
            Arguments.arguments(new RuntimeException("Test Exception")),
            Arguments.arguments(createNotabeneApiException(HttpServletResponse.SC_BAD_REQUEST))
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestUnregisteredWebhookException")
    void testUnregisterWebhook_exception(Exception exception) {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        doThrow(exception).when(notabeneApiWrapper).unregisterWebhook(any(), any());

        boolean result = service.unregisterWebhook(providerCredentials);

        assertFalse(result);
        assertUnregisterWebhookRequest(providerCredentials);
    }

    @Test
    void testTestProviderCredentials_valid() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        boolean result = service.testProviderCredentials(providerCredentials);

        assertTrue(result);
        verify(notabeneApiWrapper, times(1)).listVasps(eq(providerCredentials), any());
    }

    @Test
    void testTestProviderCredentials_invalid() {
        ITravelRuleProviderCredentials providerCredentials = createTravelRuleProviderIdentification();

        when(notabeneApiWrapper.listVasps(eq(providerCredentials), any())).thenThrow(NotabeneApiException.class);

        boolean result = service.testProviderCredentials(providerCredentials);

        assertFalse(result);
    }

    private ITravelRuleProviderCredentials createTravelRuleProviderIdentification() {
        return new ITravelRuleProviderCredentials() {
            @Override
            public String getClientId() {
                return "clientId";
            }

            @Override
            public String getClientSecret() {
                return "clientSecret";
            }

            @Override
            public String getVaspDid() {
                return "vaspDid";
            }

            @Override
            public String publicKey() {
                return null;
            }

            @Override
            public String privateKey() {
                return null;
            }
        };
    }

    private void assertRegisterWebhookRequest(ITravelRuleProviderCredentials providerCredentials) {
        ArgumentCaptor<NotabeneRegisterWebhookRequest> requestCaptor = ArgumentCaptor.forClass(NotabeneRegisterWebhookRequest.class);
        verify(notabeneApiWrapper).registerWebhook(eq(providerCredentials), requestCaptor.capture());
        NotabeneRegisterWebhookRequest request = requestCaptor.getValue();
        assertNotNull(request);
        assertEquals(providerCredentials.getVaspDid(), request.getVaspDid());
        assertEquals("https://localhost:7743/extensions/notabene/webhooks", request.getUrl());
    }

    private void assertUnregisterWebhookRequest(ITravelRuleProviderCredentials providerCredentials) {
        ArgumentCaptor<NotabeneUnregisterWebhookRequest> requestCaptor = ArgumentCaptor.forClass(NotabeneUnregisterWebhookRequest.class);
        verify(notabeneApiWrapper).unregisterWebhook(eq(providerCredentials), requestCaptor.capture());
        NotabeneUnregisterWebhookRequest request = requestCaptor.getValue();
        assertNotNull(request);
        assertEquals(providerCredentials.getVaspDid(), request.getVaspDid());
    }

    private NotabeneListVaspsResponse createNotabeneListVaspsResponse(NotabeneVaspInfoSimple... vasp) {
        NotabeneListVaspsResponse response = new NotabeneListVaspsResponse();
        response.setVasps(Arrays.asList(vasp));
        return response;
    }

    private void verifyListAllVaspsCalled(ITravelRuleProviderCredentials providerCredentials) {
        ArgumentCaptor<NotabeneListVaspsQueryParams> captor = ArgumentCaptor.forClass(NotabeneListVaspsQueryParams.class);
        verify(notabeneApiWrapper, times(1)).listVasps(eq(providerCredentials), captor.capture());
        NotabeneListVaspsQueryParams params = captor.getValue();
        assertNotNull(params);
        assertTrue(params.getAll());
        assertNull(params.getQuery());
    }

    private static NotabeneApiException createNotabeneApiException(int httpStatus) {
        NotabeneApiError notabeneApiError = new NotabeneApiError();
        notabeneApiError.setMessage("Test Exception");
        notabeneApiError.setCode(httpStatus);
        return new NotabeneApiException(notabeneApiError);
    }

}