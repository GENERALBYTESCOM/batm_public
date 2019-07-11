package com.generalbytes.batm.server.extensions.extra.lightningbitcoin;

import org.junit.Test;

import static org.junit.Assert.*;

public class LightningBitcoinAddressValidatorTest {

    @Test
    public void isAddressValid() {
        assertTrue(new LightningBitcoinAddressValidator().isAddressValid("lnbc1pws00jppp59hxx6nczp7z2e49kuj0q8l0dt6sefyaxvz7vcrzaldwrat4jzlnqdqu2askcmr9wssx7e3q2dshgmmndp5scqzysxqrrss2erzu7ha7ghwym9qsr5ea45g9tw94f74la95nd8y9lzzq57e5l6hdmgy4a90uxrmrc8sddwjhm6h9qujch76esz6r5qymmyrvp8qs8gqwr9cgq"));
        assertFalse(new LightningBitcoinAddressValidator().isAddressValid("lnbc10n1pws00jppp59hxx6nczp7z2e49kuj0q8l0dt6sefyaxvz7vcrzaldwrat4jzlnqdqu2askcmr9wssx7e3q2dshgmmndp5scqzysxqrrss2erzu7ha7ghwym9qsr5ea45g9tw94f74la95nd8y9lzzq57e5l6hdmgy4a90uxrmrc8sddwjhm6h9qujch76esz6r5qymmyrvp8qs8gqwr9cgq"));
        assertFalse(new LightningBitcoinAddressValidator().isAddressValid("ln1qs8gqwr9cgq"));
        assertFalse(new LightningBitcoinAddressValidator().isAddressValid(""));
        assertFalse(new LightningBitcoinAddressValidator().isAddressValid(null));
    }
}