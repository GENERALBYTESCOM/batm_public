package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.common.sumsub.api.SumsubApiFactory;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.SumsubTransferHandler;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.SumsubWebhookValidator;
import org.apache.commons.lang3.StringUtils;

/**
 * Factory for Sumsub Travel Rule provider.
 */
public class SumsubProviderFactory implements ITravelRuleProviderFactory {

    private final SumsubService sumsubService;
    private final SumsubApiService apiService;
    private final SumsubProviderRegistry providerRegistry;

    /**
     * Constructor.
     */
    public SumsubProviderFactory(TravelRuleExtensionContext extensionContext) {
        SumsubApiFactory apiFactory = new SumsubApiFactory();
        apiService = new SumsubApiService(apiFactory);
        SumsubValidator validator = new SumsubValidator();
        sumsubService = new SumsubService(extensionContext, apiService, validator);
        providerRegistry = new SumsubProviderRegistry();
        SumsubWebhookValidator webhookValidator = new SumsubWebhookValidator();
        ObjectMapper objectMapper = new ObjectMapper();
        SumsubTransferHandler.getInstance().init(providerRegistry, webhookValidator, objectMapper);
    }

    @Override
    public String getProviderName() {
        return SumsubProvider.NAME;
    }

    @Override
    public synchronized ITravelRuleProvider getProvider(ITravelRuleProviderCredentials credentials) {
        String vaspDid = credentials.getVaspDid();
        if (StringUtils.isBlank(vaspDid)) {
            return initializeSumsubProvider(credentials);
        }

        SumsubProvider alreadyInitializedSumsubProvider = providerRegistry.get(vaspDid);
        if (alreadyInitializedSumsubProvider != null) {
            alreadyInitializedSumsubProvider.updateCredentials(credentials);
            return alreadyInitializedSumsubProvider;
        }

        SumsubProvider sumsubProvider = initializeSumsubProvider(credentials);
        providerRegistry.put(vaspDid, sumsubProvider);

        return sumsubProvider;
    }

    private SumsubProvider initializeSumsubProvider(ITravelRuleProviderCredentials credentials) {
        return new SumsubProvider(credentials, sumsubService, apiService);
    }

}
