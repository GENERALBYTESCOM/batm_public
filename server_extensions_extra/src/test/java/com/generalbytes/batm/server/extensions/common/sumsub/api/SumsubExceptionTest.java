package com.generalbytes.batm.server.extensions.common.sumsub.api;

import com.generalbytes.batm.server.extensions.common.sumsub.SumsubException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumsubExceptionTest {

    @Test
    void testSumsubException_cause() {
        SumsubException exception = new SumsubException(new Throwable("error message"));

        assertEquals("java.lang.Throwable: error message", exception.getMessage());
        assertEquals("error message", exception.getCause().getMessage());
    }

    @Test
    void testSumsubException_message_and_cause() {
        SumsubException exception = new SumsubException("error message 1", new Throwable("error message 2"));

        assertEquals("error message 1", exception.getMessage());
        assertEquals("error message 2", exception.getCause().getMessage());
    }

}