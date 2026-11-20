package com.generalbytes.batm.server.extensions.compliance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComplianceTierTest {

    @Test
    void shouldExposeIdAndName() {
        ComplianceTier tier = new ComplianceTier(3L, "Registered");

        assertEquals(3L, tier.id());
        assertEquals("Registered", tier.name());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectMissingName(String name) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ComplianceTier(1L, name));

        assertEquals("name is required", exception.getMessage());
    }
}
