package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VeriffDigestTest {

    @Test
    void digest() {
        // https://developers.veriff.com/#generating-x-hmac-signature
        VeriffDigest d = new VeriffDigest("abcdef12-abcd-abcd-abcd-abcdef012345");
        String testPayload = "{\"verification\":{\"callback\":\"https://veriff.com\",\"person\":{\"firstName\":\"John\",\"lastName\":\"Smith\"},\"document\":{\"type\":\"PASSPORT\",\"country\":\"EE\"},\"vendorData\":\"unique id of a user\",\"timestamp\":\"2016-05-19T08:30:25.597Z\"}}";
        assertEquals("6af6d95822e19e9cc707aec55395d8d363ba2c7bc4625bc04ebeca0c7bf8cd67", d.digest(testPayload));
        assertEquals("895cb3fd31139d89aeb7b263682e15479ab57651e70591256ad2c14fde4950dd", d.digest("aea9ba6d-1b47-47fc-a4fc-f72b6d3584a7"));
    }

}