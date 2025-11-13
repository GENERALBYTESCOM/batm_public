package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrConfigurationServiceTest {

    @Mock
    private TravelRuleExtensionContext extensionContext;
    @InjectMocks
    private GtrConfigurationService configurationService;

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "passphrase"})
    void testGetGtrConfiguration(String clientCertificatePassphrase) {
        mockValidConfiguration(clientCertificatePassphrase);

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertCommonValidGtrConfiguration(configuration, clientCertificatePassphrase);
        assertEquals(20, configuration.getAccessTokenExpirationInMinutes());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n", "invalid_integer" })
    void testGetGtrConfiguration_accessTokenExpiration_default(String accessTokenExpirationInMinutes) {
        mockValidConfiguration();
        mockKeyProperty("accessTokenExpirationInMinutes", accessTokenExpirationInMinutes);

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertCommonValidGtrConfiguration(configuration, "certPass");
        assertEquals(86400, configuration.getAccessTokenExpirationInMinutes());
    }

    private void assertCommonValidGtrConfiguration(GtrConfiguration configuration, String clientCertificatePassphrase) {
        assertEquals("https://platform.globaltravelrule.com", configuration.getApiUrl());
        assertEquals("/gtr/client_certificate.p12", configuration.getClientCertificatePath());
        assertEquals(clientCertificatePassphrase, configuration.getClientCertificatePassphrase());
        assertEquals("/gtr/gtr_server_trust_certificate.pem", configuration.getGtrServerTrustCertificatePath());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n", "pre fix", "prefix!", "pre-fix", " prefix", "prefix ", "pre/fix", "pre%fix", "pre.fix" })
    void testGetGtrConfiguration_invalid_requestIdPrefix(String requestIdPrefix) {
        mockValidConfiguration();
        mockKeyProperty("requestIdPrefix", requestIdPrefix);

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertNull(configuration);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testGetGtrConfiguration_invalid_clientCertificatePath(String clientCertificatePath) {
        mockValidConfiguration();
        mockKeyProperty("clientCertificatePath", clientCertificatePath);

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertNull(configuration);
        verify(extensionContext, never()).configFileExists(clientCertificatePath);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testGetGtrConfiguration_invalid_gtrServerTrustCertificatePath(String gtrServerTrustCertificatePath) {
        mockValidConfiguration();
        mockKeyProperty("gtrServerTrustCertificatePath", gtrServerTrustCertificatePath);

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertNull(configuration);
        verify(extensionContext, never()).configFileExists(gtrServerTrustCertificatePath);
    }

    @Test
    void testGetGtrConfiguration_invalid_clientCertificateExtension() {
        mockValidConfiguration();
        mockKeyProperty("clientCertificatePath", "/gtr/client_certificate.abc");

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertNull(configuration);
        verify(extensionContext, never()).configFileExists(anyString());
    }

    @Test
    void testGetGtrConfiguration_invalid_clientCertificate() {
        mockValidConfiguration();
        mockCertificate("/gtr/client_certificate.p12", false);

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertNull(configuration);
    }

    @Test
    void testGetGtrConfiguration_invalid_gtrServerTrustCertificateExtension() {
        mockValidConfiguration();
        mockKeyProperty("gtrServerTrustCertificatePath", "/gtr/gtr_server_trust_certificate.abc");

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertNull(configuration);
        verify(extensionContext, times(1)).configFileExists(anyString());
        verify(extensionContext, times(1)).configFileExists("/gtr/client_certificate.p12");
        verify(extensionContext, never()).configFileExists("/gtr/gtr_server_trust_certificate.abc");
    }

    @Test
    void testGetGtrConfiguration_invalid_gtrServerTrustCertificate() {
        mockValidConfiguration();
        mockCertificate("/gtr/gtr_server_trust_certificate.pem", false);

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertNull(configuration);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n", "invalid_boolean", "false" })
    void testGetGtrConfiguration_webhooksEnabled_disabled(String webhooksEnabled) {
        mockValidConfiguration();
        mockKeyProperty("webhooksEnabled", webhooksEnabled);

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertFalse(configuration.isWebhooksEnabled());
    }

    @Test
    void testGetGtrConfiguration_webhooksEnabled_enabled() {
        mockValidConfiguration();

        GtrConfiguration configuration = configurationService.getGtrConfiguration();
        assertCommonValidGtrConfiguration(configuration, "certPass");
        assertTrue(configuration.isWebhooksEnabled());
    }

    private void mockValidConfiguration() {
        mockValidConfiguration("certPass");
    }

    private void mockValidConfiguration(String clientCertificatePassphrase) {
        mockApiUrl();
        mockKeyProperty("requestIdPrefix", "Prefix_123");
        mockKeyProperty("clientCertificatePath", "/gtr/client_certificate.p12");
        mockKeyProperty("clientCertificatePassphrase", clientCertificatePassphrase);
        mockKeyProperty("gtrServerTrustCertificatePath", "/gtr/gtr_server_trust_certificate.pem");
        mockKeyProperty("accessTokenExpirationInMinutes", "20");
        mockKeyProperty("webhooksEnabled", "true");
        mockCertificate("/gtr/client_certificate.p12", true);
        mockCertificate("/gtr/gtr_server_trust_certificate.pem", true);
    }

    private void mockApiUrl() {
        when(extensionContext.getConfigProperty("gtr", "apiUrl", "https://platform.globaltravelrule.com"))
                .thenReturn("https://platform.globaltravelrule.com");
    }

    private void mockCertificate(String certificatePath, boolean returnValue) {
        lenient().when(extensionContext.configFileExists(certificatePath)).thenReturn(returnValue);
    }

    private void mockKeyProperty(String key, String returnValue) {
        when(extensionContext.getConfigProperty("gtr", key, null)).thenReturn(returnValue);
    }

}