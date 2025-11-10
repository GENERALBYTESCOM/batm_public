package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Service responsible for creating the configuration for Global Travel Rule (GTR) provider.
 */
@Slf4j
@AllArgsConstructor
public class GtrConfigurationService {

    private static final String DEFAULT_API_URL = "https://platform.globaltravelrule.com";
    private static final int DEFAULT_ACCESS_TOKEN_EXPIRATION_IN_MINUTES = 86400; // 60 days

    private final TravelRuleExtensionContext extensionContext;

    /**
     * Configuration file for Global Travel Rule (GTR) provider implementation, in '/batm/config' directory.
     */
    private static final String GTR_CONFIG_FILE_NAME = "gtr";

    /**
     * Get Global Travel Rule (GTR) configuration.
     *
     * @return Global Travel Rule (GTR) configuration.
     */
    public GtrConfiguration getGtrConfiguration() {
        GtrConfiguration gtrConfiguration = new GtrConfiguration();
        gtrConfiguration.setApiUrl(getApiUrl());
        gtrConfiguration.setRequestIdPrefix(getRequestIdPrefix());
        gtrConfiguration.setClientCertificatePath(getClientCertificatePath());
        gtrConfiguration.setClientCertificatePassphrase(getClientCertificatePassphrase());
        gtrConfiguration.setGtrServerTrustCertificatePath(getGtrServerTrustCertificatePath());
        gtrConfiguration.setAccessTokenExpirationInMinutes(getAccessTokenExpirationInMinutes());
        gtrConfiguration.setWebhooksEnabled(isGtrWebhooksEnabled());

        try {
            checkConfiguration(gtrConfiguration);
        } catch (TravelRuleProviderException e) {
            log.warn("GTR configuration is not valid - {}", e.getMessage());
            return null;
        }

        log.info("Using Global Travel Rule (GTR) configuration: {}", gtrConfiguration);
        return gtrConfiguration;
    }

    private void checkConfiguration(GtrConfiguration gtrConfiguration) {
        checkRequestIdPrefix(gtrConfiguration);
        checkClientCertificate(gtrConfiguration);
        checkGtrServerTrustCertificate(gtrConfiguration);
    }

    private String getApiUrl() {
        return getConfiguredProperty("apiUrl", DEFAULT_API_URL);
    }

    private String getRequestIdPrefix() {
        return getConfiguredProperty("requestIdPrefix", null);
    }

    private String getClientCertificatePath() {
        return getConfiguredProperty("clientCertificatePath", null);
    }

    private String getClientCertificatePassphrase() {
        return getConfiguredProperty("clientCertificatePassphrase", null);
    }

    private String getGtrServerTrustCertificatePath() {
        return getConfiguredProperty("gtrServerTrustCertificatePath", null);
    }

    private boolean isGtrWebhooksEnabled() {
        String webhooksEnabled = getConfiguredProperty("webhooksEnabled", null);
        return "true".equalsIgnoreCase(webhooksEnabled);
    }

    private int getAccessTokenExpirationInMinutes() {
        String expirationTime = getConfiguredProperty("accessTokenExpirationInMinutes", null);
        if (StringUtils.isNotBlank(expirationTime)) {
            try {
                return Integer.parseInt(expirationTime);
            } catch (NumberFormatException e) {
                log.warn("GTR configuration - invalid access token expiration, default value of {} minutes will be used",
                        DEFAULT_ACCESS_TOKEN_EXPIRATION_IN_MINUTES);
            }
        }

        return DEFAULT_ACCESS_TOKEN_EXPIRATION_IN_MINUTES;
    }

    private void checkRequestIdPrefix(GtrConfiguration gtrConfiguration) {
        String prefix = gtrConfiguration.getRequestIdPrefix();
        if (StringUtils.isBlank(prefix)) {
            throw new TravelRuleProviderException("request ID prefix is not set");
        }

        if (!prefix.matches("\\w+")) {
            throw new TravelRuleProviderException("request ID prefix contains invalid characters");
        }
    }

    private void checkClientCertificate(GtrConfiguration gtrConfiguration) {
        checkCertificate(gtrConfiguration.getClientCertificatePath(), ".p12", "client");
    }

    private void checkGtrServerTrustCertificate(GtrConfiguration gtrConfiguration) {
        checkCertificate(gtrConfiguration.getGtrServerTrustCertificatePath(), ".pem", "server trust");
    }

    private void checkCertificate(String relativePathInConfigDirectory,
                                  String expectedFileExtension,
                                  String certificateType
    ) {
        if (StringUtils.isBlank(relativePathInConfigDirectory)) {
            throw new TravelRuleProviderException(certificateType + " certificate is not set");
        }

        if (!relativePathInConfigDirectory.endsWith(expectedFileExtension)) {
            throw new TravelRuleProviderException(certificateType + " certificate must have '" + expectedFileExtension + "' extension");
        }

        if (!extensionContext.configFileExists(relativePathInConfigDirectory)) {
            throw new TravelRuleProviderException("GTR " + certificateType + " certificate not found");
        }
    }

    private String getConfiguredProperty(String key, String defaultValue) {
        return extensionContext.getConfigProperty(GTR_CONFIG_FILE_NAME, key, defaultValue);
    }

}
