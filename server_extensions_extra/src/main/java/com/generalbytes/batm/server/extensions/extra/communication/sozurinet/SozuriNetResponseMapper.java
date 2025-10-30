package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * A utility class responsible for mapping responses from the SozuriNet API
 * to the {@link ISmsResponse} interface. Provides methods to parse both
 * error and success responses from the API and transform them into objects
 * that comply with the application's response model.
 */
@Slf4j
@UtilityClass
public class SozuriNetResponseMapper {

    public static ISmsResponse mapErrorResponse(String errorMessage) {
        return new SmsResponse(null, ISmsResponse.ResponseStatus.ERROR, null, errorMessage);
    }

    /**
     * Parse the JSON response from SozuriNet API
     * JSON response format:
     * Success:
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
     * Error:
     * {
     *   "message": "Unauthenticated."
     * }
     */
    public static ISmsResponse mapJsonResponse(SozuriNetJsonResponse jsonResponse) {
        // Check for error response (has "message" field)
        if (jsonResponse.getMessage() != null) {
            String errorMessage = jsonResponse.getMessage();
            log.warn("SMS sending failed: {}", errorMessage);
            return mapErrorResponse(errorMessage);
        }

        // Check for successful response (has recipients)
        if (jsonResponse.getRecipients() != null && !jsonResponse.getRecipients().isEmpty()) {
            SozuriNetJsonResponse.Recipient recipient = jsonResponse.getRecipients().get(0);
            String messageId = recipient.getMessageId();
            
            if (messageId != null) {
                log.debug("SMS sent successfully with ID: {}, status: {}, statusCode: {}", 
                    messageId, recipient.getStatus(), recipient.getStatusCode());
                return mapSuccessResponse(messageId, null);
            } else {
                log.warn("Failed to parse message ID from successful response: {}", jsonResponse);
                return mapErrorResponse("Invalid response format - missing message ID");
            }
        }

        // If neither error nor success format is recognized
        log.warn("Unrecognized response format: {}", jsonResponse);
        return mapErrorResponse("Invalid response format from SMS service");
    }

    private static ISmsResponse mapSuccessResponse(String messageId, BigDecimal cost) {
        return new SmsResponse(messageId, ISmsResponse.ResponseStatus.OK, cost, null);
    }
}