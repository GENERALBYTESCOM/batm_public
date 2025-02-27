package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public class OnfidoConfigurationService {

    /**
     * Configuration file for Onfido in '/batm/config' directory.
     */
    private static final String ONFIDO_CONFIG_FILE = "onfido";

    private final IExtensionContext ctx;

    /**
     * Get callback URL for verification site webhook.
     *
     * @return URL from configuration file or default value based on configured hostname and port 7743
     */
    public String getVerificationSiteCallbackUrl() {
        return getFromConfigOrDefault(getVerificationSiteWebhookConfig(), this::getMasterServerApiAddress);
    }

    /**
     * Get callback URL for Onfido webhook.
     *
     * @return URL from configuration file or default value based on configured hostname and port 8743
     */
    public String getOnfidoCallbackUrl() {
        return getFromConfigOrDefault(getOnfidoWebhookConfig(), this::getMasterServerProxyAddress);
    }

    private String getMasterServerApiAddress() {
        return "https://" + getHostname() + ":" + 7743;
    }

    /**
     * @return this server address with hostname form /batm/config/hostname,
     * port number and path set to the one used by nginx proxy installed with {@code batm-manage install-reverse-proxy}.
     */
    private String getMasterServerProxyAddress() {
        return "https://" + getHostname() + ":" + 8743 + "/server";
    }

    private String getHostname() {
        String hostname = Strings.nullToEmpty(ctx.getConfigFileContent("hostname")).trim();
        if (hostname.isEmpty()) {
            throw new RuntimeException("Hostname not configured in /batm/config");
        }
        return hostname;
    }

    private String getFromConfigOrDefault(String configValue, Supplier<String> defaultValue) {
        return configValue != null ? configValue : defaultValue.get();
    }

    private String getOnfidoWebhookConfig() {
        return getConfiguredPropertyOrNull("webhook.onfido");
    }

    private String getVerificationSiteWebhookConfig() {
        return getConfiguredPropertyOrNull("webhook.verificationSite");
    }

    private String getConfiguredPropertyOrNull(String key) {
        return ctx.getConfigProperty(ONFIDO_CONFIG_FILE, key, null);
    }
}
