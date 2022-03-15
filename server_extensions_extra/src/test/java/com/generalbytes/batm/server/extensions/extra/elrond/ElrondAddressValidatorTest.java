package com.generalbytes.batm.server.extensions.extra.elrond;


import org.junit.Assert;
import org.junit.Test;

public class ElrondAddressValidatorTest {
    @Test
    public void addressValidTest() {
        ElrondAddressValidator lav = new ElrondAddressValidator();
        String address = "erd1kvwvfpn3ncvt5dn8e0rnm8s7qv0u4yz3sl7dqcwuc0vq8533mevqnvvawh";
        boolean isValid = lav.isAddressValid(address);
        Assert.assertTrue(isValid);
    }
}
