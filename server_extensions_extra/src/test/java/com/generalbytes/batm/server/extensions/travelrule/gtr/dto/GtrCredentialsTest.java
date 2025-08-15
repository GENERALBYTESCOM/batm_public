package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GtrCredentialsTest {

    @Test
    void testConstructor() {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        when(credentials.getVaspDid()).thenReturn("vasp_code");
        when(credentials.getClientId()).thenReturn("access_key");
        when(credentials.getPublicKey()).thenReturn("curve_public_key");
        when(credentials.getPrivateKey()).thenReturn("curve_private_key");

        GtrCredentials gtrCredentials = new GtrCredentials(credentials, clientSecret -> "signed_secret_key");

        assertEquals("vasp_code", gtrCredentials.getVaspCode());
        assertEquals("access_key", gtrCredentials.getAccessKey());
        assertEquals("signed_secret_key", gtrCredentials.getSignedSecretKey());
        assertEquals("curve_public_key", gtrCredentials.getCurvePublicKey());
        assertEquals("curve_private_key", gtrCredentials.getCurvePrivateKey());
    }

}