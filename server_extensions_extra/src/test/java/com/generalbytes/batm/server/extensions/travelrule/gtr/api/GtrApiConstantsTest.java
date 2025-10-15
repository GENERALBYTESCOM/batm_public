package com.generalbytes.batm.server.extensions.travelrule.gtr.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GtrApiConstantsTest {

    @Test
    void testCallbackTypes() {
        assertEquals(0, GtrApiConstants.CallbackType.NETWORK_TEST);
        assertEquals(4, GtrApiConstants.CallbackType.PII_VERIFICATION);
        assertEquals(6, GtrApiConstants.CallbackType.ADDRESS_VERIFICATION);
        assertEquals(7, GtrApiConstants.CallbackType.RECEIVE_TX_ID);
        assertEquals(9, GtrApiConstants.CallbackType.TX_VERIFICATION);
    }

    @Test
    void testSecretTypes() {
        assertEquals(1, GtrApiConstants.SecretType.CURVE_25519);
    }

    @Test
    void testVerifyPiiStatuses() {
        assertEquals(1, GtrApiConstants.PiiStatus.MATCH);
        assertEquals(2, GtrApiConstants.PiiStatus.MISMATCH);
    }

    @Test
    void testVerifyStatuses() {
        assertEquals(100000, GtrApiConstants.VerifyStatus.SUCCESS);
        assertEquals(100004, GtrApiConstants.VerifyStatus.CLIENT_BAD_PARAMETERS);
        assertEquals(100008, GtrApiConstants.VerifyStatus.BENEFICIARY_INTERNAL_SERVER_ERROR);
        assertEquals(200001, GtrApiConstants.VerifyStatus.ADDRESS_NOT_FOUND);
        assertEquals(200003, GtrApiConstants.VerifyStatus.PII_VERIFICATION_FAILED);
        assertEquals(200007, GtrApiConstants.VerifyStatus.TX_NOT_FOUND);
    }

    @Test
    void testVerifyFields() {
        assertEquals("100026", GtrApiConstants.VerifyField.Originator.NaturalPerson.NAME);
        assertEquals("110026", GtrApiConstants.VerifyField.Beneficiary.NaturalPerson.NAME);
    }

}