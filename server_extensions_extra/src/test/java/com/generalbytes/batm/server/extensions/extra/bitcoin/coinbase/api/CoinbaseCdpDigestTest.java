package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.CoinbaseException;
import org.junit.Before;
import org.junit.Test;
import si.mazi.rescu.RestInvocation;

import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoinbaseCdpDigestTest {

    private static final String FAKE_PRIVATE_KEY = "-----BEGIN EC PRIVATE KEY-----\n" +
        "MHcCAQEEIMY4fXcqiZaKr+30GdkG4mNc30m05dK68DCXBzqsqSdfoAoGCCqGSM49\\n" +
        "AwEHoUQDQgAEjUwGV8F0rPqqxNMjCJFvRW+ST6PFpopTxIv3aWY3G5+ig7bqDss0\\n" +
        "L7E2ZmsrtSMIt51scGbS54wpAxhp2IiWdg==\n" +
        "-----END EC PRIVATE KEY-----";
    private static final String FAKE_KEY_NAME = "organizations/23db6f78-1a78-401a-a42f-4dbfb28a7496/" +
        "apiKeys/8880ecd5-225f-43ef-a51f-8a9d191a261d";

    private RestInvocation restInvocation;

    @Before
    public void init() {
        restInvocation = mock(RestInvocation.class);
    }

    @Test
    public void testCreate_invalid() {
        doTestCreate_invalid(null, null, "cdpPrivateKey cannot be null");
        doTestCreate_invalid(null, "keyName", "cdpPrivateKey cannot be null");
        doTestCreate_invalid("privateKey", null, "cdpKeyName cannot be null");
    }

    private void doTestCreate_invalid(String cdpPrivateKey, String cdpKeyName, String expectedErrorMessage) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> CoinbaseCdpDigest.create(cdpPrivateKey, cdpKeyName));

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void testDigestParams_valid() {
        String mockUrl = "https://api.coinbase.com/v2/accounts?type=deposit";
        String mockQueryString = "type=deposit";
        String mockHttpMethod = "GET";

        when(restInvocation.getInvocationUrl()).thenReturn(mockUrl);
        when(restInvocation.getQueryString()).thenReturn(mockQueryString);
        when(restInvocation.getHttpMethod()).thenReturn(mockHttpMethod);

        CoinbaseCdpDigest digest = CoinbaseCdpDigest.create(FAKE_PRIVATE_KEY, FAKE_KEY_NAME);

        String jwt = digest.digestParams(restInvocation);

        assertNotNull(jwt);
        assertTrue("JWT token should start with 'Bearer '", jwt.startsWith("Bearer "));

        String[] jwtParts = jwt.replace("Bearer ", "").split("\\.");
        assertEquals("JWT token should have three parts (header, payload, signature)", 3, jwtParts.length);

        String header = new String(Base64.getDecoder().decode(jwtParts[0]));
        assertTrue(header.contains("\"typ\":\"JWT\""));
        assertTrue(header.contains("\"alg\":\"ES256\""));
        assertTrue(header.contains("\"kid\":\"" + FAKE_KEY_NAME + "\""));

        String payload = new String(Base64.getDecoder().decode(jwtParts[1]));
        assertTrue(payload.contains("\"iss\":\"cdp\""));
        assertTrue(payload.contains("\"sub\":\"" + FAKE_KEY_NAME + "\""));
        assertTrue(payload.contains("\"uri\":\"GET api.coinbase.com/v2/accounts\""));
    }

    @Test
    public void testDigestParams_invalidPrivateKey() {
        String mockUrl = "https://api.coinbase.com/v2/accounts?type=deposit";
        String mockQueryString = "type=deposit";
        String mockHttpMethod = "GET";

        when(restInvocation.getInvocationUrl()).thenReturn(mockUrl);
        when(restInvocation.getQueryString()).thenReturn(mockQueryString);
        when(restInvocation.getHttpMethod()).thenReturn(mockHttpMethod);

        CoinbaseCdpDigest digest = CoinbaseCdpDigest.create("invalidPrivateKey", FAKE_KEY_NAME);

        CoinbaseException exception = assertThrows(CoinbaseException.class, () -> digest.digestParams(restInvocation));

        assertEquals("Failed to generate JWT for request: GET api.coinbase.com/v2/accounts", exception.getMessage());
    }

}