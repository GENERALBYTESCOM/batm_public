package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class SumsubProviderRegistryTest {

    @Test
    void testGet_providerDoesNotExist() {
        SumsubProviderRegistry registry = new SumsubProviderRegistry();

        SumsubProvider sumsubProvider = registry.get("vaspDid");

        assertNull(sumsubProvider);
    }

    @Test
    void testPut() {
        SumsubProvider sumsubProvider1 = Mockito.mock(SumsubProvider.class);
        SumsubProvider sumsubProvider2 = Mockito.mock(SumsubProvider.class);

        SumsubProviderRegistry registry = new SumsubProviderRegistry();
        registry.put("vaspDid_1", sumsubProvider1);
        registry.put("vaspDid_2", sumsubProvider2);

        SumsubProvider returnedSumsubProvider1 = registry.get("vaspDid_1");
        assertSame(sumsubProvider1, returnedSumsubProvider1);

        SumsubProvider returnedSumsubProvider2 = registry.get("vaspDid_2");
        assertSame(sumsubProvider2, returnedSumsubProvider2);
    }

}