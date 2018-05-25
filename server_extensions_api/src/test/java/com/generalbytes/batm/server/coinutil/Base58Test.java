package com.generalbytes.batm.server.coinutil;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class Base58Test {

    private static final Logger log = LoggerFactory.getLogger("com.generalbytes.batm.server.coinutil.Base58Test");

    @Test
    public void bitcoinAddressTest() {
        String address = "35qL43qYwLdKtnR7yMfGNDvzv6WyZ8yT2n";
        try {
            Base58.decodeToBigInteger(address);
            Base58.decodeChecked(address);
            System.out.println("Address = " + address);
            Assert.assertNotNull(address);
        } catch (AddressFormatException e) {
            log.debug("isAddressValid - address = " + address);
            Assert.assertTrue(false);
        }
    }

    @Test
    public void bitcoinCashAddressTest() {
        String address = "qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        try {
            Base58.decodeToBigInteger(address);
            Base58.decodeChecked(address);
            System.out.println("Address = " + address);
            Assert.assertNotNull(address);
        } catch (AddressFormatException e) {
            log.info("isAddressValid - address = " + address);
            Assert.assertTrue(false);
        }
    }

    @Test
    public void encodeSimpleString() {
        String originalInput = "test input";
        String encodedString = "qze82zm3uuufcdftffy7auyxav8wyxntxqqcswq5s7";
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedString = new String(decodedBytes);
        Assert.assertNotNull(decodedString);
    }
}
