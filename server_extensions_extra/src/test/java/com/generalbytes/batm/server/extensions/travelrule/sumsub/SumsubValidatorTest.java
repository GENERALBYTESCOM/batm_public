package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferResolvedEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.submittransaction.SumsubSubmitTxWithoutApplicantRequest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactionownershipresolution.SumsubTransactionOwnershipResolutionResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.updatetransactionhash.SumsubUpdateTransactionHashResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SumsubValidatorTest {

    private final SumsubValidator validator = new SumsubValidator();

    @Test
    void testValidateSubmitTxWithoutApplicantResponse() {
        SumsubSubmitTxWithoutApplicantRequest request = mock(SumsubSubmitTxWithoutApplicantRequest.class);
        when(request.getTxnId()).thenReturn("txn_id");

        SumsubTransactionInformationResponse.TransactionData transactionData
                = mock(SumsubTransactionInformationResponse.TransactionData.class);
        when(transactionData.getTxnId()).thenReturn("txn_id");

        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("sumsub_id");
        when(response.getData()).thenReturn(transactionData);

        assertDoesNotThrow(() -> validator.validateSubmitTxWithoutApplicantResponse(request, response));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateSubmitTxWithoutApplicantResponse_invalid_responseId(String sumsubId) {
        SumsubSubmitTxWithoutApplicantRequest request = mock(SumsubSubmitTxWithoutApplicantRequest.class);

        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn(sumsubId);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSubmitTxWithoutApplicantResponse(request, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSubmitTxWithoutApplicantResponse_invalid_dataObject() {
        SumsubSubmitTxWithoutApplicantRequest request = mock(SumsubSubmitTxWithoutApplicantRequest.class);

        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("sumsub_id");
        when(response.getData()).thenReturn(null);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSubmitTxWithoutApplicantResponse(request, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    private static Stream<Arguments> ids_notMatch_arguments() {
        return Stream.of(
                arguments("request_id", "response_id"),
                arguments("request_id", ""),
                arguments("request_id", "  "),
                arguments("request_id", null)
        );
    }

    @ParameterizedTest
    @MethodSource("ids_notMatch_arguments")
    void testValidateSubmitTxWithoutApplicantResponse_txnIds_notMatch(String requestTxnId, String responseTxnId) {
        SumsubSubmitTxWithoutApplicantRequest request = mock(SumsubSubmitTxWithoutApplicantRequest.class);
        when(request.getTxnId()).thenReturn(requestTxnId);

        SumsubTransactionInformationResponse.TransactionData transactionData
                = mock(SumsubTransactionInformationResponse.TransactionData.class);
        when(transactionData.getTxnId()).thenReturn(responseTxnId);

        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("sumsub_id");
        when(response.getData()).thenReturn(transactionData);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSubmitTxWithoutApplicantResponse(request, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateSumsubUpdateTransactionHashResponse_invalid_responseId(String sumsubId) {
        ITravelRuleTransferUpdateRequest request = mock(ITravelRuleTransferUpdateRequest.class);

        SumsubUpdateTransactionHashResponse response = mock(SumsubUpdateTransactionHashResponse.class);
        when(response.getId()).thenReturn(sumsubId);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubUpdateTransactionHashResponse(request, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("ids_notMatch_arguments")
    void testValidateSumsubUpdateTransactionHashResponse_sumsubIds_notMatch(String requestSumsubId, String responseSumsubId) {
        ITravelRuleTransferUpdateRequest request = mock(ITravelRuleTransferUpdateRequest.class);
        when(request.getId()).thenReturn(requestSumsubId);

        SumsubUpdateTransactionHashResponse response = mock(SumsubUpdateTransactionHashResponse.class);
        when(response.getId()).thenReturn(responseSumsubId);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubUpdateTransactionHashResponse(request, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSumsubUpdateTransactionHashResponse_invalid_dataObject() {
        ITravelRuleTransferUpdateRequest request = mock(ITravelRuleTransferUpdateRequest.class);
        when(request.getId()).thenReturn("sumsub_id");

        SumsubUpdateTransactionHashResponse response = mock(SumsubUpdateTransactionHashResponse.class);
        when(response.getId()).thenReturn("sumsub_id");
        when(response.getData()).thenReturn(null);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubUpdateTransactionHashResponse(request, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("ids_notMatch_arguments")
    void testValidateSumsubUpdateTransactionHashResponse_txnIds_notMatch(String requestTxnId, String responseTxnId) {
        ITravelRuleTransferUpdateRequest request = mock(ITravelRuleTransferUpdateRequest.class);
        when(request.getId()).thenReturn("sumsub_id");
        when(request.getPublicId()).thenReturn(requestTxnId);

        SumsubUpdateTransactionHashResponse.TransactionData transactionData
                = mock(SumsubUpdateTransactionHashResponse.TransactionData.class);
        when(transactionData.getTxnId()).thenReturn(responseTxnId);

        SumsubUpdateTransactionHashResponse response = mock(SumsubUpdateTransactionHashResponse.class);
        when(response.getId()).thenReturn("sumsub_id");
        when(response.getData()).thenReturn(transactionData);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubUpdateTransactionHashResponse(request, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSumsubTransactionInformationResponse() {
        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("sumsub_id");
        when(response.getData()).thenReturn(mock(SumsubTransactionInformationResponse.TransactionData.class));

        assertDoesNotThrow(() -> validator.validateSumsubTransactionInformationResponse("sumsub_id", response));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateSumsubTransactionInformationResponse_invalid_responseId(String sumsubId) {
        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn(sumsubId);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubTransactionInformationResponse("sumsub_id", response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSumsubTransactionInformationResponse_invalid_dataObject() {
        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("sumsub_id");
        when(response.getData()).thenReturn(null);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubTransactionInformationResponse("sumsub_id", response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSumsubTransactionInformationResponse_invalid_txnIds_notMatch() {
        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("response_sumsub_id");
        when(response.getData()).thenReturn(mock(SumsubTransactionInformationResponse.TransactionData.class));

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class,
                () -> validator.validateSumsubTransactionInformationResponse("request_sumsub_id", response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSumsubTransactionOwnershipResolutionResponse() {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);
        when(event.getTransferExternalId()).thenReturn("sumsub_id");

        SumsubTransactionOwnershipResolutionResponse response = mock(SumsubTransactionOwnershipResolutionResponse.class);
        when(response.getId()).thenReturn("sumsub_id");

        assertDoesNotThrow(() -> validator.validateSumsubTransactionOwnershipResolutionResponse(event, response));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateSumsubTransactionOwnershipResolutionResponse_invalid_responseId(String sumsubId) {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);

        SumsubTransactionOwnershipResolutionResponse response = mock(SumsubTransactionOwnershipResolutionResponse.class);
        when(response.getId()).thenReturn(sumsubId);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubTransactionOwnershipResolutionResponse(event, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSumsubTransactionOwnershipResolutionResponse_invalid_txnIds_notMatch() {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);
        when(event.getTransferExternalId()).thenReturn("request_sumsub_id");

        SumsubTransactionOwnershipResolutionResponse response = mock(SumsubTransactionOwnershipResolutionResponse.class);
        when(response.getId()).thenReturn("response_sumsub_id");

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class,
                () -> validator.validateSumsubTransactionOwnershipResolutionResponse(event, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSumsubConfirmWalletOwnershipResponse() {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);
        when(event.getTransferExternalId()).thenReturn("sumsub_id");

        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("sumsub_id");

        assertDoesNotThrow(() -> validator.validateSumsubConfirmWalletOwnershipResponse(event, response));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateSumsubConfirmWalletOwnershipResponse_invalid_responseId(String sumsubId) {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);

        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn(sumsubId);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubConfirmWalletOwnershipResponse(event, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

    @Test
    void testValidateSumsubConfirmWalletOwnershipResponse_invalid_txnIds_notMatch() {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);
        when(event.getTransferExternalId()).thenReturn("request_sumsub_id");

        SumsubTransactionInformationResponse response = mock(SumsubTransactionInformationResponse.class);
        when(response.getId()).thenReturn("response_sumsub_id");

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class,
                () -> validator.validateSumsubConfirmWalletOwnershipResponse(event, response)
        );

        assertEquals("invalid response", exception.getMessage());
    }

}