package com.generalbytes.batm.server.extensions.extra.litecoin;

import org.junit.Assert;
import org.junit.Test;

public class LitecoinAddressValidatorTest {

    @Test
    public void addressValidTest() {
        LitecoinAddressValidator lav = new LitecoinAddressValidator();
        String address = "MMR2PV6EqX617NYzVsztfMFRZTr6zDHWQx";
        boolean isValid = lav.isAddressValid(address);
        Assert.assertTrue(isValid);
    }
}
