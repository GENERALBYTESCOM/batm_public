package com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubApplicant;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubCounterparty;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubInstitutionInfo;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubPaymentMethod;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class SumsubWebhookValidatorTest {

    private final SumsubWebhookValidator validator = new SumsubWebhookValidator();

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    void testValidateSumsubWebhookMessage_type(String type) {
        SumsubWebhookMessage message = mock(SumsubWebhookMessage.class);
        when(message.getType()).thenReturn(type);
        when(message.getKytDataTxnId()).thenReturn("transfer_public_id");
        when(message.getKytTxnId()).thenReturn("sumsub_id");

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubWebhookMessage(message)
        );

        assertEquals("Sumsub incoming message does not contain type. Txn ID: transfer_public_id, Sumsub transaction ID: sumsub_id",
                exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    void testValidateSumsubWebhookMessage_kytDataTxnId(String kytDataTxnId) {
        SumsubWebhookMessage message = mock(SumsubWebhookMessage.class);
        when(message.getType()).thenReturn("type");
        when(message.getKytDataTxnId()).thenReturn(kytDataTxnId);
        when(message.getKytTxnId()).thenReturn("sumsub_id");

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSumsubWebhookMessage(message)
        );

        assertEquals("Sumsub incoming message does not contain kytDataTxnId, unable to pair transfer. Sumsub transaction ID: sumsub_id",
                exception.getMessage());
    }

    @Test
    void testValidateTransactionInformationResponse_applicant() {
        SumsubTransactionInformationResponse response = createSumsubTransactionInformationResponse();
        response.getData().setApplicant(null);

        SumsubWebhookMessage message = getMockedSumsubWebhookMessage();

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateTransactionInformationResponse(response, message)
        );

        assertEquals("The transaction data obtained from Sumsub is not valid, 'data.applicant' object is null."
                + " Txn ID: transfer_public_id, Sumsub transaction ID: sumsub_id", exception.getMessage());
    }

    @Test
    void testValidateTransactionInformationResponse_applicant_institutionInfo() {
        SumsubTransactionInformationResponse response = createSumsubTransactionInformationResponse();
        response.getData().getApplicant().setInstitutionInfo(null);

        SumsubWebhookMessage message = getMockedSumsubWebhookMessage();

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateTransactionInformationResponse(response, message)
        );

        assertEquals("The transaction data obtained from Sumsub is not valid, 'data.applicant.institutionInfo' object is null."
                + " Txn ID: transfer_public_id, Sumsub transaction ID: sumsub_id", exception.getMessage());
    }

    @Test
    void testValidateTransactionInformationResponse_counterparty() {
        SumsubTransactionInformationResponse response = createSumsubTransactionInformationResponse();
        response.getData().setCounterparty(null);

        SumsubWebhookMessage message = getMockedSumsubWebhookMessage();

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateTransactionInformationResponse(response, message)
        );

        assertEquals("The transaction data obtained from Sumsub is not valid, 'data.counterparty' object is null."
                + " Txn ID: transfer_public_id, Sumsub transaction ID: sumsub_id", exception.getMessage());
    }

    @Test
    void testValidateTransactionInformationResponse_counterparty_institutionInfo() {
        SumsubTransactionInformationResponse response = createSumsubTransactionInformationResponse();
        response.getData().getCounterparty().setInstitutionInfo(null);

        SumsubWebhookMessage message = getMockedSumsubWebhookMessage();

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateTransactionInformationResponse(response, message)
        );

        assertEquals("The transaction data obtained from Sumsub is not valid, 'data.counterparty.institutionInfo' object is null."
                + " Txn ID: transfer_public_id, Sumsub transaction ID: sumsub_id", exception.getMessage());
    }

    @Test
    void testValidateTransactionInformationResponse_counterparty_paymentMethod() {
        SumsubTransactionInformationResponse response = createSumsubTransactionInformationResponse();
        response.getData().getCounterparty().setPaymentMethod(null);

        SumsubWebhookMessage message = getMockedSumsubWebhookMessage();

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateTransactionInformationResponse(response, message)
        );

        assertEquals("The transaction data obtained from Sumsub is not valid, 'data.counterparty.paymentMethod' object is null."
                + " Txn ID: transfer_public_id, Sumsub transaction ID: sumsub_id", exception.getMessage());
    }

    @Test
    void testValidateSignature() {
        SumsubWebhookRequest request = new SumsubWebhookRequest(
                "vasp_did",
                "HMAC_SHA512_HEX",
                "7b88f980882c95e2be7cf49e55f69804e6b53e664f33181b965e77b73eca408b"
                        + "aa2a7bb5ff0e47d77127bc1089b54e43d68f6b4564f15baba769b491475c5f2e",
                "message"
        );

        assertDoesNotThrow(() -> validator.validateSignature(request, "secret_key"));
    }

    @Test
    void testValidateSignature_invalidSignature() {
        SumsubWebhookRequest request = new SumsubWebhookRequest(
                "vasp_did",
                "HMAC_SHA512_HEX",
                "invalid_payload_digest",
                "message"
        );

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSignature(request, "secret_key")
        );

        assertEquals("The received digest does not match the signature of the received payload.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    void testValidateSignature_message_blank(String message) {
        SumsubWebhookRequest request = new SumsubWebhookRequest(
                "vasp_did",
                "HMAC_SHA512_HEX",
                "payload_digest",
                message
        );

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSignature(request, "secret_key")
        );

        assertEquals("Received Sumsub webhook does not contain a message payload.", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    void testValidateSignature_digestAlgorithm_blank(String digestAlgorithm) {
        SumsubWebhookRequest request = new SumsubWebhookRequest(
                "vasp_did",
                digestAlgorithm,
                "payload_digest",
                "message"
        );

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSignature(request, "secret_key")
        );

        assertEquals(
                "Digest algorithm is not present in HTTP request header. Check signature settings in Sumsub Webhook Manager.",
                exception.getMessage()
        );
    }

    @Test
    void testValidateSignature_digestAlgorithm_unsupportedAlgorithm() {
        SumsubWebhookRequest request = new SumsubWebhookRequest(
                "vasp_did",
                "unsupported_digest_algorithm",
                "payload_digest",
                "message"
        );

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSignature(request, "secret_key")
        );

        assertEquals(
                "Digest algorithm 'unsupported_digest_algorithm' is not supported. Use SHA-512 algorithm in Sumsub Webhook Manager.",
                exception.getMessage()
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    void testValidateSignature_payloadDigest_blank(String payloadDigest) {
        SumsubWebhookRequest request = new SumsubWebhookRequest(
                "vasp_did",
                "HMAC_SHA512_HEX",
                payloadDigest,
                "message"
        );

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> validator.validateSignature(request, "secret_key")
        );

        assertEquals("Payload digest is not present in HTTP request header.", exception.getMessage());
    }

    @Test
    void testValidateSignature_mac_algorithmNotFound() {
        try (MockedStatic<Mac> macMock = mockStatic(Mac.class)) {
            SumsubWebhookRequest request = new SumsubWebhookRequest(
                    "vasp_did",
                    "HMAC_SHA512_HEX",
                    "7b88f980882c95e2be7cf49e55f69804e6b53e664f33181b965e77b73eca408b"
                            + "aa2a7bb5ff0e47d77127bc1089b54e43d68f6b4564f15baba769b491475c5f2e",
                    "message"
            );

            macMock.when(() -> Mac.getInstance("HmacSHA512"))
                    .thenThrow(new NoSuchAlgorithmException("test-validate-signature-mac-algorithm-not-found-exception"));

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> validator.validateSignature(request, "secret_key")
            );

            assertEquals("HMAC algorithm 'HmacSHA512' is not available.", exception.getMessage());
        }
    }

    @Test
    void testValidateSignature_mac_invalidKeyException() throws Exception {
        try (MockedStatic<Mac> macMock = mockStatic(Mac.class)) {
            SumsubWebhookRequest request = new SumsubWebhookRequest(
                    "vasp_did",
                    "HMAC_SHA512_HEX",
                    "7b88f980882c95e2be7cf49e55f69804e6b53e664f33181b965e77b73eca408b"
                            + "aa2a7bb5ff0e47d77127bc1089b54e43d68f6b4564f15baba769b491475c5f2e",
                    "message"
            );

            Mac mac = mock(Mac.class);
            macMock.when(() -> Mac.getInstance("HmacSHA512")).thenReturn(mac);
            doThrow(new InvalidKeyException("test-validate-signature-mac-invalid-key-exception")).when(mac).init(any(SecretKeySpec.class));

            TravelRuleProviderException exception = assertThrows(
                    TravelRuleProviderException.class, () -> validator.validateSignature(request, "secret_key")
            );

            assertEquals(
                    "Failed to initialize Mac for verifying signature. Check if the private key is set in the Sumsub provider settings.",
                    exception.getMessage()
            );
        }
    }

    private SumsubWebhookMessage getMockedSumsubWebhookMessage() {
        SumsubWebhookMessage message = mock(SumsubWebhookMessage.class);
        when(message.getKytDataTxnId()).thenReturn("transfer_public_id");
        when(message.getKytTxnId()).thenReturn("sumsub_id");

        return message;
    }

    private static SumsubTransactionInformationResponse createSumsubTransactionInformationResponse() {
        SumsubApplicant applicant = new SumsubApplicant();
        applicant.setInstitutionInfo(new SumsubInstitutionInfo());

        SumsubCounterparty counterparty = new SumsubCounterparty();
        counterparty.setInstitutionInfo(new SumsubInstitutionInfo());
        counterparty.setPaymentMethod(new SumsubPaymentMethod());

        SumsubTransactionInformationResponse.TransactionData transactionData = new SumsubTransactionInformationResponse.TransactionData();
        transactionData.setApplicant(applicant);
        transactionData.setCounterparty(counterparty);

        SumsubTransactionInformationResponse response = new SumsubTransactionInformationResponse();
        response.setData(transactionData);

        return response;
    }

}