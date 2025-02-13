package com.generalbytes.batm.server.extensions.extra.syscoin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyscoinAddressValidatorTest {
    private final SyscoinAddressValidator validator = new SyscoinAddressValidator();

    @Test
    void isAddressValid() {
        assertTrue(validator.isAddressValid("SSt4EYRJGeQv6vtQy3BgH2NbyFpHPZL27P"));
        assertTrue(validator.isAddressValid("SWu38cLXzEdxwTCiCKLsmUEMu6nh9ext57"));
        assertFalse(validator.isAddressValid("SSt4EYRJGeQv6xxxxxxxxxxxxxxxxxxxxx"));

        assertTrue(validator.isAddressValid("sys1q6dxdsytx6r2jerzm3g8zsdhgrs4s585y63gzm5"));
        assertFalse(validator.isAddressValid("sys1qqqqqq"));
    }
}