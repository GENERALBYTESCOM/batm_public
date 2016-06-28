package com.generalbytes.batm.server.extensions.extra.test.shadowcash;

import com.generalbytes.batm.server.extensions.extra.shadowcash.ShadowcashAddressValidator;
import com.generalbytes.batm.server.extensions.extra.test.BaseTest;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author ludx
 */
public class ShadowcashAddressValidatorTest extends BaseTest {

    private ShadowcashAddressValidator shadowcashAddressValidator = new ShadowcashAddressValidator();

    @Test(groups = {"init"})
    public void validateShadowcashAddressTest() {
        assertThat(shadowcashAddressValidator.isAddressValid("SeMP3JexZbZaYvTMJM6aR7XHLXbAJvd462"), is(true));
        assertThat(shadowcashAddressValidator.isAddressValid("1eMP3JexZbZaYvTMJM6aR7XHLXbAJvd462"), is(false));
    }

}