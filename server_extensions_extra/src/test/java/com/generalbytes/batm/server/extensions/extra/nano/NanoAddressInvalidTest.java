package com.generalbytes.batm.server.extensions.extra.nano;

import java.util.Arrays;
import java.util.Collection;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class NanoAddressInvalidTest {
    private static final NanoAddressValidator NANO_ADDRESS_VALIDATOR = new NanoAddressValidator();
    private final String input;

    @Parameterized.Parameters
    public static Collection getTestData() throws AddressFormatException {
        return Arrays.asList(new Object[][] { { "xrb_396sch48s3jmzq1bk31pxxpz64rn7joj38emj4ueypkb9p9mzrym34obze6c" },
                { "nano_396sch48s3jmzq1bk31pxxpz64rn7" } });
    }

    // @Parameterized.Parameters annotated method results are passed here
    public NanoAddressInvalidTest(String input) {
        this.input = input;
    }

    @Test
    public void isValid() throws AddressFormatException {
        Assert.assertFalse (NANO_ADDRESS_VALIDATOR.isAddressValid(input));
    }
}