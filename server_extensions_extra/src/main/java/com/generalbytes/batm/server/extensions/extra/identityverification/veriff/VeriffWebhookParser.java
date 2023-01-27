package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationEventWebhookRequest;
import com.generalbytes.batm.server.services.web.IdentityCheckWebhookException;

import javax.ws.rs.core.Response;
import java.util.Map;

public class VeriffWebhookParser {
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public String getApplicantId(String rawPayload) throws IdentityCheckWebhookException {
        return apply(rawPayload,
            (raw, parsed) -> parsed.getApplicantId(),
            (raw, parsed) -> parsed.getApplicantId());
    }

    /**
     * calls `ifDecision` consumer if the raw payload matches the Decision webhook request payload type
     * and `ifEvent` if it matches the Event webhook type
     *
     * @throws IdentityCheckWebhookException in case the ifDecision or ifEvent throws IdentityCheckWebhookException exception
     *                                       or when the request cannot be parsed, or it does not match any known request type
     */
    public void accept(String rawPayload,
                       WebhookPayloadConsumer<VerificationDecisionWebhookRequest> ifDecision,
                       WebhookPayloadConsumer<VerificationEventWebhookRequest> ifEvent) throws IdentityCheckWebhookException {

        apply(rawPayload,
            (raw, parsed) -> {ifDecision.accept(raw, parsed); return null;},
            (raw, parsed) -> {ifEvent.accept(raw, parsed); return null;});
    }

    /**
     * calls `ifDecision` function if the raw payload matches the Decision webhook request payload type
     * and `ifEvent` if it matches the Event webhook type
     *
     * @return the result of ifDecision or ifEvent function call
     * @throws IdentityCheckWebhookException in case the ifDecision or ifEvent function throws IdentityCheckWebhookException exception
     *                                       or when the request cannot be parsed, or it does not match any known request type
     */
    public <T> T apply(String rawPayload,
                       WebhookPayloadFunction<VerificationDecisionWebhookRequest, T> ifDecision,
                       WebhookPayloadFunction<VerificationEventWebhookRequest, T> ifEvent) throws IdentityCheckWebhookException {

        Map<String, ?> payloadAsMap = parseAsMap(rawPayload);
        if (VerificationDecisionWebhookRequest.matches(payloadAsMap)) {
            return ifDecision.apply(rawPayload, parse(rawPayload, VerificationDecisionWebhookRequest.class));
        }
        if (VerificationEventWebhookRequest.matches(payloadAsMap)) {
            return ifEvent.apply(rawPayload, parse(rawPayload, VerificationEventWebhookRequest.class));
        }
        throw new IdentityCheckWebhookException(Status.BAD_REQUEST.getStatusCode(), "cannot determine webhook request type", rawPayload);
    }

    private <T> T parse(String rawPayload, Class<T> valueType) throws IdentityCheckWebhookException {
        try {
            return objectMapper.readValue(rawPayload, valueType);
        } catch (JsonProcessingException e) {
            throw new IdentityCheckWebhookException(Status.BAD_REQUEST.getStatusCode(), "failed to parse request data", rawPayload, e);
        }
    }

    private Map<String, ?> parseAsMap(String rawPayload) throws IdentityCheckWebhookException {
        try {
            return objectMapper.readValue(rawPayload, new TypeReference<Map<String, ?>>() {});
        } catch (JsonProcessingException e) {
            throw new IdentityCheckWebhookException(Status.BAD_REQUEST.getStatusCode(), "failed to parse request data", rawPayload, e);
        }
    }

    @FunctionalInterface
    interface WebhookPayloadFunction<T, R> {
        R apply(String rawPayload, T parsedPayload) throws IdentityCheckWebhookException;
    }

    @FunctionalInterface
    interface WebhookPayloadConsumer<T> {
        void accept(String rawPayload, T parsedPayload) throws IdentityCheckWebhookException;
    }
}
