package com.generalbytes.batm.server.extensions.travelrule.gtr;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class GtrProviderRegistryTest {

    @Test
    void testGet_providerDoesNotExist() {
        GtrProviderRegistry registry = new GtrProviderRegistry();

        GtrProvider gtrProvider = registry.get("vaspDid");

        assertNull(gtrProvider);
    }

    @Test
    void testPut() {
        GtrProvider gtrProvider1 = Mockito.mock(GtrProvider.class);
        GtrProvider gtrProvider2 = Mockito.mock(GtrProvider.class);

        GtrProviderRegistry registry = new GtrProviderRegistry();
        registry.put("vaspDid_1", gtrProvider1);
        registry.put("vaspDid_2", gtrProvider2);

        GtrProvider returnedGtrProvider1 = registry.get("vaspDid_1");
        assertSame(gtrProvider1, returnedGtrProvider1);

        GtrProvider returnedGtrProvider2 = registry.get("vaspDid_2");
        assertSame(gtrProvider2, returnedGtrProvider2);
    }

}