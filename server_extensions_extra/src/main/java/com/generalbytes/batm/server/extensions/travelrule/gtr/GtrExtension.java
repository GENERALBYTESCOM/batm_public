package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * Extension for Global Travel Rule (GTR) provider implementation.
 */
@Slf4j
public class GtrExtension extends AbstractExtension {

    private GtrConfiguration configuration;
    private Set<IRestService> restServices;
    private TravelRuleExtensionContext extensionContext;

    @Override
    public String getName() {
        return "Travel rule extension for GTR";
    }

    @Override
    public void init(IExtensionContext ctx) {
        log.info("Initializing Global Travel Rule (GTR) extension");
        super.init(ctx);
        extensionContext = new TravelRuleExtensionContext(ctx);

        GtrConfigurationService configurationService = new GtrConfigurationService(extensionContext);
        configuration = configurationService.getGtrConfiguration();
        restServices = setupRestServices();

        log.info("Global Travel Rule (GTR) extension initialized");
    }

    @Override
    public Set<ITravelRuleProviderFactory> getTravelRuleProviderFactories() {
        if (configuration == null) {
            log.warn("GTR configuration is not valid, check the configuration file");
            return Set.of();
        }
        return Set.of(new GtrProviderFactory(configuration, extensionContext));
    }

    @Override
    public Set<IRestService> getRestServices() {
        if (restServices == null) {
            throw new IllegalStateException("Extension not initialized yet");
        }
        return restServices;
    }

    private Set<IRestService> setupRestServices() {
        Set<IRestService> services = new HashSet<>();
        if (configuration != null && configuration.isWebhooksEnabled()) {
            services.add(new GtrWebhookRestService());
            log.info("Global Travel Rule (GTR) webhook initialized");
        } else {
            log.info("Global Travel Rule (GTR) webhook disabled,"
                    + " to enable set 'webhooksEnabled' to 'true' in the Global Travel Rule (GTR) configuration");
        }
        return services;
    }
}
