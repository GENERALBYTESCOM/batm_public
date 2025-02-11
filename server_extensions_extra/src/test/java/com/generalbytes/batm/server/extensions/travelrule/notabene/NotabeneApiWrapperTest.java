package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.fasterxml.jackson.core.JsonParseException;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApi;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApiCall;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneFullyValidateTransferResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsQueryParams;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneRegisterWebhookRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferCreateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneUnregisterWebhookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotabeneApiWrapperTest {

    @Mock
    private NotabeneApi api;
    @Mock
    private NotabeneApiService apiService;
    @Mock
    private NotabeneApiFactory apiFactory;

    private NotabeneApiWrapper apiWrapper;

    @BeforeEach
    void setUp() {
        when(apiFactory.getNotabeneApi()).thenReturn(api);

        apiWrapper = new NotabeneApiWrapper(apiFactory, apiService);
    }

    private static Stream<Arguments> provideQueryParametersForListVasps() {
        return Stream.of(
            Arguments.arguments("query", true),
            Arguments.arguments("query", false),
            Arguments.arguments("query", null),
            Arguments.arguments(null, true),
            Arguments.arguments(null, false),
            Arguments.arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideQueryParametersForListVasps")
    void testListVasps(String query, Boolean returnAllRecords) {
        NotabeneListVaspsQueryParams params = mock(NotabeneListVaspsQueryParams.class);
        when(params.getQuery()).thenReturn(query);
        when(params.getAll()).thenReturn(returnAllRecords);
        NotabeneListVaspsResponse response = mock(NotabeneListVaspsResponse.class);
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        testApiWrapperMethod(providerCredentials, response,
            () -> apiWrapper.listVasps(providerCredentials, params),
            authorization -> verify(api).listVasps(authorization, query, returnAllRecords));
    }

    @Test
    void testValidateFull() {
        NotabeneTransferCreateRequest request = mock(NotabeneTransferCreateRequest.class);
        NotabeneFullyValidateTransferResponse response = mock(NotabeneFullyValidateTransferResponse.class);
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        testApiWrapperMethod(providerCredentials, response,
            () -> apiWrapper.validateFull(providerCredentials, request),
            authorization -> verify(api).validateFull(authorization, request));
    }

    @Test
    void testCreateTransfer() {
        NotabeneTransferCreateRequest request = mock(NotabeneTransferCreateRequest.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        testApiWrapperMethod(providerCredentials, response,
            () -> apiWrapper.createTransfer(providerCredentials, request),
            authorization -> verify(api).createTransfer(authorization, request));
    }

    @Test
    void testUpdateTransfer() {
        NotabeneTransferUpdateRequest request = mock(NotabeneTransferUpdateRequest.class);
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        testApiWrapperMethod(providerCredentials, response,
            () -> apiWrapper.updateTransfer(providerCredentials, request),
            authorization -> verify(api).updateTransfer(authorization, request));
    }

    @Test
    void testGetAddressOwnershipInformation() {
        NotabeneAddressOwnershipInfoRequest request = mock(NotabeneAddressOwnershipInfoRequest.class);
        when(request.getAddress()).thenReturn("address");
        when(request.getVaspDid()).thenReturn("vaspDID");
        when(request.getAsset()).thenReturn("asset");
        NotabeneAddressOwnershipInfoResponse response = mock(NotabeneAddressOwnershipInfoResponse.class);
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        testApiWrapperMethod(providerCredentials, response,
            () -> apiWrapper.getAddressOwnershipInformation(providerCredentials, request),
            authorization -> verify(api).getAddressOwnershipInformation(authorization, "address", "vaspDID", "asset"));
    }

    @Test
    void testApproveTransfer() {
        NotabeneTransferInfo response = mock(NotabeneTransferInfo.class);
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        testApiWrapperMethod(providerCredentials, response,
            () -> apiWrapper.approveTransfer(providerCredentials, "transferId"),
            authorization -> verify(api).approveTransfer(authorization, "transferId"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testRegisterWebhook(boolean shouldThrowJsonParseException) throws JsonParseException {
        NotabeneRegisterWebhookRequest request = mock(NotabeneRegisterWebhookRequest.class);
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        if (shouldThrowJsonParseException) {
            doThrow(new JsonParseException(null, "Test Exception")).when(api).registerWebhook(anyString(), any());
        }

        testApiWrapperMethod(providerCredentials, null,
            () -> {
                apiWrapper.registerWebhook(providerCredentials, request);
                return null;
            },
            authorization -> verify(api).registerWebhook(authorization, request));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testUnregisterWebhook(boolean shouldThrowJsonParseException) throws JsonParseException {
        NotabeneUnregisterWebhookRequest request = mock(NotabeneUnregisterWebhookRequest.class);
        ITravelRuleProviderCredentials providerCredentials = mock(ITravelRuleProviderCredentials.class);

        if (shouldThrowJsonParseException) {
            doThrow(new JsonParseException(null, "Test Exception")).when(api).unregisterWebhook(anyString(), any());
        }

        testApiWrapperMethod(providerCredentials, null,
            () -> {
                apiWrapper.unregisterWebhook(providerCredentials, request);
                return null;
            },
            authorization -> verify(api).unregisterWebhook(authorization, request));
    }

    private <R> void testApiWrapperMethod(ITravelRuleProviderCredentials providerCredentialsMock, R responseMock,
                                          Supplier<R> apiWrapperCall, ThrowingConsumer<String> apiCallVerification) {
        when(apiService.callApi(eq(providerCredentialsMock), any())).thenReturn(responseMock);

        R result = apiWrapperCall.get();

        assertEquals(responseMock, result);

        ArgumentCaptor<NotabeneApiCall<R>> captor = ArgumentCaptor.forClass(NotabeneApiCall.class);
        verify(apiService).callApi(eq(providerCredentialsMock), captor.capture());

        NotabeneApiCall<R> capturedCall = captor.getValue();
        assertNotNull(capturedCall);

        capturedCall.execute("authorization");
        try {
            apiCallVerification.accept("authorization");
        } catch (Exception e) {
            fail("Unexpected exception thrown from api call verification");
        }

        verifyNoInteractions(providerCredentialsMock);
        if (responseMock != null) {
            verifyNoInteractions(responseMock);
        }
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

}