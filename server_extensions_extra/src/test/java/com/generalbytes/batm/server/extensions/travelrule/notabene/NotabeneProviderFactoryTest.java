package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotabeneProviderFactoryTest {
    @Mock
    private NotabeneConfiguration configuration;
    @InjectMocks
    private NotabeneProviderFactory notabeneProviderFactory;

    @Test
    void testGetProviderName() {
        assertEquals(NotabeneTravelRuleProvider.NAME, notabeneProviderFactory.getProviderName());
    }

    @Test
    void testGetProvider() {
        ITravelRuleProviderCredentials credentials = createCredentials("vaspDid");

        ITravelRuleProvider provider = notabeneProviderFactory.getProvider(credentials);
        assertNotNull(provider);
        assertInstanceOf(NotabeneTravelRuleProvider.class, provider);

        ITravelRuleProvider repeatedCall = notabeneProviderFactory.getProvider(credentials);
        assertEquals(provider, repeatedCall);
    }

    @Test
    void testGetProvider_differentProvidersPerVaspDid() {
        ITravelRuleProviderCredentials credentials1 = createCredentials("vaspDid1");
        ITravelRuleProviderCredentials credentials2 = createCredentials("vaspDid2");

        ITravelRuleProvider provider1 = notabeneProviderFactory.getProvider(credentials1);
        ITravelRuleProvider provider2 = notabeneProviderFactory.getProvider(credentials2);
        assertNotEquals(provider1, provider2);
    }

    @Test
    void testGetProvider_unknownVaspDid() {
        ITravelRuleProviderCredentials credentials1 = createCredentials(null);
        ITravelRuleProviderCredentials credentials2 = createCredentials(null);

        ITravelRuleProvider provider1 = notabeneProviderFactory.getProvider(credentials1);
        ITravelRuleProvider provider2 = notabeneProviderFactory.getProvider(credentials2);
        assertNotEquals(provider1, provider2);
    }

    private static ITravelRuleProviderCredentials createCredentials(String vaspDid) {
        ITravelRuleProviderCredentials credentials1 = mock(ITravelRuleProviderCredentials.class);
        when(credentials1.getVaspDid()).thenReturn(vaspDid);
        return credentials1;
    }
}