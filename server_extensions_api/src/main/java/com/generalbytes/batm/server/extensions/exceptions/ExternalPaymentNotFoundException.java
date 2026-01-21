package com.generalbytes.batm.server.extensions.exceptions;

/**
 * Exception thrown when the specified external payment cannot be found.
 */
public class ExternalPaymentNotFoundException extends ExternalPaymentProcessingException {

    public ExternalPaymentNotFoundException(String message) {
        super(message);
    }
}
