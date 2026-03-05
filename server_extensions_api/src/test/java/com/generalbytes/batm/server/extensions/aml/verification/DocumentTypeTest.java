package com.generalbytes.batm.server.extensions.aml.verification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentTypeTest {

    @Test
    void testEnumNames() {
        assertEquals(7, DocumentType.values().length);
        assertEquals("national_identity_card", DocumentType.national_identity_card.name());
        assertEquals("driving_licence", DocumentType.driving_licence.name());
        assertEquals("passport", DocumentType.passport.name());
        assertEquals("voter_id", DocumentType.voter_id.name());
        assertEquals("work_permit", DocumentType.work_permit.name());
        assertEquals("residence_permit", DocumentType.residence_permit.name());
        assertEquals("other", DocumentType.other.name());
    }

}