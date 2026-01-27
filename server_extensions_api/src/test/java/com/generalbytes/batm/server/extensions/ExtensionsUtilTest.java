package com.generalbytes.batm.server.extensions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionsUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "user@example.com",                // Standard
        "firstname.lastname@domain.com",   // Dots in local part
        "user+mailbox@google.com",         // Plus sign (common for filtering)
        "1234567890@domain.com",           // Numeric local part
        "user1234567890@domain.com",       // Alphanumeric local part
        "user@sub.domain.example.com",     // Subdomains
        "user@sub1.com",                   // Subdomain with number
        "a@b.cd",                          // Short domain/TLD
        "a@something.technology",          // Long domain/TLD
        "user_name@domain-one.com",        // Underscores and hyphens
    })
    void testValidEmails(String email) {
        assertTrue(ExtensionsUtil.isValidEmailAddress(email), "Should be valid: " + email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "plainaddress",                    // No @ symbol
        "Abc.example.com",                 // No @ symbol with dots
        "A@b@c@example.com",               // Multiple @ symbols
        "#@%^%#$@#$@#.com",                // Garbage
        "@example.com",                    // No local part
        "user@.",                          // No TLD
        "user@domain..com",                // Double dot in domain
        "user..name@domain.com",           // Double dot in local part
        ".user@domain.com",                // Leading dot
        "user.@domain.com",                // Trailing dot
        "user@domain.c",                   // TLD too short (usually 2+ chars)
        "user@domain.123",                 // Numeric TLD
        "user name@domain.com",            // Contains spaces

    })
    void testInvalidEmails(String email) {
        assertFalse(ExtensionsUtil.isValidEmailAddress(email), "Should be invalid: " + email);
    }
}