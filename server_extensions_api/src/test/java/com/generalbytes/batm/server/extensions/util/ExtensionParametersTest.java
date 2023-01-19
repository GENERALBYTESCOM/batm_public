package com.generalbytes.batm.server.extensions.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExtensionParametersTest {
    @Test
    public void test() {
        testFromDelimited(null, null, 0);

        testFromDelimited("", "", 0);
        testFromDelimited(null, "", 1);

        testFromDelimited("", "::", 0);
        testFromDelimited("", "::", 1);
        testFromDelimited("", "::", 2);
        testFromDelimited(null, ":", 3);

        testFromDelimited(null, "a:b:c", 3);
        testFromDelimited("", "a:b:", 2);
        testFromDelimited("", "a::c", 1);

        testFromDelimited("a", "a:b:c", 0);
        testFromDelimited("a", "a", 0);
        testFromDelimited("c", "a:b:c", 2);

        testFromDelimited("a:b", "a\\:b:c", 0);
        testFromDelimited("a\\;b", "a\\;b:c", 0);

        assertEquals("aa::cc:", new ExtensionParameters(Arrays.asList("aa", "", "cc", "")).getDelimited());

        ExtensionParameters params = new ExtensionParameters(Arrays.asList("", "A:B:C", "\\X"));
        assertEquals("", params.get(0));
        assertEquals("A:B:C", params.get(1));
        assertEquals("\\X", params.get(2));

        assertEquals(":A\\:B\\:C:\\X", params.getDelimited());
    }

    private void testFromDelimited(String expected, String input, int paramNumber) {
        assertEquals(expected, ExtensionParameters.fromDelimited(input).get(paramNumber));
    }

    @Test
    public void getPrefix() {
        assertEquals("A", ExtensionParameters.fromDelimited("A:b:c:").getPrefix());
        assertEquals("", ExtensionParameters.fromDelimited(":").getPrefix());
        assertEquals("", ExtensionParameters.fromDelimited("").getPrefix());
        assertNull(ExtensionParameters.fromDelimited(null).getPrefix());
    }

    @Test
    public void getParameters() {
        assertEquals(1, ExtensionParameters.fromDelimited("P:").getWithoutPrefix().size());
        assertEquals(0, ExtensionParameters.fromDelimited("P").getWithoutPrefix().size());
        assertEquals(0, ExtensionParameters.fromDelimited("").getWithoutPrefix().size());
        assertEquals(0, ExtensionParameters.fromDelimited(null).getWithoutPrefix().size());

        ExtensionParameters parameters = ExtensionParameters.fromDelimited("A:b:c:");
        assertEquals(Arrays.asList("b", "c", ""), parameters.getWithoutPrefix());
        assertEquals(Arrays.asList("A", "b", "c", ""), parameters.getAll());
    }
}