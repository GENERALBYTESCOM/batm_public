package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumsubConfigurationServiceTest {

    @Mock
    private TravelRuleExtensionContext extensionContext;
    @InjectMocks
    private SumsubConfigurationService configurationService;

    @Test
    void testGetSumsubConfiguration_webhooksEnabled_enabled() {
        mockKeyProperty("webhooksEnabled", "true");

        SumsubConfiguration configuration = configurationService.getSumsubConfiguration();
        assertTrue(configuration.isWebhooksEnabled());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n", "invalid_boolean", "false" })
    void testGetSumsubConfiguration_webhooksEnabled_disabled(String webhooksEnabled) {
        mockKeyProperty("webhooksEnabled", webhooksEnabled);

        SumsubConfiguration configuration = configurationService.getSumsubConfiguration();
        assertFalse(configuration.isWebhooksEnabled());
    }

    @Test
    void testGetOriginatorWalletAddress() {
        mockKeyProperty("originatorWalletAddress.BTC", " bc1q_hot_wallet ");

        assertEquals("bc1q_hot_wallet", configurationService.getOriginatorWalletAddress("BTC"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  " })
    void testGetOriginatorWalletAddress_notConfigured(String configuredValue) {
        mockKeyProperty("originatorWalletAddress.BTC", configuredValue);

        assertNull(configurationService.getOriginatorWalletAddress("BTC"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  " })
    void testGetOriginatorWalletAddress_assetBlank(String asset) {
        assertNull(configurationService.getOriginatorWalletAddress(asset));
        verifyNoInteractions(extensionContext);
    }

    private void mockKeyProperty(String key, String returnValue) {
        when(extensionContext.getConfigProperty("sumsub", key, null)).thenReturn(returnValue);
    }

}