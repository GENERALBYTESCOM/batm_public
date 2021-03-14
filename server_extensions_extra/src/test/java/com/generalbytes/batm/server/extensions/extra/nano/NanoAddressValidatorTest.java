package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Karl Oczadly
 */
public class NanoAddressValidatorTest {

    private static final NanoAddressValidator VALIDATOR = new NanoAddressValidator(
            new NanoExtensionContext(CryptoCurrency.NANO, null, NanoCurrencyUtil.NANO));


    @Test
    public void testValidAddresses() {
        assertTrue(VALIDATOR.isAddressValid("nano:nano_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertTrue(VALIDATOR.isAddressValid("nano_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertTrue(VALIDATOR.isAddressValid("nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertTrue(VALIDATOR.isAddressValid("nano_1jtx5p8141zjtukz4msp1x93st7nh475f74odj8673qqm96xczmtcnanos1o"));
        assertTrue(VALIDATOR.isAddressValid("xrb_1jtx5p8141zjtukz4msp1x93st7nh475f74odj8673qqm96xczmtcnanos1o"));
    }

    @Test
    public void testInvalidAddresses() {
        assertFalse(VALIDATOR.isAddressValid("nano_396sch48s3jmzq1bk312xxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertFalse(VALIDATOR.isAddressValid("ban:ban_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertFalse(VALIDATOR.isAddressValid("ban_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertFalse(VALIDATOR.isAddressValid("_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertFalse(VALIDATOR.isAddressValid("_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c"));
        assertFalse(VALIDATOR.isAddressValid("34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertFalse(VALIDATOR.isAddressValid("34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6q"));
        assertFalse(VALIDATOR.isAddressValid("some random string"));
        assertFalse(VALIDATOR.isAddressValid(""));
    }

}