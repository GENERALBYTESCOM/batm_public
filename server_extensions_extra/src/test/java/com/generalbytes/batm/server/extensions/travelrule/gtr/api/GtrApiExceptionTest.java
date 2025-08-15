package com.generalbytes.batm.server.extensions.travelrule.gtr.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class GtrApiExceptionTest {

    @Test
    void testGetMessage() {
        GtrApiError apiError = createApiError("message", "detailed message", 21);
        GtrApiException exception = new GtrApiException(apiError);

        assertEquals(21, exception.getStatusCode());
        assertEquals("message (detailed message)", exception.getMessage());
    }

    @Test
    void testGetMessage_nullError() {
        GtrApiException exception = new GtrApiException(null);

        assertNull(exception.getStatusCode());
        assertEquals("UNEXPECTED RESPONSE", exception.getMessage());
    }

    private static Stream<Arguments> testGetMessage_nullError_arguments() {
        return Stream.of(
                arguments(null, null, "missing message (missing detail message)"),
                arguments(null, "detail message", "missing message (detail message)"),
                arguments("message", null, "message (missing detail message)"),
                arguments("message", "detail message", "message (detail message)")
        );
    }

    @ParameterizedTest
    @MethodSource("testGetMessage_nullError_arguments")
    void testGetMessage_missingProperties(String message, String detailedMessage, String expectedResultMessage) {
        GtrApiError apiError = createApiError(message, detailedMessage, null);
        GtrApiException exception = new GtrApiException(apiError);

        assertNull(exception.getStatusCode());
        assertEquals(expectedResultMessage, exception.getMessage());
    }

    @Test
    void testGetMessage_nullErrorProperties() {
        GtrApiError apiError = new GtrApiError();
        GtrApiException exception = new GtrApiException(apiError);

        assertNull(exception.getStatusCode());
        assertEquals("missing message (missing detail message)", exception.getMessage());
    }

    private GtrApiError createApiError(String message, String detailedMessage, Integer statusCode) {
        GtrApiError apiError = new GtrApiError();
        apiError.setMsg(message);
        apiError.setVerifyStatus(statusCode);
        apiError.setVerifyMessage(detailedMessage);

        return apiError;
    }

}