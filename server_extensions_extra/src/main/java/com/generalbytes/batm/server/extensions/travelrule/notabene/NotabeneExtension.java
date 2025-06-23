package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * Extension for Notabene travel rule provider implementation.
 */
@Slf4j
public class NotabeneExtension extends AbstractExtension {

    /**
     * Configuration file for Notabene travel rule provider implementation, in '/batm/config' directory.
     */
    private static final String NOTABENE_CONFIG_FILE = "notabene";

    private Set<IRestService> restServices = null;
    private NotabeneConfiguration configuration = null;

    @Override
    public String getName() {
        return "Travel rule extension for Notabene";
    }

    @Override
    public void init(IExtensionContext ctx) {
        log.info("Initializing Notabene extension");
        super.init(ctx);
        configuration = getConfiguration();
        restServices = getServices();
        log.info("Notabene extension initialized");
    }

    @Override
    public Set<ITravelRuleProviderFactory> getTravelRuleProviderFactories() {
        if (configuration == null) {
            throw new IllegalStateException("Extension not initialized yet");
        }
        return Set.of(new NotabeneProviderFactory(configuration));
    }

    @Override
    public Set<IRestService> getRestServices() {
        if (restServices == null) {
            throw new IllegalStateException("Extension not initialized yet");
        }
        return restServices;
    }

    private Set<IRestService> getServices() {
        Set<IRestService> services = new HashSet<>();
        if (isNotabeneWebhooksEnabled()) {
            services.add(new NotabeneWebhookRestService());
            log.info("Notabene webhook initialized");
        } else {
            log.info("Notabene webhook disabled, to enable set 'webhooksEnabled' to 'true' in the Notabene configuration");
        }
        return services;
    }

    private NotabeneConfiguration getConfiguration() {
        NotabeneConfiguration notabeneConfiguration = new NotabeneConfiguration();
        notabeneConfiguration.setApiUrl(getNotabeneApiUrl());
        notabeneConfiguration.setAuthApiUrl(getNotabeneAuthApiUrl());
        notabeneConfiguration.setAutomaticApprovalOfOutgoingTransfersEnabled(isNotabeneAutomaticApprovalOfOutgoingTransfersEnabled());
        notabeneConfiguration.setMasterExtensionsUrl(getMasterExtensionUrl());
        log.info("Using Notabene configuration: {}", notabeneConfiguration);
        return notabeneConfiguration;
    }

    private String getNotabeneApiUrl() {
        return getConfiguredProperty("apiUrl", "https://api.notabene.id");
    }

    private String getNotabeneAuthApiUrl() {
        return getConfiguredProperty("authApiUrl", "https://auth.notabene.id");
    }

    private boolean isNotabeneAutomaticApprovalOfOutgoingTransfersEnabled() {
        String automaticApprovalEnabled = getConfiguredProperty("automaticallyApproveOutgoingTransfers", null);
        return "true".equalsIgnoreCase(automaticApprovalEnabled);
    }

    private boolean isNotabeneWebhooksEnabled() {
        String webhooksEnabled = getConfiguredProperty("webhooksEnabled", null);
        return "true".equalsIgnoreCase(webhooksEnabled);
    }

    private String getConfiguredProperty(String key, String defaultValue) {
        return ctx.getConfigProperty(NOTABENE_CONFIG_FILE, key, defaultValue);
    }

    private String getMasterExtensionUrl() {
        String masterExtensionsUrl = getConfiguredProperty("masterExtensionsUrl", null);
        if (masterExtensionsUrl == null) {
            String masterHost = ctx.getConfigProperty("network", "master_bind_ip", null);
            masterExtensionsUrl = String.format("https://%s:7743/extensions", masterHost);
        }
        return masterExtensionsUrl;
    }
}
