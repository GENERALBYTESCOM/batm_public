package com.generalbytes.batm.server.extensions.travelrule.gtr.mapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101Payload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrObjectMapperTest {

    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private GtrObjectMapper gtrObjectMapper;

    @Test
    void testSerializeIvms101Payload() throws JsonProcessingException {
        GtrIvms101Payload payload = mock(GtrIvms101Payload.class);

        when(objectMapper.writeValueAsString(payload)).thenReturn("serialized_object");

        String serializedPayload = gtrObjectMapper.serializeIvms101Payload(payload);

        assertEquals("serialized_object", serializedPayload);
    }

    @Test
    void testSerializeIvms101Payload_exception() throws JsonProcessingException {
        GtrIvms101Payload payload = mock(GtrIvms101Payload.class);

        when(objectMapper.writeValueAsString(payload)).thenThrow(new JsonParseException(null, "test-serialize-ivms101-payload-exception"));

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrObjectMapper.serializeIvms101Payload(payload)
        );

        assertEquals("Failed to serialize GTR IVMS101 payload object to JSON string.", exception.getMessage());
    }

    @Test
    void testSerializeGtrWebhookMessage() throws JsonProcessingException {
        GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);

        when(objectMapper.writeValueAsString(webhookMessage)).thenReturn("serialized_object");

        String serializedWebhookMessage = gtrObjectMapper.serializeGtrWebhookMessage(webhookMessage);

        assertEquals("serialized_object", serializedWebhookMessage);
    }

    @Test
    void testSerializeGtrWebhookMessage_exception() throws JsonProcessingException {
        GtrWebhookMessage webhookMessage = mock(GtrWebhookMessage.class);

        when(objectMapper.writeValueAsString(webhookMessage))
                .thenThrow(new JsonParseException(null, "test-serialize-webhook-message-exception"));

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrObjectMapper.serializeGtrWebhookMessage(webhookMessage)
        );

        assertEquals("Failed to serialize GTR Webhook Message object to JSON string.", exception.getMessage());
    }

    @Test
    void testDeserializeIvms101Payload() throws JsonProcessingException {
        GtrIvms101Payload payload = mock(GtrIvms101Payload.class);

        when(objectMapper.readValue("payload", GtrIvms101Payload.class)).thenReturn(payload);

        GtrIvms101Payload resultPayload = gtrObjectMapper.deserializeIvms101Payload("payload");

        assertSame(payload, resultPayload);
    }

    @Test
    void testDeserializeIvms101Payload_exception() throws JsonProcessingException {
        when(objectMapper.readValue("payload", GtrIvms101Payload.class))
                .thenThrow(new JsonParseException(null, "test-deserialize-ivms101-payload-exception"));

        TravelRuleProviderException exception = assertThrows(
                TravelRuleProviderException.class, () -> gtrObjectMapper.deserializeIvms101Payload("payload")
        );

        assertEquals("Failed to deserialize GTR IVMS101 object from JSON string.", exception.getMessage());
    }

}