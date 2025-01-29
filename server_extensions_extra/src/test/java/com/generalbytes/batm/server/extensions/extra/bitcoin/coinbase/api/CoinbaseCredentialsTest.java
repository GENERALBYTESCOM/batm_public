package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class CoinbaseCredentialsTest {

    @Test
    public void testCreateLegacy_valid() {
        CoinbaseCredentials credentials = CoinbaseCredentials.createLegacy("apiKey", "apiSecret");

        assertEquals("apiKey", credentials.getLegacyApiKey());
        assertEquals("apiSecret", credentials.getLegacyApiSecret());
        assertNull(credentials.getCdpPrivateKey());
        assertNull(credentials.getCdpKeyName());
        assertTrue(credentials.isLegacy());
        assertFalse(credentials.isCdp());
    }

    @Test
    public void testCreateLegacy_invalid() {
        doTestCreateLegacyInvalid(null, "apiSecret", "apiKey cannot be null or empty");
        doTestCreateLegacyInvalid("", "apiSecret", "apiKey cannot be null or empty");
        doTestCreateLegacyInvalid("apiKey", null, "apiSecret cannot be null or empty");
        doTestCreateLegacyInvalid("apiKey", "", "apiSecret cannot be null or empty");
    }

    @Test
    public void testCreateCdp_valid() {
        CoinbaseCredentials credentials = CoinbaseCredentials.createCdp("cdpPrivateKey", "cdpKeyName");

        assertEquals("cdpPrivateKey", credentials.getCdpPrivateKey());
        assertEquals("cdpKeyName", credentials.getCdpKeyName());
        assertNull(credentials.getLegacyApiKey());
        assertNull(credentials.getLegacyApiSecret());
        assertFalse(credentials.isLegacy());
        assertTrue(credentials.isCdp());
    }

    @Test
    public void testCreateCdp_invalid() {
        doTestCreateCdpInvalid(null, "cdpKeyName", "cdpPrivateKey cannot be null or empty");
        doTestCreateCdpInvalid("", "cdpKeyName", "cdpPrivateKey cannot be null or empty");
        doTestCreateCdpInvalid("cdpPrivateKey", null, "cdpKeyName cannot be null or empty");
        doTestCreateCdpInvalid("cdpPrivateKey", "", "cdpKeyName cannot be null or empty");
    }

    private void doTestCreateCdpInvalid(String cdpPrivateKey, String cdpKeyName, String expectedErrorMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> CoinbaseCredentials.createCdp(cdpPrivateKey, cdpKeyName));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    private void doTestCreateLegacyInvalid(String apiKey, String apiSecret, String expectedErrorMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> CoinbaseCredentials.createLegacy(apiKey, apiSecret));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

}