package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.SumsubWebhookRestService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumsubTravelRuleExtensionTest {

    @Mock
    private SumsubConfiguration configuration;
    @Mock
    private IExtensionContext context;
    @Spy
    private SumsubTravelRuleExtension sumsubExtension;

    @Test
    void testGetName() {
        String name = sumsubExtension.getName();

        assertEquals("Travel rule extension for Sumsub", name);
    }

    @Test
    void testGetTravelRuleProviderFactories_withInit() {
        try (MockedConstruction<SumsubConfigurationService> configurationServiceMock = mockSumsubConfigurationService()) {
            sumsubExtension.init(context);
            Set<ITravelRuleProviderFactory> factories = sumsubExtension.getTravelRuleProviderFactories();

            assertEquals(1, factories.size());
            assertInstanceOf(SumsubProviderFactory.class, factories.iterator().next());

            List<SumsubConfigurationService> constructedSumsubConfigurationServices = configurationServiceMock.constructed();
            assertEquals(1, constructedSumsubConfigurationServices.size());
        }
    }

    @Test
    void testGetTravelRuleProviderFactories_withoutInit() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, sumsubExtension::getTravelRuleProviderFactories);
        assertEquals("Extension not initialized yet", exception.getMessage());
        verify(sumsubExtension, never()).init(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testGetRestServices_withInit(boolean webhooksEnabled) {
        try (MockedConstruction<SumsubConfigurationService> ignored = mockSumsubConfigurationService()) {
            when(configuration.isWebhooksEnabled()).thenReturn(webhooksEnabled);

            sumsubExtension.init(context);
            Set<IRestService> restServices = sumsubExtension.getRestServices();

            if (webhooksEnabled) {
                assertEquals(1, restServices.size());
                assertInstanceOf(SumsubWebhookRestService.class, restServices.iterator().next());
            } else {
                assertEquals(0, restServices.size());
            }
        }
    }

    @Test
    void testGetRestServices_withoutInit() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, sumsubExtension::getRestServices);
        assertEquals("Extension not initialized yet", exception.getMessage());
        verify(sumsubExtension, never()).init(any());
    }

    private MockedConstruction<SumsubConfigurationService> mockSumsubConfigurationService() {
        return mockConstruction(
                SumsubConfigurationService.class, (mock, context) -> when(mock.getSumsubConfiguration()).thenReturn(configuration)
        );
    }

}