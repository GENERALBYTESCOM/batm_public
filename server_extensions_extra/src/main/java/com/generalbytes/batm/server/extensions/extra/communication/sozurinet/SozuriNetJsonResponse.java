package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * JSON response object for SozuriNet API calls
 * Example of positive response:
 * {
 *   "messageData": {"messages": 1},
 *   "recipients": [{
 *     "messageId": "795ecc08996e79d491cce39913fbf11d2bfdb18d",
 *     "to": "254603572525",
 *     "status": "sent",
 *     "statusCode": "11",
 *     "bulkId": "bulk69038c633ae6d2.861374281761840227",
 *     "messagePart": 1,
 *     "type": "promotional"
 *   }]
 * }
 * 
 * Or in case of error (e.g., invalid API key):
 * {
 *   "message": "Unauthenticated."
 * }
 */
@Setter
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SozuriNetJsonResponse {

    @JsonProperty("messageData")
    private MessageData messageData;

    @JsonProperty("recipients")
    private List<Recipient> recipients;

    @JsonProperty("message")
    private String message;

    @Setter
    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageData {
        @JsonProperty("messages")
        private Integer messages;
    }

    @Setter
    @Getter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Recipient {
        @JsonProperty("messageId")
        private String messageId;

        @JsonProperty("to")
        private String to;

        @JsonProperty("status")
        private String status;

        @JsonProperty("statusCode")
        private String statusCode;

        @JsonProperty("bulkId")
        private String bulkId;

        @JsonProperty("messagePart")
        private Integer messagePart;

        @JsonProperty("type")
        private String type;
    }

}