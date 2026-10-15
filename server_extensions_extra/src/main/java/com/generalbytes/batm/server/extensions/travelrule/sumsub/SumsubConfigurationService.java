package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
    /**
     * Key prefix of the originator (hot) wallet address, e.g. {@code originatorWalletAddress.BTC}.
     */
    private static final String ORIGINATOR_WALLET_ADDRESS_KEY_PREFIX = "originatorWalletAddress.";

    private final TravelRuleExtensionContext extensionContext;

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

    /**
     * Get the configured originator (hot) wallet address used for outgoing transfers of the given asset
     * ({@code originatorWalletAddress.<asset>}). The configuration file is read on each call, so changes apply without restart.
     *
     * @param asset Crypto asset of the transfer (e.g. BTC).
     * @return The configured address, or null if not configured.
     */
    public String getOriginatorWalletAddress(String asset) {
        if (StringUtils.isBlank(asset)) {
            return null;
        }
        String address = getConfiguredProperty(ORIGINATOR_WALLET_ADDRESS_KEY_PREFIX + asset, null);
        return StringUtils.isBlank(address) ? null : address.trim();
    }

    private boolean isSumsubWebhooksEnabled() {
        String webhooksEnabled = getConfiguredProperty("webhooksEnabled", null);
        return "true".equalsIgnoreCase(webhooksEnabled);
    }

    private String getConfiguredProperty(String key, String defaultValue) {
        return extensionContext.getConfigProperty(SUMSUB_CONFIG_FILE_NAME, key, defaultValue);
    }

}
