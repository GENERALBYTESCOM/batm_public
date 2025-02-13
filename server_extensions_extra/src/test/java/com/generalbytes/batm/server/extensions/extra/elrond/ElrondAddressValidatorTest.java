package com.generalbytes.batm.server.extensions.extra.elrond;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ElrondAddressValidatorTest {
    @Test
    void addressValidTest() {
        ElrondAddressValidator lav = new ElrondAddressValidator();
        String address = "erd1kvwvfpn3ncvt5dn8e0rnm8s7qv0u4yz3sl7dqcwuc0vq8533mevqnvvawh";
        boolean isValid = lav.isAddressValid(address);
        assertTrue(isValid);
    }
}
