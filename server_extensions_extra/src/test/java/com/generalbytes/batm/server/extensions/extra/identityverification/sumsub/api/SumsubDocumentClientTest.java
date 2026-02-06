package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SumsubDocumentClientTest {

    @Test
    void downloadDocumentThrowsWhenHostUnreachable() {
        SumsubDocumentClient clientWithInvalidUrl = new SumsubDocumentClient("token", "secret",
            "https://invalid-host-that-does-not-exist-12345.example.com");

        assertThrows(IOException.class, () ->
            clientWithInvalidUrl.downloadDocument("inspection123", "999999999")
        );
    }
}
