package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for creating the configuration for Sumsub provider.
 */
@Slf4j
@AllArgsConstructor
public class SumsubConfigurationService {

    /**
     * Configuration file for Sumsub provider implementation, in '/batm/config' directory.
     */
    private static final String SUMSUB_CONFIG_FILE_NAME = "sumsub";

    private final IExtensionContext extensionContext;

    /**
     * Sumsub configuration.
     *
     * @return Sumsub configuration.
     */
    public SumsubConfiguration getSumsubConfiguration() {
        SumsubConfiguration configuration = new SumsubConfiguration();
        configuration.setWebhooksEnabled(isSumsubWebhooksEnabled());

        log.info("Using Sumsub configuration: {}", configuration);
        return configuration;
    }

    private boolean isSumsubWebhooksEnabled() {
        String webhooksEnabled = getConfiguredProperty("webhooksEnabled", null);
        return "true".equalsIgnoreCase(webhooksEnabled);
    }

    private String getConfiguredProperty(String key, String defaultValue) {
        return extensionContext.getConfigProperty(SUMSUB_CONFIG_FILE_NAME, key, defaultValue);
    }

}
