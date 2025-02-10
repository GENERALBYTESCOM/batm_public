package com.generalbytes.batm.server.extensions.coinutil;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BCHUtilTest {

    @Test
    void convertBech32To3() {
        assertEquals("11F89RtxhJ1TwjtpEZx7NLXwmEH8iz6RN", BCHUtil.convertBech32To3("qqqqhjsevx2979t4rlrtkqqqmkmza6rcyuytaa05sg"));
    }
}

