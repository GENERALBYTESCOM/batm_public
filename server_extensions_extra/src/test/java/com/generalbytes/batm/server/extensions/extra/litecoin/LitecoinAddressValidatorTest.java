package com.generalbytes.batm.server.extensions.extra.litecoin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LitecoinAddressValidatorTest {

    @Test
    void addressValidTest() {
        LitecoinAddressValidator lav = new LitecoinAddressValidator();
        String address = "MMR2PV6EqX617NYzVsztfMFRZTr6zDHWQx";
        boolean isValid = lav.isAddressValid(address);
        assertTrue(isValid);
    }
}
