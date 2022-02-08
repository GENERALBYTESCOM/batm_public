package com.generalbytes.batm.server.extensions.coinutil;

import org.junit.Assert;
import org.junit.Test;

public class BCHUtilTest {

    @Test
    public void convertBech32To3() {
        Assert.assertEquals("11F89RtxhJ1TwjtpEZx7NLXwmEH8iz6RN", BCHUtil.convertBech32To3("qqqqhjsevx2979t4rlrtkqqqmkmza6rcyuytaa05sg"));
    }
}

