package com.generalbytes.batm.server.extensions.travelrule.gtr.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mockStatic;

class HashingServiceTest {

    private final HashingService hashingService = new HashingService();

    private static Stream<Arguments> testComputeSha512_arguments() {
        return Stream.of(
                arguments("value", "ec2c83edecb60304d154ebdb85bdfaf61a92bd142e71c4f7b25a15b9cb5f3c0a"
                                 + "e301cfb3569cf240e4470031385348bc296d8d99d09e06b26f09591a97527296"),
                arguments("", "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce"
                            + "47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"),
                arguments(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("testComputeSha512_arguments")
    void testComputeSha512(String value, String expectedHash) {
        String hash = hashingService.computeSha512(value);

        assertEquals(expectedHash, hash);
    }

    @Test
    void testComputeSha512_algorithmNotFound() {
        try (MockedStatic<MessageDigest> messageDigestMock = mockStatic(MessageDigest.class)) {
            messageDigestMock.when(() -> MessageDigest.getInstance("SHA-512")).thenThrow(NoSuchAlgorithmException.class);

            String hash = hashingService.computeSha512("value");

            assertNull(hash);
        }
    }

}