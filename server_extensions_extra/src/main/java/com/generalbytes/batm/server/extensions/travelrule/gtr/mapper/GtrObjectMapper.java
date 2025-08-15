package com.generalbytes.batm.server.extensions.travelrule.gtr.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101Payload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper used to map objects from/to JSON format.
 */
@Slf4j
@AllArgsConstructor
public class GtrObjectMapper {

    private final ObjectMapper objectMapper;

    /**
     * Serialize {@link GtrIvms101Payload} to {@link String}.
     *
     * @param payload {@link GtrIvms101Payload} to serialize.
     * @return Serialized {@link GtrIvms101Payload} as {@link String}.
     */
    public String serializeIvms101Payload(GtrIvms101Payload payload) {
        return serialize(payload, "Failed to serialize GTR IVMS101 payload object to JSON string.");
    }

    /**
     * Serialize {@link GtrWebhookMessage} to {@link String}.
     *
     * @param webhookMessage {@link GtrWebhookMessage} to serialize.
     * @return Serialized {@link GtrWebhookMessage} as {@link String}.
     */
    public String serializeGtrWebhookMessage(GtrWebhookMessage webhookMessage) {
        return serialize(webhookMessage, "Failed to serialize GTR Webhook Message object to JSON string.");
    }

    private String serialize(Object object, String errorMessage) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(errorMessage, e);
            throw new TravelRuleProviderException(errorMessage);
        }
    }

    /**
     * Deserialize {@link String} to {@link GtrIvms101Payload}.
     *
     * @param ivms101Payload {@link String} to deserialize.
     * @return Deserialized {@link GtrIvms101Payload} from {@link String}.
     */
    public GtrIvms101Payload deserializeIvms101Payload(String ivms101Payload) {
        try {
            return objectMapper.readValue(ivms101Payload, GtrIvms101Payload.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize GTR IVMS101 object from JSON string.", e);
            throw new TravelRuleProviderException("Failed to deserialize GTR IVMS101 object from JSON string.");
        }
    }

}
