package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferResolvedEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.SumsubVaspListResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction.SumsubSubmitTxWithoutApplicantRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactionownershipresolution.SumsubTransactionOwnershipResolutionResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.walletownershipconfirmation.SumsubConfirmWalletOwnershipRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.mapper.SumsubTravelRuleApiMapper;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api.SumsubApiException;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api.vo.SumsubApiError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumsubServiceTest {

    @Mock
    private IExtensionContext extensionContext;
    @Mock
    private SumsubApiService apiService;
    @Mock
    private SumsubValidator validator;
    @InjectMocks
    private SumsubService sumsubService;

    @Mock
    private ITravelRuleProviderCredentials credentials;

    @Test
    void testGetAllVasps() {
        SumsubVaspListResponse expectedResponse = mock(SumsubVaspListResponse.class);

        when(apiService.getAllVasps(credentials)).thenReturn(expectedResponse);

        SumsubVaspListResponse response = sumsubService.getAllVasps(credentials);

        assertSame(expectedResponse, response);
    }

    @Test
    void testGetAllVasps_exception() {
        when(apiService.getAllVasps(credentials)).thenThrow(new RuntimeException("test-get-all-vasps-exception"));

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> sumsubService.getAllVasps(credentials)
        );

        assertEquals("Failed to fetch VASPs from Sumsub.", exception.getMessage());
    }

    @Test
    void testSubmitTransactionWithoutApplicant() {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
            when(transferData.getTransactionAmount()).thenReturn(2_100_000_000L);
            when(transferData.getTransactionAsset()).thenReturn("BTC");

            SumsubSubmitTxWithoutApplicantRequest request = mock(SumsubSubmitTxWithoutApplicantRequest.class);
            SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);

            when(extensionContext.convertCryptoFromBaseUnit(2_100_000_000L, "BTC")).thenReturn(BigDecimal.valueOf(21));
            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubSubmitTxWithoutApplicantRequest(
                    transferData, BigDecimal.valueOf(21)
            )).thenReturn(request);
            when(apiService.submitTransactionWithoutApplicant(credentials, request)).thenReturn(expectedResponse);

            SumsubTransactionInformationResponse response = sumsubService.submitTransactionWithoutApplicant(credentials, transferData);

            assertSame(expectedResponse, response);
            verify(validator, times(1)).validateSubmitTxWithoutApplicantResponse(request, expectedResponse);
        }
    }

    private static Stream<Arguments> exception_arguments() {
        return Stream.of(
                arguments(new RuntimeException("test-submit-transaction-without-applicant-runtime-exception")),
                arguments(new TravelRuleProviderException("test-submit-transaction-without-applicant-travel-rule-provider-exception")),
                arguments(new SumsubApiException(new SumsubApiError()))
        );
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testSubmitTransactionWithoutApplicant_apiServiceException(Exception thrownException) {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
            when(transferData.getTransactionAmount()).thenReturn(2_100_000_000L);
            when(transferData.getTransactionAsset()).thenReturn("BTC");

            SumsubSubmitTxWithoutApplicantRequest request = mock(SumsubSubmitTxWithoutApplicantRequest.class);

            when(extensionContext.convertCryptoFromBaseUnit(2_100_000_000L, "BTC")).thenReturn(BigDecimal.valueOf(21));
            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubSubmitTxWithoutApplicantRequest(
                    transferData, BigDecimal.valueOf(21)
            )).thenReturn(request);
            when(apiService.submitTransactionWithoutApplicant(credentials, request)).thenThrow(thrownException);

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> sumsubService.submitTransactionWithoutApplicant(credentials, transferData)
            );

            assertEquals("Failed to submit transaction for non-existing applicant via Sumsub.", exception.getMessage());
            verifyNoInteractions(validator);
        }
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testSubmitTransactionWithoutApplicant_validatorException(Exception thrownException) {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
            when(transferData.getTransactionAmount()).thenReturn(2_100_000_000L);
            when(transferData.getTransactionAsset()).thenReturn("BTC");

            SumsubSubmitTxWithoutApplicantRequest request = mock(SumsubSubmitTxWithoutApplicantRequest.class);
            SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);

            when(extensionContext.convertCryptoFromBaseUnit(2_100_000_000L, "BTC")).thenReturn(BigDecimal.valueOf(21));
            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubSubmitTxWithoutApplicantRequest(
                    transferData, BigDecimal.valueOf(21)
            )).thenReturn(request);
            when(apiService.submitTransactionWithoutApplicant(credentials, request)).thenReturn(expectedResponse);
            doThrow(thrownException).when(validator).validateSubmitTxWithoutApplicantResponse(request, expectedResponse);

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> sumsubService.submitTransactionWithoutApplicant(credentials, transferData)
            );

            assertEquals("Failed to submit transaction for non-existing applicant via Sumsub.", exception.getMessage());
        }
    }

    @Test
    void testUpdateTransactionHash() {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferUpdateRequest updateRequest = mock(ITravelRuleTransferUpdateRequest.class);
            when(updateRequest.getId()).thenReturn("sumsub_id");

            SumsubUpdateTransactionHashRequest request = mock(SumsubUpdateTransactionHashRequest.class);
            SumsubUpdateTransactionHashResponse expectedResponse = mock(SumsubUpdateTransactionHashResponse.class);

            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubUpdateTransactionHashRequest(updateRequest)).thenReturn(request);
            when(apiService.updateTransactionHash(credentials, "sumsub_id", request)).thenReturn(expectedResponse);

            SumsubUpdateTransactionHashResponse response = sumsubService.updateTransactionHash(credentials, updateRequest);

            assertSame(expectedResponse, response);
            verify(validator, times(1)).validateSumsubUpdateTransactionHashResponse(updateRequest, expectedResponse);
            verifyNoInteractions(extensionContext);
        }
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testUpdateTransactionHash_apiServiceException(Exception thrownException) {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferUpdateRequest updateRequest = mock(ITravelRuleTransferUpdateRequest.class);
            when(updateRequest.getId()).thenReturn("sumsub_id");

            SumsubUpdateTransactionHashRequest request = mock(SumsubUpdateTransactionHashRequest.class);

            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubUpdateTransactionHashRequest(updateRequest)).thenReturn(request);
            when(apiService.updateTransactionHash(credentials, "sumsub_id", request)).thenThrow(thrownException);

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> sumsubService.updateTransactionHash(credentials, updateRequest)
            );

            assertEquals("Failed to update blockchain transaction hash via Sumsub.", exception.getMessage());
            verifyNoInteractions(validator, extensionContext);
        }
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testUpdateTransactionHash_validatorException(Exception thrownException) {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferUpdateRequest updateRequest = mock(ITravelRuleTransferUpdateRequest.class);
            when(updateRequest.getId()).thenReturn("sumsub_id");

            SumsubUpdateTransactionHashRequest request = mock(SumsubUpdateTransactionHashRequest.class);
            SumsubUpdateTransactionHashResponse expectedResponse = mock(SumsubUpdateTransactionHashResponse.class);

            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubUpdateTransactionHashRequest(updateRequest)).thenReturn(request);
            when(apiService.updateTransactionHash(credentials, "sumsub_id", request)).thenReturn(expectedResponse);
            doThrow(thrownException).when(validator).validateSumsubUpdateTransactionHashResponse(updateRequest, expectedResponse);

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> sumsubService.updateTransactionHash(credentials, updateRequest)
            );

            assertEquals("Failed to update blockchain transaction hash via Sumsub.", exception.getMessage());
            verifyNoInteractions(extensionContext);
        }
    }

    @Test
    void testGetTransactionInformation() {
        SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);
        when(expectedResponse.getData()).thenReturn(mock(SumsubTransactionInformationResponse.TransactionData.class));

        when(apiService.getTransactionInformation(credentials, "sumsub_id")).thenReturn(expectedResponse);

        SumsubTransactionInformationResponse response = sumsubService.getTransactionInformation(credentials, "sumsub_id");

        assertSame(expectedResponse, response);
        verify(validator, times(1)).validateSumsubTransactionInformationResponse("sumsub_id", expectedResponse);
        verifyNoInteractions(extensionContext);
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testGetTransactionInformation_apiServiceException(Exception thrownException) {
        when(apiService.getTransactionInformation(credentials, "sumsub_id")).thenThrow(thrownException);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> sumsubService.getTransactionInformation(credentials, "sumsub_id")
        );

        assertEquals("Failed to get transaction information from Sumsub.", exception.getMessage());
        verifyNoInteractions(validator, extensionContext);
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testGetTransactionInformation_validatorException(Exception thrownException) {
        SumsubTransactionInformationResponse expectedResponse = mock(SumsubTransactionInformationResponse.class);

        when(apiService.getTransactionInformation(credentials, "sumsub_id")).thenReturn(expectedResponse);
        doThrow(thrownException).when(validator).validateSumsubTransactionInformationResponse("sumsub_id", expectedResponse);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> sumsubService.getTransactionInformation(credentials, "sumsub_id")
        );

        assertEquals("Failed to get transaction information from Sumsub.", exception.getMessage());
        verifyNoInteractions(extensionContext);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testTestProviderCredentials_clientId_blank(String clientId) {
        when(credentials.getClientId()).thenReturn(clientId);

        boolean result = sumsubService.testProviderCredentials(credentials);

        assertFalse(result);
        verifyNoInteractions(apiService, extensionContext);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testTestProviderCredentials_clientSecret_blank(String clientSecret) {
        when(credentials.getClientId()).thenReturn("client_id");
        when(credentials.getClientSecret()).thenReturn(clientSecret);

        boolean result = sumsubService.testProviderCredentials(credentials);

        assertFalse(result);
        verifyNoInteractions(apiService, extensionContext);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testTestProviderCredentials_privateKey_blank(String privateKey) {
        when(credentials.getClientId()).thenReturn("client_id");
        when(credentials.getClientSecret()).thenReturn("client_secret");
        when(credentials.getPrivateKey()).thenReturn(privateKey);

        boolean result = sumsubService.testProviderCredentials(credentials);

        assertFalse(result);
        verifyNoInteractions(apiService, extensionContext);
    }

    @Test
    void testTestProviderCredentials_apiException() {
        when(credentials.getClientId()).thenReturn("client_id");
        when(credentials.getClientSecret()).thenReturn("client_secret");
        when(credentials.getPrivateKey()).thenReturn("private_key");
        when(apiService.getAllVasps(credentials)).thenThrow(new RuntimeException("test-provider-credentials-api-exception"));

        boolean valid = sumsubService.testProviderCredentials(credentials);

        assertFalse(valid);
        verifyNoMoreInteractions(apiService);
        verifyNoInteractions(extensionContext);
    }

    @Test
    void testTestProviderCredentials() {
        when(credentials.getClientId()).thenReturn("client_id");
        when(credentials.getClientSecret()).thenReturn("client_secret");
        when(credentials.getPrivateKey()).thenReturn("private_key");

        boolean result = sumsubService.testProviderCredentials(credentials);

        assertTrue(result);
        verify(apiService, times(1)).getAllVasps(credentials);
        verifyNoMoreInteractions(apiService);
        verifyNoInteractions(extensionContext);
    }

    @Test
    void testHandleTransferResolved_approved() {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.APPROVED);
            SumsubTransactionOwnershipResolutionResponse resolutionResponse = mock(SumsubTransactionOwnershipResolutionResponse.class);

            when(apiService.confirmTransactionOwnership(credentials, "sumsub_transaction_id")).thenReturn(resolutionResponse);

            ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
            SumsubConfirmWalletOwnershipRequest confirmWalletOwnershipRequest = mock(SumsubConfirmWalletOwnershipRequest.class);
            SumsubTransactionInformationResponse confirmWalletOwnershipResponse = mock(SumsubTransactionInformationResponse.class);

            when(extensionContext.findTravelRuleTransferByPublicId("transfer_public_id")).thenReturn(transferData);
            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubConfirmWalletOwnershipRequest(transferData))
                    .thenReturn(confirmWalletOwnershipRequest);
            when(apiService.confirmWalletOwnership(credentials, "sumsub_transaction_id", confirmWalletOwnershipRequest))
                    .thenReturn(confirmWalletOwnershipResponse);

            boolean response = sumsubService.handleTransferResolved(credentials, event);

            assertTrue(response);
            verify(validator, times(1)).validateSumsubTransactionOwnershipResolutionResponse(event, resolutionResponse);
            verify(validator, times(1)).validateSumsubConfirmWalletOwnershipResponse(event, confirmWalletOwnershipResponse);
            verifyNoMoreInteractions(apiService, extensionContext, validator);
        }
    }

    @Test
    void testHandleTransferResolved_rejected() {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.REJECTED);
            SumsubTransactionOwnershipResolutionResponse resolutionResponse = mock(SumsubTransactionOwnershipResolutionResponse.class);

            when(apiService.rejectTransactionOwnership(credentials, "sumsub_transaction_id")).thenReturn(resolutionResponse);

            ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
            SumsubConfirmWalletOwnershipRequest confirmWalletOwnershipRequest = mock(SumsubConfirmWalletOwnershipRequest.class);
            SumsubTransactionInformationResponse confirmWalletOwnershipResponse = mock(SumsubTransactionInformationResponse.class);

            when(extensionContext.findTravelRuleTransferByPublicId("transfer_public_id")).thenReturn(transferData);
            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubConfirmWalletOwnershipRequest(transferData))
                    .thenReturn(confirmWalletOwnershipRequest);
            when(apiService.confirmWalletOwnership(credentials, "sumsub_transaction_id", confirmWalletOwnershipRequest))
                    .thenReturn(confirmWalletOwnershipResponse);

            boolean response = sumsubService.handleTransferResolved(credentials, event);

            assertTrue(response);
            verify(validator, times(1)).validateSumsubTransactionOwnershipResolutionResponse(event, resolutionResponse);
            verify(validator, times(1)).validateSumsubConfirmWalletOwnershipResponse(event, confirmWalletOwnershipResponse);
            verifyNoMoreInteractions(apiService, extensionContext, validator);
        }
    }

    @Test
    void testHandleTransferResolved_inProgress() {
        ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.IN_PROGRESS);

        boolean response = sumsubService.handleTransferResolved(credentials, event);

        assertFalse(response);
        verifyNoInteractions(apiService, validator, extensionContext);
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testHandleTransferResolved_approved_apiServiceException(Exception thrownException) {
        ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.APPROVED);

        when(apiService.confirmTransactionOwnership(credentials, "sumsub_transaction_id")).thenThrow(thrownException);

        boolean response = sumsubService.handleTransferResolved(credentials, event);

        assertFalse(response);
        verifyNoInteractions(validator, extensionContext);
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testHandleTransferResolved_rejected_apiServiceException(Exception thrownException) {
        ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.REJECTED);

        when(apiService.rejectTransactionOwnership(credentials, "sumsub_transaction_id")).thenThrow(thrownException);

        boolean response = sumsubService.handleTransferResolved(credentials, event);

        assertFalse(response);
        verifyNoInteractions(validator, extensionContext);
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testHandleTransferResolved_approved_validatorException(Exception thrownException) {
        ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.APPROVED);
        SumsubTransactionOwnershipResolutionResponse resolutionResponse = mock(SumsubTransactionOwnershipResolutionResponse.class);

        when(apiService.confirmTransactionOwnership(credentials, "sumsub_transaction_id")).thenReturn(resolutionResponse);
        doThrow(thrownException).when(validator).validateSumsubTransactionOwnershipResolutionResponse(event, resolutionResponse);

        boolean response = sumsubService.handleTransferResolved(credentials, event);

        assertFalse(response);
        verifyNoMoreInteractions(apiService);
        verifyNoInteractions(extensionContext);
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testHandleTransferResolved_rejected_validatorException(Exception thrownException) {
        ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.REJECTED);
        SumsubTransactionOwnershipResolutionResponse resolutionResponse = mock(SumsubTransactionOwnershipResolutionResponse.class);

        when(apiService.rejectTransactionOwnership(credentials, "sumsub_transaction_id")).thenReturn(resolutionResponse);
        doThrow(thrownException).when(validator).validateSumsubTransactionOwnershipResolutionResponse(event, resolutionResponse);

        boolean response = sumsubService.handleTransferResolved(credentials, event);

        assertFalse(response);
        verifyNoMoreInteractions(apiService);
        verifyNoInteractions(extensionContext);
    }

    @Test
    void testConfirmWalletOwnership_transferNotFound() {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.APPROVED);

            when(extensionContext.findTravelRuleTransferByPublicId("transfer_public_id")).thenReturn(null);

            boolean response = sumsubService.handleTransferResolved(credentials, event);

            assertFalse(response);
            mapperMock.verify(() -> SumsubTravelRuleApiMapper.toSumsubConfirmWalletOwnershipRequest(any()), never());
            verify(apiService, never()).confirmWalletOwnership(any(), anyString(), any());
            verify(validator, never()).validateSumsubConfirmWalletOwnershipResponse(any(), any());
        }
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testConfirmWalletOwnership_apiServiceException(Exception thrownException) {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.APPROVED);

            ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
            SumsubConfirmWalletOwnershipRequest confirmWalletOwnershipRequest = mock(SumsubConfirmWalletOwnershipRequest.class);

            when(extensionContext.findTravelRuleTransferByPublicId("transfer_public_id")).thenReturn(transferData);
            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubConfirmWalletOwnershipRequest(transferData))
                    .thenReturn(confirmWalletOwnershipRequest);
            when(apiService.confirmWalletOwnership(credentials, "sumsub_transaction_id", confirmWalletOwnershipRequest))
                    .thenThrow(thrownException);

            boolean response = sumsubService.handleTransferResolved(credentials, event);

            assertFalse(response);
            verify(validator, never()).validateSumsubConfirmWalletOwnershipResponse(any(), any());
        }
    }

    @ParameterizedTest
    @MethodSource("exception_arguments")
    void testConfirmWalletOwnership_validatorException(Exception thrownException) {
        try (MockedStatic<SumsubTravelRuleApiMapper> mapperMock = mockStatic(SumsubTravelRuleApiMapper.class)) {
            ITravelRuleTransferResolvedEvent event = createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus.APPROVED);

            ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
            SumsubConfirmWalletOwnershipRequest confirmWalletOwnershipRequest = mock(SumsubConfirmWalletOwnershipRequest.class);
            SumsubTransactionInformationResponse confirmWalletOwnershipResponse = mock(SumsubTransactionInformationResponse.class);

            when(extensionContext.findTravelRuleTransferByPublicId("transfer_public_id")).thenReturn(transferData);
            mapperMock.when(() -> SumsubTravelRuleApiMapper.toSumsubConfirmWalletOwnershipRequest(transferData))
                    .thenReturn(confirmWalletOwnershipRequest);
            when(apiService.confirmWalletOwnership(credentials, "sumsub_transaction_id", confirmWalletOwnershipRequest))
                    .thenReturn(confirmWalletOwnershipResponse);
            doThrow(thrownException).when(validator).validateSumsubConfirmWalletOwnershipResponse(event, confirmWalletOwnershipResponse);

            boolean response = sumsubService.handleTransferResolved(credentials, event);

            assertFalse(response);
        }
    }

    private ITravelRuleTransferResolvedEvent createITravelRuleTransferResolvedEvent(TravelRuleProviderTransferStatus status) {
        return new ITravelRuleTransferResolvedEvent() {
            @Override
            public String getTransferPublicId() {
                return "transfer_public_id";
            }

            @Override
            public String getTransferExternalId() {
                return "sumsub_transaction_id";
            }

            @Override
            public TravelRuleProviderTransferStatus getResolvedStatus() {
                return status;
            }
        };
    }

}