package com.generalbytes.batm.server.extensions;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdentitySuspensionTest {

    @Test
    void newInstanceIsPermanentWithoutReasonOrIdentity() {
        IdentitySuspension suspension = new IdentitySuspension();

        assertNull(suspension.getIdentityPublicId());
        assertNull(suspension.getUntil());
        assertNull(suspension.getReason());
    }

    @Test
    void toStringDoesNotContainReason() {
        IdentitySuspension suspension = new IdentitySuspension();
        suspension.setUntil(OffsetDateTime.now().plusDays(1));
        suspension.setReason("secret reason");

        assertFalse(suspension.toString().contains("secret reason"));
        assertTrue(suspension.toString().contains("until="));
    }
}
