package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.IIdentityWalletEvaluationRequest;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GtrValidatorTest {

    private final GtrValidator gtrValidator = new GtrValidator();

    @ParameterizedTest
    @ValueSource(strings = { "vc", "vaspCode", "vasp_code", "VASPCODE" })
    void testValidateVaspCode_valid(String vaspCode) {
        assertDoesNotThrow(() -> gtrValidator.validateVaspCode(vaspCode));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVaspCode_invalid(String vaspCode) {
        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateVaspCode(vaspCode)
        );

        assertEquals("invalid input, VASP code is blank", exception.getMessage());
    }

    @Test
    void testValidateRegisterTravelRuleResponse_valid() {
        GtrRegisterTravelRuleResponse response = createGtrRegisterTravelRuleResponse("request_id");

        assertDoesNotThrow(() -> gtrValidator.validateRegisterTravelRuleResponse(response, "request_id"));
    }

    @Test
    void testValidateRegisterTravelRuleResponse_invalid() {
        GtrRegisterTravelRuleResponse response = createGtrRegisterTravelRuleResponse("invalid_request_id");

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateRegisterTravelRuleResponse(response, "request_id")
        );

        assertEquals("invalid response", exception.getMessage());
    }

    private GtrRegisterTravelRuleResponse createGtrRegisterTravelRuleResponse(String requestId) {
        GtrRegisterTravelRuleResponse response = mock(GtrRegisterTravelRuleResponse.class);
        when(response.getRequestId()).thenReturn(requestId);

        return response;
    }

    @Test
    void testValidateWalletEvaluationRequest_valid() {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);
        when(request.getCryptoAddress()).thenReturn("cryptoAddress");
        when(request.getCryptocurrency()).thenReturn("BTC");
        when(request.getDidOfVaspHostingCustodialWallet()).thenReturn("hostingVaspDid");

        assertDoesNotThrow(() -> gtrValidator.validateWalletEvaluationRequest(request));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyAddressData_invalid_cryptoAddress(String invalidCryptoAddress) {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);
        when(request.getCryptoAddress()).thenReturn(invalidCryptoAddress);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateWalletEvaluationRequest(request)
        );

        assertEquals("GTR data for address verification is not valid - crypto address is blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyAddressData_invalid_cryptocurrency(String invalidCryptocurrency) {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);
        when(request.getCryptoAddress()).thenReturn("cryptoAddress");
        when(request.getCryptocurrency()).thenReturn(invalidCryptocurrency);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateWalletEvaluationRequest(request)
        );

        assertEquals("GTR data for address verification is not valid - cryptocurrency is blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyAddressData_invalid_didOfVaspHostingCustodialWallet(String invalidDidOfVaspHostingCustodialWallet) {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);
        when(request.getCryptoAddress()).thenReturn("cryptoAddress");
        when(request.getCryptocurrency()).thenReturn("BTC");
        when(request.getDidOfVaspHostingCustodialWallet()).thenReturn(invalidDidOfVaspHostingCustodialWallet);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateWalletEvaluationRequest(request)
        );

        assertEquals(
                "GTR data for address verification is not valid - DID of VASP hosting custodial wallet is null", exception.getMessage()
        );
    }

    @Test
    void testValidateGtrWebhookMessage_valid() {
        GtrWebhookMessage message = new GtrWebhookMessage();
        message.setInvokeVaspCode("vasp_code");

        assertDoesNotThrow(() -> gtrValidator.validateGtrWebhookMessage(message));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateGtrWebhookMessage_invalid_vaspCode(String vaspCode) {
        GtrWebhookMessage message = new GtrWebhookMessage();
        message.setInvokeVaspCode(vaspCode);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateGtrWebhookMessage(message)
        );

        assertEquals("GTR webhook payload for PII verification is not valid - invoke VASP code is blank", exception.getMessage());
    }

    @Test
    void testValidateVerifyPiiIncomingMessage_valid() {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();

        assertDoesNotThrow(() -> gtrValidator.validateVerifyPiiIncomingMessage(payload));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyPiiIncomingMessage_invalid_requestId(String requestId) {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();
        when(payload.getRequestId()).thenReturn(requestId);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateVerifyPiiIncomingMessage(payload)
        );

        assertEquals("GTR webhook payload for PII verification is not valid - request ID is blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyPiiIncomingMessage_invalid_address(String address) {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();
        when(payload.getAddress()).thenReturn(address);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateVerifyPiiIncomingMessage(payload)
        );

        assertEquals("GTR webhook payload for PII verification is not valid - address is blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyPiiIncomingMessage_invalid_encryptedPayload(String encryptedPayload) {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();
        when(payload.getEncryptedPayload()).thenReturn(encryptedPayload);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateVerifyPiiIncomingMessage(payload)
        );

        assertEquals("GTR webhook payload for PII verification is not valid - encrypted payload is blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyPiiIncomingMessage_invalid_originatorVasp(String originatorVasp) {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();
        when(payload.getOriginatorVasp()).thenReturn(originatorVasp);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateVerifyPiiIncomingMessage(payload)
        );

        assertEquals("GTR webhook payload for PII verification is not valid - originator VASP is blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyPiiIncomingMessage_invalid_beneficiaryVasp(String beneficiaryVasp) {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();
        when(payload.getBeneficiaryVasp()).thenReturn(beneficiaryVasp);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateVerifyPiiIncomingMessage(payload)
        );

        assertEquals("GTR webhook payload for PII verification is not valid - beneficiary VASP is blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyPiiIncomingMessage_invalid_initiatorPublicKey(String initiatorPublicKey) {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();
        when(payload.getInitiatorPublicKey()).thenReturn(initiatorPublicKey);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateVerifyPiiIncomingMessage(payload)
        );

        assertEquals("GTR webhook payload for PII verification is not valid - initiator public key is blank", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testValidateVerifyPiiIncomingMessage_invalid_receiverPublicKey(String receiverPublicKey) {
        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();
        when(payload.getReceiverPublicKey()).thenReturn(receiverPublicKey);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrValidator.validateVerifyPiiIncomingMessage(payload)
        );

        assertEquals("GTR webhook payload for PII verification is not valid - receiver public key is blank", exception.getMessage());
    }

    private GtrPiiVerifyWebhookPayload createGtrPiiVerifyWebhookPayload() {
        GtrPiiVerifyWebhookPayload payload = mock(GtrPiiVerifyWebhookPayload.class);
        lenient().when(payload.getRequestId()).thenReturn("request_id");
        lenient().when(payload.getAddress()).thenReturn("address");
        lenient().when(payload.getEncryptedPayload()).thenReturn("encrypted_payload");
        lenient().when(payload.getOriginatorVasp()).thenReturn("originator_vasp");
        lenient().when(payload.getBeneficiaryVasp()).thenReturn("beneficiary_vasp");
        lenient().when(payload.getInitiatorPublicKey()).thenReturn("initiator_public_key");
        lenient().when(payload.getReceiverPublicKey()).thenReturn("receiver_public_key");

        return payload;
    }

}