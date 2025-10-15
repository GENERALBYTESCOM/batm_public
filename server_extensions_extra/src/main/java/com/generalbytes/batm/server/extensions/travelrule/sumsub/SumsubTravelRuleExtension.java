package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.SumsubWebhookRestService;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * Extension for Sumsub Travel Rule provider implementation.
 */
@Slf4j
public class SumsubTravelRuleExtension extends AbstractExtension {

    private SumsubConfiguration configuration;
    private TravelRuleExtensionContext extensionContext;

    @Override
    public void init(IExtensionContext ctx) {
        log.info("Initializing Sumsub Travel Rule extension");
        super.init(ctx);
        extensionContext = new TravelRuleExtensionContext(ctx);

        SumsubConfigurationService configurationService = new SumsubConfigurationService(extensionContext);
        configuration = configurationService.getSumsubConfiguration();
    }

    @Override
    public String getName() {
        return "Travel rule extension for Sumsub";
    }

    @Override
    public Set<ITravelRuleProviderFactory> getTravelRuleProviderFactories() {
        checkExtensionInitialization();

        return Set.of(new SumsubProviderFactory(extensionContext));
    }

    @Override
    public Set<IRestService> getRestServices() {
        checkExtensionInitialization();

        if (configuration.isWebhooksEnabled()) {
            log.info("Sumsub webhook initialized");
            return Set.of(new SumsubWebhookRestService());
        }

        log.info("Sumsub webhook disabled, to enable set 'webhooksEnabled' to 'true' in the Sumsub configuration");
        return Set.of();
    }

    private void checkExtensionInitialization() {
        if (configuration == null) {
            throw new IllegalStateException("Extension not initialized yet");
        }
    }

}
