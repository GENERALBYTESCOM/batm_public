package com.generalbytes.batm.server.extensions.extra.stellar;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class StellarcoinAddressValidatorTest {

    @Test
    public void isAddressValid() {
        StellarcoinAddressValidator v = new StellarcoinAddressValidator();
        assertTrue(v.isAddressValid("GDKTRWA5IA2H7OOZSYVD3M46HBBJEJX3AHCNM3AQO4T6PYAKXZ2R4A5N"));
        assertTrue(v.isAddressValid("GBE4WIINUARNQW6F6W2CNN6ZXZELG5EL3HZILX4FDXEVBBZHHTHBCXKZ"));
        assertTrue(v.isAddressValid("GB7ZCPDQ6PYQ26BBZP44C456HJ7CUIEFZEVXO2OEP6YSF5W2CHI7LSEY"));
        assertTrue(v.isAddressValid("GCGU5GS2NJUVXFK6XLLCTYUNYP7L5LUKKIOFE4RUXEASRRQBNT726YEK"));
        assertTrue(v.isAddressValid("GCKGBPWQFOLORZPVWSXEG76C6ROJUB2KPWZDIKUVJZ7LOBU5O5JGSBOF"));
        assertTrue(v.isAddressValid("GBBKY3EWSK7KYJC5TMTLGQZ2YTBEDPRIBV54ITSHCVHKQWUHWPSBPYEB"));
        assertTrue(v.isAddressValid("GBELI2EJ6WTXJXB2HM7IEK7JCAYM5PYCC5KPAL3TWLQP6SZBO2KEHTMS"));
    }
}