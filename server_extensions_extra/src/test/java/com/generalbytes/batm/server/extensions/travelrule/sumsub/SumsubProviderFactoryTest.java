package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumsubProviderFactoryTest {

    @Mock
    private IExtensionContext extensionContext;
    @InjectMocks
    private SumsubProviderFactory factory;

    @Test
    void testGetName() {
        String name = factory.getProviderName();

        assertEquals("Sumsub Travel Rule Provider", name);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testGetProvider_withoutVasp(String vaspDid) {
        ITravelRuleProviderCredentials credentials = mockITravelRuleProviderCredentials(vaspDid);

        ITravelRuleProvider provider = factory.getProvider(credentials);
        assertTrue(provider instanceof SumsubProvider);
    }

    @Test
    void testGetProvider_withVasp() {
        ITravelRuleProviderCredentials credentials = mockITravelRuleProviderCredentials("vasp_did");

        ITravelRuleProvider provider = factory.getProvider(credentials);
        assertTrue(provider instanceof SumsubProvider);
    }

    @Test
    void testGetProvider_cachedVasp() {
        ITravelRuleProviderCredentials credentialsVasp1 = mockITravelRuleProviderCredentials("vasp_1");
        ITravelRuleProviderCredentials credentialsVasp2 = mockITravelRuleProviderCredentials("vasp_2");

        ITravelRuleProvider provider1 = factory.getProvider(credentialsVasp1);
        assertTrue(provider1 instanceof SumsubProvider);

        ITravelRuleProvider provider2 = factory.getProvider(credentialsVasp2);
        assertTrue(provider2 instanceof SumsubProvider);

        assertNotEquals(provider1, provider2);
        assertEquals(provider1, factory.getProvider(credentialsVasp1));
        assertEquals(provider2, factory.getProvider(credentialsVasp2));
    }

    private ITravelRuleProviderCredentials mockITravelRuleProviderCredentials(String vaspDid) {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        when(credentials.getVaspDid()).thenReturn(vaspDid);

        return credentials;
    }

}