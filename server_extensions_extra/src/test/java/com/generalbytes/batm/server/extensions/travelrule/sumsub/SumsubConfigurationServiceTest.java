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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void testGetSumsubConfiguration_originatorWalletAddresses() {
        when(extensionContext.getConfigFileContent("sumsub")).thenReturn(String.join("\n",
                "webhooksEnabled=true",
                "originator.wallet.BTC.address= bc1q_hot_wallet ",
                "originator.wallet.USDT-TRON.address=T_hot_wallet",
                "originator.wallet.ETH.address=  ",
                "originator.wallet.LTC.type=custodial",
                "originatorWalletAddress.DOGE=D_old_key_format"
        ));

        SumsubConfiguration configuration = configurationService.getSumsubConfiguration();

        assertEquals(Map.of("BTC", "bc1q_hot_wallet", "USDT-TRON", "T_hot_wallet"), configuration.getOriginatorWalletAddresses());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "webhooksEnabled=true" })
    void testGetSumsubConfiguration_originatorWalletAddresses_notConfigured(String fileContent) {
        when(extensionContext.getConfigFileContent("sumsub")).thenReturn(fileContent);

        SumsubConfiguration configuration = configurationService.getSumsubConfiguration();

        assertEquals(Map.of(), configuration.getOriginatorWalletAddresses());
    }

    @Test
    void testGetSumsubConfiguration_originatorWalletAddresses_malformedFile() {
        when(extensionContext.getConfigFileContent("sumsub")).thenReturn("originator.wallet.BTC.address=\\uXYZ");

        SumsubConfiguration configuration = configurationService.getSumsubConfiguration();

        assertEquals(Map.of(), configuration.getOriginatorWalletAddresses());
    }

    private void mockKeyProperty(String key, String returnValue) {
        when(extensionContext.getConfigProperty("sumsub", key, null)).thenReturn(returnValue);
    }

}