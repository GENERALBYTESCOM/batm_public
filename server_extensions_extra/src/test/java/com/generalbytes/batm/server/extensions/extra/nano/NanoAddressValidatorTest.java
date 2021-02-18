package com.generalbytes.batm.server.extensions.extra.nano;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class NanoAddressValidatorTest {

    private static final NanoAddressValidator VALIDATOR = new NanoAddressValidator();


    @Test
    public void testValidAddresses() {
        assertTrue(VALIDATOR.isAddressValid("nano_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertTrue(VALIDATOR.isAddressValid("nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertTrue(VALIDATOR.isAddressValid("nano_1jtx5p8141zjtukz4msp1x93st7nh475f74odj8673qqm96xczmtcnanos1o"));
        assertTrue(VALIDATOR.isAddressValid("xrb_1jtx5p8141zjtukz4msp1x93st7nh475f74odj8673qqm96xczmtcnanos1o"));
    }

    @Test
    public void testInvalidAddresses() {
        assertFalse(VALIDATOR.isAddressValid("nano_396tch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertFalse(VALIDATOR.isAddressValid("ban_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertFalse(VALIDATOR.isAddressValid("nano_5x4ui45q1cw8hydmfdn4ec5ijsdqi4ryp14g4ayh71jcdkwmddrq7ca9xzn9"));
        assertFalse(VALIDATOR.isAddressValid("nano_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6"));
        assertFalse(VALIDATOR.isAddressValid("34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertFalse(VALIDATOR.isAddressValid("34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6q"));
        assertFalse(VALIDATOR.isAddressValid("some random string"));
        assertFalse(VALIDATOR.isAddressValid(""));
    }

}