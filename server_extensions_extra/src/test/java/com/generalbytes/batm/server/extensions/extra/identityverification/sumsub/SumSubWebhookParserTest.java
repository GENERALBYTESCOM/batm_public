package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SumSubWebhookParserTest {

    private SumSubWebhookParser parser;

    @BeforeEach
    void setUp() {
        parser = new SumSubWebhookParser();
    }

    @Test
    void testParse_valid() throws IdentityCheckWebhookException {
        String rawPayload = "{\"stringField\":\"some string\",\"dateField\":\"2021-01-01\"}";

        TestObject parse = parser.parse(rawPayload, TestObject.class);

        assertEquals("some string", parse.stringField());
        assertEquals(LocalDate.of(2021, 1, 1), parse.dateField());
    }

    @Test
    void testParse_fail() {
        IdentityCheckWebhookException exception = assertThrows(IdentityCheckWebhookException.class, () -> parser.parse("not a valid json body", TestObject.class));
        assertEquals("not a valid json body", exception.getMessage());
        assertEquals("Failed to parse request data", exception.getResponseEntity());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exception.getResponseStatus());
    }

    record TestObject(String stringField, LocalDate dateField) {

    }
}