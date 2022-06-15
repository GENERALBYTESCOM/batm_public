package com.generalbytes.batm.server.extensions.extra.cardano;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardanoAddressValidatorTest {

    @Test
    public void isAddressValid() {
        CardanoAddressValidator v = new CardanoAddressValidator();
        assertTrue(v.isAddressValid("addr1u8pcjgmx7962w6hey5hhsd502araxp26kdtgagakhaqtq8sxy9w7g"));
        assertTrue(v.isAddressValid("addr1g9u5vlrf4xkxv2qpwngf6cjhtw542ayty80v8dyr49rf5evph3wczvf2kd5vam"));
        assertTrue(v.isAddressValid("addr1qx8fg2e9yn0ga6sav0760cxmx0antql96mfuhqgzcc5swugw2jqqlugnx9qjep9xvcx40z0zfyep55r2t3lav5smyjrsxv9uuh"));
        assertTrue(v.isAddressValid("Ae2tdPwUPEZ4YjgvykNpoFeYUxoyhNj2kg8KfKWN2FizsSpLUPv68MpTVDo"));
        assertTrue(v.isAddressValid("Ae2tdPwUPEZHtBmjZBF4YpMkK9tMSPTE2ADEZTPN97saNkhG78TvXdp3GDk"));
        assertTrue(v.isAddressValid("DdzFFzCqrhszf5Qa6Dvxmrtjptvx6Unt976EkBog63eqzYXxjPD8LwJbkw7uwfmScwjPo7UJjqWUFhu1t89MTnLAjWUB3SZNKwJ2DaRz"));
        assertTrue(v.isAddressValid("DdzFFzCqrht8X95uyfUa9Wa3byueNNKsYdYrRws8RtqXHFnpSecacdrveVGaP3SYyHz8zt6asTZR9xqRCoqf6sA8LXp8ZMM4um4V9rp4"));

        assertFalse(v.isAddressValid("addr1u8pcjgmx7962w6hey5hhsd502araxp26kdtgagakhaqtq8sxy9w7g@@@"));
        assertFalse(v.isAddressValid("addr1g9u5vlrf4xkxv2qpwngf6cjhtw542ayty80v8dyr49rf5evph3wczvf2kd5vam@@@"));
        assertFalse(v.isAddressValid("addr1qx8fg2e9yn0ga6sav0760cxmx0antql96mfuhqgzcc5swugw2jqqlugnx9qjep9xvcx40z0zfyep55r2t3lav5smyjrsxv9uuh@@@"));
        assertFalse(v.isAddressValid("Ae2tdPwUPEZ4YjgvykNpoFeYUxoyhNj2kg8KfKWN2FizsSpLUPv68MpTVDo@@@"));
        assertFalse(v.isAddressValid("Ae2tdPwUPEZHtBmjZBF4YpMkK9tMSPTE2ADEZTPN97saNkhG78TvXdp3GDk@@@"));
        assertFalse(v.isAddressValid("DdzFFzCqrhszf5Qa6Dvxmrtjptvx6Unt976EkBog63eqzYXxjPD8LwJbkw7uwfmScwjPo7UJjqWUFhu1t89MTnLAjWUB3SZNKwJ2DaRz@@@"));
        assertFalse(v.isAddressValid("DdzFFzCqrht8X95uyfUa9Wa3byueNNKsYdYrRws8RtqXHFnpSecacdrveVGaP3SYyHz8zt6asTZR9xqRCoqf6sA8LXp8ZMM4um4V9rp4@@@"));

        assertFalse(v.isAddressValid(""));
        assertFalse(v.isAddressValid("invalid"));
    }
}