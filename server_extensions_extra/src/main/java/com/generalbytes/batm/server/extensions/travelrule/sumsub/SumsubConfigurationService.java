package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Key of the originator (hot) wallet address per crypto, e.g. {@code originator.wallet.BTC.address}.
     */
    private static final Pattern ORIGINATOR_WALLET_ADDRESS_KEY = Pattern.compile("^originator\\.wallet\\.([^.]+)\\.address$");

    private final TravelRuleExtensionContext extensionContext;

    /**
     * Sumsub configuration.
     *
     * @return Sumsub configuration.
     */
    public SumsubConfiguration getSumsubConfiguration() {
        SumsubConfiguration configuration = new SumsubConfiguration();
        configuration.setWebhooksEnabled(isSumsubWebhooksEnabled());
        configuration.setOriginatorWalletAddresses(getOriginatorWalletAddresses());

        log.info("Using Sumsub configuration: {}", configuration);
        return configuration;
    }

    private Map<String, String> getOriginatorWalletAddresses() {
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(StringUtils.defaultString(extensionContext.getConfigFileContent(SUMSUB_CONFIG_FILE_NAME))));
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to read originator wallet addresses from Sumsub configuration at /batm/config/sumsub", e);
            return Map.of();
        }

        Map<String, String> addresses = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            Matcher matcher = ORIGINATOR_WALLET_ADDRESS_KEY.matcher(key);
            String address = properties.getProperty(key);
            if (matcher.matches() && StringUtils.isNotBlank(address)) {
                addresses.put(matcher.group(1), address.trim());
            }
        }
        return Map.copyOf(addresses);
    }

    private boolean isSumsubWebhooksEnabled() {
        String webhooksEnabled = getConfiguredProperty("webhooksEnabled", null);
        return "true".equalsIgnoreCase(webhooksEnabled);
    }

    private String getConfiguredProperty(String key, String defaultValue) {
        return extensionContext.getConfigProperty(SUMSUB_CONFIG_FILE_NAME, key, defaultValue);
    }

}
