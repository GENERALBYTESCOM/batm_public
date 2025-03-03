package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OnfidoConfigurationServiceTest {
    private IExtensionContext ctx;
    private OnfidoConfigurationService configurationService;

    @Before
    public void setUp() {
        ctx = mock(IExtensionContext.class);
        configurationService = new OnfidoConfigurationService(ctx);
    }

    @Test
    public void testGetVerificationSiteCallbackUrl_fromConfig() {
        String configuredUrl = "https://my.server.com:1234/verification";
        when(ctx.getConfigProperty("onfido", "webhook.verificationSite", null)).thenReturn(configuredUrl);
        String verificationSiteCallbackUrl = configurationService.getVerificationSiteCallbackUrl();
        assertEquals(configuredUrl, verificationSiteCallbackUrl);
    }

    @Test
    public void testGetVerificationSiteCallbackUrl_defaultValue() {
        when(ctx.getConfigProperty("onfido", "webhook.verificationSite", null)).thenReturn(null);
        when(ctx.getConfigFileContent("hostname")).thenReturn("default.server.com");
        String verificationSiteCallbackUrl = configurationService.getVerificationSiteCallbackUrl();
        assertEquals("https://default.server.com:7743", verificationSiteCallbackUrl);
    }

    @Test
    public void testGetOnfidoCallbackUrl_fromConfig() {
        String configuredUrl = "https://my.server.com:1234/onfido";
        when(ctx.getConfigProperty("onfido", "webhook.onfido", null)).thenReturn(configuredUrl);
        String onfidoCallbackUrl = configurationService.getOnfidoCallbackUrl();
        assertEquals(configuredUrl, onfidoCallbackUrl);
    }

    @Test
    public void testGetOnfidoCallbackUrl_defaultValue() {
        when(ctx.getConfigProperty("onfido", "webhook.onfido", null)).thenReturn(null);
        when(ctx.getConfigFileContent("hostname")).thenReturn("default.server.com");
        String onfidoCallbackUrl = configurationService.getOnfidoCallbackUrl();
        assertEquals("https://default.server.com:8743/server", onfidoCallbackUrl);
    }

    @Test
    public void testHostnameNotConfigured() {
        when(ctx.getConfigFileContent("hostname")).thenReturn(null);
        try {
            configurationService.getVerificationSiteCallbackUrl();
        } catch (RuntimeException e) {
            assertEquals("Hostname not configured in /batm/config", e.getMessage());
        }
    }
}