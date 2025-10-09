package com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto;

/**
 * Wrapper object for Sumsub webhook request.
 */
public record SumsubWebhookRequest(String vaspDid, String digestAlgorithm, String payloadDigest, String message) {

}
