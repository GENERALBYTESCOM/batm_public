package com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumsubApiErrorTest {

    @Test
    void testToString() {
        SumsubApiError error = new SumsubApiError();
        error.setDescription("description");
        error.setCode(200);
        error.setCorrelationId("correlation_id");
        error.setErrorCode(1000);
        error.setErrorName("duplicate-document");

        String errorAsString = error.toString();

        assertEquals(
                "description, error code: 1000, error name: duplicate-document, HTTP code: 200, correlation ID: correlation_id",
                errorAsString
        );
    }

}