package com.generalbytes.batm.server.services.amlkyc.verification.veriff.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VeriffDigestTest {

    @Test
    public void digest() {
        // https://developers.veriff.com/#generating-x-hmac-signature
        VeriffDigest d = new VeriffDigest("abcdef12-abcd-abcd-abcd-abcdef012345");
        String testPayload = "{\"verification\":{\"callback\":\"https://veriff.com\",\"person\":{\"firstName\":\"John\",\"lastName\":\"Smith\"},\"document\":{\"type\":\"PASSPORT\",\"country\":\"EE\"},\"vendorData\":\"unique id of a user\",\"timestamp\":\"2016-05-19T08:30:25.597Z\"}}";
        assertEquals("6af6d95822e19e9cc707aec55395d8d363ba2c7bc4625bc04ebeca0c7bf8cd67", d.digest(testPayload));
    }

}