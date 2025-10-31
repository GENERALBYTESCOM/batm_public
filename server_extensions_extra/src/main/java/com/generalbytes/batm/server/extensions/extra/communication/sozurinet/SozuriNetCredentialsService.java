package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for managing and validating credentials used to interact with the SozuriNet API.
 * This class processes raw credentials and converts them into a {@link SozuriNetApiCredentials} object containing
 * authentication details required by the API.
 * Credentials format: "apiKey:project:from:campaign:channel"
 * - apiKey: API key from SozuriNet account
 * - project: Project identifier from SozuriNet account
 * - from: Sender name/identifier
 * - campaign: Campaign identifier
 * - channel: Channel type (e.g., "sms")
 */
@Slf4j
public class SozuriNetCredentialsService {

    public SozuriNetApiCredentials getCredentials(String credentials) {
        String[] tokens = credentials.split(":");
        if (tokens.length != 5) {
            log.error("Invalid credentials format. Expected format: 'apiKey:project:from:campaign:channel'");
            throw new SozuriNetValidationException("Invalid credentials format");
        }

        String apiKey = tokens[0];
        String project = tokens[1];
        String from = tokens[2];
        String campaign = tokens[3];
        String channel = tokens[4];

        return new SozuriNetApiCredentials(project, from, campaign, channel, apiKey);
    }
}
