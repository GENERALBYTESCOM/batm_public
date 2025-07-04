package com.generalbytes.batm.server.extensions.extra.solana;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolanaAddressValidatorTest {

    private final SolanaAddressValidator addressValidator = new SolanaAddressValidator();

    @ParameterizedTest
    @ValueSource(strings = {
        "4Nd1m3MGMv9nMdjXyWBvKS1xYj1c7vGxHw8TQdGU9E8s",
        "8x3DaNbXMyB7U1djFgYeD1q5HvcnFHp9xvh8uZG1aAZP",
        "C2Rcb7kJReTRzA9VrrNoCHJx9WgdkV9RwFFWZAvqxzTu",
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW3v7w",
    })
    void testIsAddressValid_valid(String validAddress) {
        boolean isValid = addressValidator.isAddressValid(validAddress);

        assertTrue(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW",
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW3v7wABCD",
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW3v7.",
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW3v7/",
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW3v7I",
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW3v7l",
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW3v70",
        "Fh3vXnZNUoZkHRbZoq7wSe4biWR9q6NMRpM7bGoW3v7O",
    })
    void testIsAddressValid_invalid(String validAddress) {
        boolean isValid = addressValidator.isAddressValid(validAddress);

        assertFalse(isValid);
    }

    @Test
    void testMustBeBase58Address() {
        assertTrue(addressValidator.mustBeBase58Address());
    }

    @Test
    void testIsPaperWalletSupported() {
        assertFalse(addressValidator.isPaperWalletSupported());
    }

}