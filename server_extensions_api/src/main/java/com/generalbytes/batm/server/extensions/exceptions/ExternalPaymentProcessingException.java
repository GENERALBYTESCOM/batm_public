package com.generalbytes.batm.server.extensions.exceptions;

/**
 * Exception thrown when an external payment update fails due to invalid state or other processing errors.
 * For example, when the underlying order already expired.
 */
public class ExternalPaymentProcessingException extends Exception {

    public ExternalPaymentProcessingException(String message) {
        super(message);
    }
}
