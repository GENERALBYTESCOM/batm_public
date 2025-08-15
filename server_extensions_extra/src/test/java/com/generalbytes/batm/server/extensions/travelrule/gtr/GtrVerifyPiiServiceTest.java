package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101Payload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrTransferHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrVerifyPiiMapper;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.Curve25519Encryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrVerifyPiiServiceTest {

    @Mock
    private GtrApiWrapper apiWrapper;
    @Mock
    private Curve25519Encryptor curve25519Encryptor;
    @Mock
    private GtrObjectMapper objectMapper;
    @Mock
    private GtrTransferHandler transferHandler;
    @Mock
    private GtrProviderRegistry providerRegistry;
    @InjectMocks
    private GtrVerifyPiiService verifyPiiService;

    @Test
    void testVerifyPii() {
        try (MockedStatic<GtrVerifyPiiMapper> verifyPiiMapperMock = mockStatic(GtrVerifyPiiMapper.class)) {
            ITravelRuleTransferData data = mock(ITravelRuleTransferData.class);
            when(data.getBeneficiaryVasp()).thenReturn(mock(ITravelRuleVasp.class));

            GtrCredentials credentials = createGtrCredentials("self_public_key", "self_private_key");
            GtrIvms101Payload ivms101Payload = mock(GtrIvms101Payload.class);
            GtrVerifyPiiRequest request = mock(GtrVerifyPiiRequest.class);
            GtrVerifyPiiResponse expectedResponse = mock(GtrVerifyPiiResponse.class);

            verifyPiiMapperMock.when(() -> GtrVerifyPiiMapper.toGtrIvms101Payload(data)).thenReturn(ivms101Payload);
            when(objectMapper.serializeIvms101Payload(ivms101Payload)).thenReturn("json_as_string");
            when(curve25519Encryptor.encrypt("json_as_string", "target_vasp_public_key", "self_private_key"))
                    .thenReturn("encrypted_payload");
            verifyPiiMapperMock.when(() -> GtrVerifyPiiMapper.toGtrVerifyPiiRequest(
                            data, "request_id", "self_public_key", "target_vasp_public_key", "encrypted_payload")
                    ).thenReturn(request);
            when(apiWrapper.verifyPii(credentials, request)).thenReturn(expectedResponse);

            GtrVerifyPiiResponse response = verifyPiiService.verifyPii(credentials, data, "request_id", "target_vasp_public_key");

            assertEquals(expectedResponse, response);
        }
    }

    @Test
    void testProcessVerifyPiiWebhookMessage() {
        GtrCredentials credentials = createGtrCredentials(null, "curve_private_key");
        GtrIvms101Payload ivms101Payload = mock(GtrIvms101Payload.class);
        GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);
        GtrProvider gtrProvider = Mockito.mock(GtrProvider.class);

        when(webhookMessage.getInvokeVaspCode()).thenReturn("invoke_vasp_code");
        when(providerRegistry.get("invoke_vasp_code")).thenReturn(gtrProvider);
        when(gtrProvider.getCredentials()).thenReturn(credentials);
        when(curve25519Encryptor.decrypt("encrypted_payload", "initiator_public_key", "curve_private_key"))
                .thenReturn("{\"ivms101\":null}");
        when(objectMapper.deserializeIvms101Payload("{\"ivms101\":null}")).thenReturn(ivms101Payload);
        when(objectMapper.serializeGtrWebhookMessage(webhookMessage)).thenReturn("raw_data");

        GtrPiiVerifyWebhookPayload payload = createGtrPiiVerifyWebhookPayload();

        verifyPiiService.processVerifyPiiWebhookMessage(webhookMessage, payload);

        verify(transferHandler, times(1)).handleVerifyPiiWebhookMessage(payload, ivms101Payload, "raw_data");
    }

    @Test
    void testProcessVerifyPiiWebhookMessage_providerNotFound() {
        GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);
        when(webhookMessage.getInvokeVaspCode()).thenReturn("invoke_vasp_code");

        when(providerRegistry.get("invoke_vasp_code")).thenReturn(null);

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> verifyPiiService.processVerifyPiiWebhookMessage(webhookMessage, null)
        );

        assertEquals("GTR provider with VASP DID 'invoke_vasp_code' not found", exception.getMessage());
    }

    private GtrPiiVerifyWebhookPayload createGtrPiiVerifyWebhookPayload() {
        GtrPiiVerifyWebhookPayload payload = new GtrPiiVerifyWebhookPayload();
        payload.setEncryptedPayload("encrypted_payload");
        payload.setInitiatorPublicKey("initiator_public_key");

        return payload;
    }

    private GtrCredentials createGtrCredentials(String curvePublicKey, String curvePrivateKey) {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        when(credentials.getPublicKey()).thenReturn(curvePublicKey);
        when(credentials.getPrivateKey()).thenReturn(curvePrivateKey);

        return new GtrCredentials(credentials, clientSecret -> null);
    }

}