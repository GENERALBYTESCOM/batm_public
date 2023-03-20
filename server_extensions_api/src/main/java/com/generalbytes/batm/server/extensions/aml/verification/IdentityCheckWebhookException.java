package com.generalbytes.batm.server.extensions.aml.verification;

/**
 * Used when Identity Verification Webhook cannot be processed correctly.
 * Contains a non 2xx-code HTTP response code and response body (entity)
 * that will be sent back to the client (the originator of the webhook)
 * and a standard exception message & cause that will be logged.
 * <p>
 * Some providers (Veriff) will try to resend the webhook if the response is non 2xx
 */
public class IdentityCheckWebhookException extends Exception {

    private final int responseStatus;
    private final Object responseEntity;

    /**
     * @param responseStatus a non 2xx HTTP response status code
     * @param responseEntity Response sent to the client
     * @param message        Message for logfile
     */
    public IdentityCheckWebhookException(int responseStatus, Object responseEntity, String message) {
        this(responseStatus, responseEntity, message, null);
    }

    public IdentityCheckWebhookException(int responseStatus, Object responseEntity, String message, Throwable cause) {
        super(message, cause);
        this.responseStatus = responseStatus;
        this.responseEntity = responseEntity;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public Object getResponseEntity() {
        return responseEntity;
    }
}
