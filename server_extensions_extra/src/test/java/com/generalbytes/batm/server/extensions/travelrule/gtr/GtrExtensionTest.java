package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrExtensionTest {

    @Mock
    private IExtensionContext context;
    @Mock
    private GtrConfiguration gtrConfiguration;
    @Spy
    private GtrExtension gtrExtension;

    @Test
    void testGetName() {
        assertEquals("Travel rule extension for GTR", gtrExtension.getName());
    }

    @Test
    void testGetTravelRuleProviderFactories_withInit() {
        try (MockedConstruction<GtrConfigurationService> gtrConfigurationServiceMock = mockGtrConfigurationService();
             MockedConstruction<GtrProviderFactory> gtrProviderFactoryMock = mockConstruction(GtrProviderFactory.class)
        ) {
            gtrExtension.init(context);
            Set<ITravelRuleProviderFactory> factories = gtrExtension.getTravelRuleProviderFactories();

            assertEquals(1, factories.size());
            ITravelRuleProviderFactory gtrFactory = factories.iterator().next();
            assertTrue(gtrFactory instanceof GtrProviderFactory);

            List<GtrConfigurationService> constructedGtrConfigurationServices = gtrConfigurationServiceMock.constructed();
            assertEquals(1, constructedGtrConfigurationServices.size());

            List<GtrProviderFactory> constructedFactories = gtrProviderFactoryMock.constructed();
            assertEquals(1, constructedFactories.size());
        }
    }

    private MockedConstruction<GtrConfigurationService> mockGtrConfigurationService() {
        return mockConstruction(
                GtrConfigurationService.class, (mock, context) -> when(mock.getGtrConfiguration()).thenReturn(gtrConfiguration)
        );
    }

    @Test
    void testGetTravelRuleProviderFactories_invalidConfiguration() {
        Set<ITravelRuleProviderFactory> factories = gtrExtension.getTravelRuleProviderFactories();
        assertEquals(0, factories.size());
        verify(gtrExtension, never()).init(any());
    }

    @Test
    void testGetRestServices_notInitialized() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, gtrExtension::getRestServices);
        assertEquals("Extension not initialized yet", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testGetRestServices_initialized(boolean webhooksEnabled) {
        try (MockedConstruction<GtrConfigurationService> ignored = mockGtrConfigurationService()) {
            when(gtrConfiguration.isWebhooksEnabled()).thenReturn(webhooksEnabled);

            gtrExtension.init(context);

            Set<IRestService> restServices = gtrExtension.getRestServices();

            assertNotNull(restServices);
            if (webhooksEnabled) {
                assertEquals(1, restServices.size());
                assertInstanceOf(GtrWebhookRestService.class, restServices.iterator().next());
            } else {
                assertEquals(0, restServices.size());
            }
        }
    }

}