package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Request body for SozuriNet API v1 messaging endpoint.
 * Example JSON:
 * {
 *   "project": "Jira",
 *   "from": "Sozuri",
 *   "to": "+420603572525",
 *   "campaign": "Bitzuri",
 *   "channel": "sms",
 *   "apiKey": "yEmrtw0EHfta0npckO6GouYQeIoTpcbBDbQsB9V38qRcCWHIqyOyLA1o1jlT",
 *   "message": "Test sms for Jira Project",
 *   "type": "promotional"
 * }
 */
@Getter
@AllArgsConstructor
public class SozuriNetSmsRequest {

    @JsonProperty("project")
    private String project;

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;

    @JsonProperty("campaign")
    private String campaign;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("apiKey")
    private String apiKey;

    @JsonProperty("message")
    private String message;

    @JsonProperty("type")
    private String type;
}

