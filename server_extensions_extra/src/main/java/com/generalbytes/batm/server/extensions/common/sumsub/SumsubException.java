package com.generalbytes.batm.server.extensions.common.sumsub;

/**
 * A custom exception class that extends {@code RuntimeException}.
 * This class is utilized to represent errors specific to the Sumsub
 * implementation. It provides constructors to capture error
 * messages and underlying causes for detailed exception handling.
 *
 * <p>Intended usage:
 * - To encapsulate and propagate underlying exceptions such as
 *   misconfigurations or cryptographic failures in Sumsub-related
 *   logic or components.
 * - To provide a meaningful abstraction for runtime errors in
 *   Sumsub-related processes.
 */
public class SumsubException extends RuntimeException {
    public SumsubException(String message, Throwable cause) {
        super(message, cause);
    }

    public SumsubException(Throwable cause) {
        super(cause);
    }
}
