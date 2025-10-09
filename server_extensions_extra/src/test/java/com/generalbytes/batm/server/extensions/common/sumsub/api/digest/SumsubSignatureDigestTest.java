package com.generalbytes.batm.server.extensions.common.sumsub.api.digest;

import com.generalbytes.batm.server.extensions.common.sumsub.SumsubException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import si.mazi.rescu.RestInvocation;

import javax.crypto.Mac;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SumsubSignatureDigestTest {

    @Test
    void testSumsubSignatureDigest() throws InvalidKeyException {
        try (MockedStatic<Mac> macMock = mockStatic(Mac.class)) {
            Mac macInstance = mock(Mac.class);

            macMock.when(() -> Mac.getInstance("HmacSHA256")).thenReturn(macInstance);

            new SumsubSignatureDigest("secret");

            verify(macInstance, times(1)).init(any());
        }
    }

    @Test
    void testSumsubSignatureDigest_noSuchAlgorithmException() {
        try (MockedStatic<Mac> macMock = mockStatic(Mac.class)) {
            macMock.when(() -> Mac.getInstance("HmacSHA256"))
                    .thenThrow(new NoSuchAlgorithmException("test-sumsub-signature-digest-no-such-algorithm-exception"));

            SumsubException exception = assertThrows(SumsubException.class, () -> new SumsubSignatureDigest("secret"));

            assertEquals(
                    "java.security.NoSuchAlgorithmException: test-sumsub-signature-digest-no-such-algorithm-exception",
                    exception.getMessage()
            );
        }
    }

    @Test
    void testSumsubSignatureDigest_invalidKeyException() throws InvalidKeyException {
        try (MockedStatic<Mac> macMock = mockStatic(Mac.class)) {
            Mac macInstance = mock(Mac.class);

            macMock.when(() -> Mac.getInstance("HmacSHA256")).thenReturn(macInstance);
            doThrow(new InvalidKeyException("test-sumsub-signature-digest-invalid-key-exception")).when(macInstance).init(any());

            SumsubException exception = assertThrows(SumsubException.class, () -> new SumsubSignatureDigest("secret"));

            assertEquals("Failed to initialize SumsubSignatureDigest, is the secret key configured properly?", exception.getMessage());
        }
    }

    @Test
    void testDigestParams() {
        SumsubSignatureDigest digest = new SumsubSignatureDigest("secret");

        RestInvocation restInvocation = mock(RestInvocation.class);
        when(restInvocation.getHttpMethod()).thenReturn("GET");
        when(restInvocation.getInvocationUrl()).thenReturn("https://api.sumsub.com/resources/status/api");
        when(restInvocation.getBaseUrl()).thenReturn("https://api.sumsub.com");
        when(restInvocation.getRequestBody()).thenReturn("body");

        String signature = digest.digestParams(restInvocation);

        assertEquals("e19364f323b9bb4cc377e1735f5bc8f1555e749393c7b32f0a7514e6b91a126a", signature);
    }

}