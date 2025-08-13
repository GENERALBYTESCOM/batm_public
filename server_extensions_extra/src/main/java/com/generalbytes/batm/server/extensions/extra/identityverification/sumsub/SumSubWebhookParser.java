package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.CustomObjectMapperFactory;

import jakarta.ws.rs.core.Response.Status;

/**
 * The SumSubWebhookParser class is responsible for parsing webhook payloads related to SumSub identity verification.
 * It utilizes a custom-configured Jackson ObjectMapper for JSON deserialization.
 */
public class SumSubWebhookParser {
    private static final ObjectMapper objectMapper;

    static {
        CustomObjectMapperFactory tmpFactory = new CustomObjectMapperFactory();
        objectMapper = tmpFactory.createObjectMapper();
    }

    /**
     * Parses the given raw JSON payload into an object of the specified type.
     * This method uses a custom-configured Jackson ObjectMapper for deserialization.
     *
     * @param rawPayload the raw JSON payload as a string
     * @param valueType the class type to which the JSON payload should be mapped
     * @param <T> the type of the object to be returned
     * @return the deserialized object of the specified type
     * @throws IdentityCheckWebhookException if the parsing fails due to invalid JSON or deserialization issues
     */
    public <T> T parse(String rawPayload, Class<T> valueType) throws IdentityCheckWebhookException {
        try {
            return objectMapper.readValue(rawPayload, valueType);
        } catch (JsonProcessingException e) {
            throw new IdentityCheckWebhookException(Status.BAD_REQUEST.getStatusCode(), "Failed to parse request data", rawPayload, e);
        }
    }
}
