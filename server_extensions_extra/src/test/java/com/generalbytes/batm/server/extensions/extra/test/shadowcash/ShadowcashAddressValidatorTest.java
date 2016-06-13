package com.generalbytes.batm.server.extensions.extra.test.shadowcash;

import com.generalbytes.batm.server.extensions.extra.shadowcash.ShadowcashAddressValidator;
import com.generalbytes.batm.server.extensions.extra.test.BaseTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

/**
 * @author ludx
 */
public class ShadowcashAddressValidatorTest extends BaseTest {

    private ShadowcashAddressValidator shadowcashAddressValidator = new ShadowcashAddressValidator();

    @Test(groups = {"init"})
    public void validateShadowcashAddressTest() {
        assertTrue(shadowcashAddressValidator.isAddressValid("SeMP3JexZbZaYvTMJM6aR7XHLXbAJvd462"));
        assertFalse(shadowcashAddressValidator.isAddressValid("1eMP3JexZbZaYvTMJM6aR7XHLXbAJvd462"));
    }

}