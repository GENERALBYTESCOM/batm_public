package com.generalbytes.batm.server.extensions.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DeterministicUuidV4GeneratorTest {

    private static Stream<Arguments> testCreateDeterministicUuidV4_arguments() {
        return Stream.of(
            arguments("test", "9f86d081-884c-4d65-9a2f-eaa0c55ad015"),
            arguments("input", "c96c6d5b-e8d0-4a12-a7b5-cdc1b207fa6b"),
            arguments("R12345", "13de980c-600e-4f99-b1ca-4191b4eeb449"),
            arguments("my_test_input", "67544c5b-44b5-4d4b-855e-9f7a7b516e0a"),
            arguments("Satoshi Nakamoto", "a0dc65ff-ca79-4873-8bea-0ac274015b95")
        );
    }

    @ParameterizedTest
    @MethodSource("testCreateDeterministicUuidV4_arguments")
    void testCreateDeterministicUuidV4(String input, String expectedUuidV4) {
        UUID uuidV4 = DeterministicUuidV4Generator.createDeterministicUuidV4(input.getBytes());

        assertEquals(expectedUuidV4, uuidV4.toString());
        assertEquals(4, uuidV4.version());
    }

}