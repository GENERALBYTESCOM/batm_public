package com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook;

import java.util.Map;

/**
 * Event webhook - sent by Veriff to Webhook events URL.
 * The Webhook events URL needs to be configured in Veriff Station.
 * <p>
 * We can get two types of events:
 * - Verification events (this): tracks events for identity verification process performed by user.
 * - Proof of address events: if feature is enabled, tracks events for proof of address data gathering performed by user.
 * <p>
 * Verification events
 * To keep the clients up to date with progress during the verification process Veriff allows to subscribe to certain events.
 * Currently, two events are triggered:
 * - user arrives to Veriff environment and starts the verification process
 * - user is done with the process and submits the attempt.
 */
public class VerificationEventWebhookRequest {
    /**
     * UUID v4 which identifies the verification session
     */
    public String id;
    /**
     * UUID v4 which identifies session attempt
     */
    public String attemptId;
    /**
     * Feature on which the event was triggered (selfid refers to the end user flow)
     */
    public String feature;
    /**
     * Event code (one of 7001, 7002)
     */
    public String code;
    /**
     * Corresponding action description (one of started, submitted)
     */
    public String action;
    /**
     * Vendor specific data string, max 400 characters long, set during session creation.
     * Identity pulbic ID in our case
     */
    public String vendorData;


    public static boolean matches(Map<String, ?> payload) {
        return payload.containsKey("action") && payload.containsKey("attemptId") && payload.containsKey("id");
    }

    public String getApplicantId() {
        return this.id;
    }

}
