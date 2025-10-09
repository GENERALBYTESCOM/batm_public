package com.generalbytes.batm.server.extensions.travelrule.sumsub.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SumsubApiExceptionTest {

    @Test
    void testSumsubApiException() {
        SumsubApiError error = mock(SumsubApiError.class);
        when(error.toString()).thenReturn("error as string");

        SumsubApiException exception = new SumsubApiException(error);

        assertEquals("error as string", exception.getMessage());
    }

    @Test
    void testSumsubApiException_errorNull() {
        SumsubApiException exception = new SumsubApiException(null);

        assertEquals("UNEXPECTED RESPONSE", exception.getMessage());
    }

}