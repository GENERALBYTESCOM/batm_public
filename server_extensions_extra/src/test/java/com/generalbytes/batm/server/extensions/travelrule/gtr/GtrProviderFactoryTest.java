package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrReceiveTxIdWebhookHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrVerifyAddressWebhookHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrVerifyPiiWebhookHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrProviderFactoryTest {

    @Mock
    private GtrConfiguration configuration;
    @Mock
    private TravelRuleExtensionContext extensionContext;
    @Mock
    private GtrApi gtrApi;
    private GtrProviderFactory factory;

    @BeforeEach
    void init() {
        try (MockedConstruction<GtrApiFactory> gtrApiFactoryMock = mockGtrApiFactory()) {
            factory = new GtrProviderFactory(configuration, extensionContext);
        }
    }

    private MockedConstruction<GtrApiFactory> mockGtrApiFactory() {
        return mockConstruction(GtrApiFactory.class, (mock, context) -> when(mock.getGtrApi()).thenReturn(gtrApi));
    }

    @Test
    void testConstruction_registerWebhookRequestHandlers_webhookEnabled() {
        try (MockedConstruction<GtrApiFactory> gtrApiFactoryMock = mockGtrApiFactory();
             MockedStatic<GtrWebhookHandlerService> webhookHandlerServiceStaticMock = mockStatic(GtrWebhookHandlerService.class)
        ) {
            GtrWebhookHandlerService webhookHandlerService = Mockito.mock(GtrWebhookHandlerService.class);

            when(configuration.isWebhooksEnabled()).thenReturn(true);
            webhookHandlerServiceStaticMock.when(GtrWebhookHandlerService::getInstance).thenReturn(webhookHandlerService);

            new GtrProviderFactory(configuration, extensionContext);

            verify(webhookHandlerService, times(1))
                    .registerHandler(eq(GtrApiConstants.CallbackType.ADDRESS_VERIFICATION), any(GtrVerifyAddressWebhookHandler.class));
            verify(webhookHandlerService, times(1))
                    .registerHandler(eq(GtrApiConstants.CallbackType.RECEIVE_TX_ID), any(GtrReceiveTxIdWebhookHandler.class));
            verify(webhookHandlerService, times(1))
                    .registerHandler(eq(GtrApiConstants.CallbackType.PII_VERIFICATION), any(GtrVerifyPiiWebhookHandler.class));
        }
    }

    @Test
    void testConstruction_registerWebhookRequestHandlers_webhookDisabled() {
        try (MockedConstruction<GtrApiFactory> gtrApiFactoryMock = mockGtrApiFactory();
             MockedStatic<GtrWebhookHandlerService> webhookHandlerServiceStaticMock = mockStatic(GtrWebhookHandlerService.class)
        ) {
            when(configuration.isWebhooksEnabled()).thenReturn(false);

            new GtrProviderFactory(configuration, extensionContext);

            webhookHandlerServiceStaticMock.verifyNoInteractions();
        }
    }

    @Test
    void testGetProviderName() {
        assertEquals(GtrProvider.NAME, factory.getProviderName());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testGetProvider_withoutVasp(String vaspDid) {
        ITravelRuleProviderCredentials credentials = mockITravelRuleProviderCredentials(vaspDid);

        ITravelRuleProvider provider = factory.getProvider(credentials);
        assertTrue(provider instanceof GtrProvider);
    }

    @Test
    void testGetProvider_withVasp() {
        ITravelRuleProviderCredentials credentials = mockITravelRuleProviderCredentials("vaspDid");

        ITravelRuleProvider provider = factory.getProvider(credentials);
        assertTrue(provider instanceof GtrProvider);
    }

    @Test
    void testGetProvider_cachedVasp() {
        ITravelRuleProviderCredentials credentialsVasp1 = mockITravelRuleProviderCredentials("vasp_1");
        ITravelRuleProviderCredentials credentialsVasp2 = mockITravelRuleProviderCredentials("vasp_2");

        ITravelRuleProvider provider1 = factory.getProvider(credentialsVasp1);
        assertTrue(provider1 instanceof GtrProvider);

        ITravelRuleProvider provider2 = factory.getProvider(credentialsVasp2);
        assertTrue(provider2 instanceof GtrProvider);

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