package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.exception;

/**
 * A custom exception class that extends {@code RuntimeException}.
 * This class is utilized to represent errors specific to the SumSub
 * implementation. It provides constructors to capture error
 * messages and underlying causes for detailed exception handling.
 *
 * <p>Intended usage:
 * - To encapsulate and propagate underlying exceptions such as
 *   misconfigurations or cryptographic failures in SumSub-related
 *   logic or components.
 * - To provide a meaningful abstraction for runtime errors in
 *   SumSub-related processes.
 */
public class SumSubException extends RuntimeException {
    public SumSubException(String message, Throwable cause) {
        super(message, cause);
    }

    public SumSubException(Throwable cause) {
        super(cause);
    }
}
