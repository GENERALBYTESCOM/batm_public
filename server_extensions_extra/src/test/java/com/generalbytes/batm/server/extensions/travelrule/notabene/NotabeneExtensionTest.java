package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotabeneExtensionTest {

    private NotabeneExtension extension;

    @BeforeEach
    void setUp() {
        extension = new NotabeneExtension();
    }

    @Test
    void testGetName() {
        assertEquals("Travel rule extension for Notabene", extension.getName());
    }

    @Test
    void testInit() {
        IExtensionContext extensionContext = mock(IExtensionContext.class);
        when(extensionContext.getConfigProperty("notabene", "webhooksEnabled", null)).thenReturn("true");

        extension.init(extensionContext);

        verify(extensionContext).getConfigProperty("notabene", "apiUrl", "https://api.notabene.id");
        verify(extensionContext).getConfigProperty("notabene", "authApiUrl", "https://auth.notabene.id");
        verify(extensionContext).getConfigProperty("notabene", "webhooksEnabled", null);
        verify(extensionContext).getConfigProperty("notabene", "automaticallyApproveOutgoingTransfers", null);
        verify(extensionContext).getConfigProperty("network", "master_bind_ip", null);

        // after initialization, the extension should have a set of rest services
        Set<IRestService> restServices = extension.getRestServices();
        assertEquals(1, restServices.size());
        IRestService restService = restServices.iterator().next();
        assertInstanceOf(NotabeneWebhookRestService.class, restService);

        Set<ITravelRuleProviderFactory> travelRuleProviderFactories = extension.getTravelRuleProviderFactories();
        assertEquals(1, travelRuleProviderFactories.size());
        ITravelRuleProviderFactory providerFactory = travelRuleProviderFactories.iterator().next();
        assertInstanceOf(NotabeneProviderFactory.class, providerFactory);
    }

    @Test
    void testInit_webhookNotEnabled() {
        IExtensionContext extensionContext = mock(IExtensionContext.class);

        extension.init(extensionContext);

        // after initialization, the extension should have a set of rest services
        Set<IRestService> restServices = extension.getRestServices();
        assertTrue(restServices.isEmpty());
    }

    @Test
    void testGetTravelRuleProviderFactories_notInitialized() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> extension.getTravelRuleProviderFactories());
        assertEquals("Extension not initialized yet", exception.getMessage());
    }

    @Test
    void testGetRestServices_notInitialized() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> extension.getRestServices());
        assertEquals("Extension not initialized yet", exception.getMessage());
    }
}