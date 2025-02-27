package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnfidoConfigurationServiceTest {
    @Mock
    private IExtensionContext ctx;
    @InjectMocks
    private OnfidoConfigurationService configurationService;

    @Test
    void testGetVerificationSiteCallbackUrl_fromConfig() {
        String configuredUrl = "https://my.server.com:1234/verification";
        when(ctx.getConfigProperty("onfido", "webhook.verificationSite", null)).thenReturn(configuredUrl);
        String verificationSiteCallbackUrl = configurationService.getVerificationSiteCallbackUrl();
        assertEquals(configuredUrl, verificationSiteCallbackUrl);
    }

    @Test
    void testGetVerificationSiteCallbackUrl_defaultValue() {
        when(ctx.getConfigProperty("onfido", "webhook.verificationSite", null)).thenReturn(null);
        when(ctx.getConfigFileContent("hostname")).thenReturn("default.server.com");
        String verificationSiteCallbackUrl = configurationService.getVerificationSiteCallbackUrl();
        assertEquals("https://default.server.com:7743", verificationSiteCallbackUrl);
    }

    @Test
    void testGetOnfidoCallbackUrl_fromConfig() {
        String configuredUrl = "https://my.server.com:1234/onfido";
        when(ctx.getConfigProperty("onfido", "webhook.onfido", null)).thenReturn(configuredUrl);
        String onfidoCallbackUrl = configurationService.getOnfidoCallbackUrl();
        assertEquals(configuredUrl, onfidoCallbackUrl);
    }

    @Test
    void testGetOnfidoCallbackUrl_defaultValue() {
        when(ctx.getConfigProperty("onfido", "webhook.onfido", null)).thenReturn(null);
        when(ctx.getConfigFileContent("hostname")).thenReturn("default.server.com");
        String onfidoCallbackUrl = configurationService.getOnfidoCallbackUrl();
        assertEquals("https://default.server.com:8743/server", onfidoCallbackUrl);
    }

    @Test
    void testHostnameNotConfigured() {
        when(ctx.getConfigFileContent("hostname")).thenReturn(null);
        try {
            configurationService.getVerificationSiteCallbackUrl();
        } catch (RuntimeException e) {
            assertEquals("Hostname not configured in /batm/config", e.getMessage());
        }
    }
}