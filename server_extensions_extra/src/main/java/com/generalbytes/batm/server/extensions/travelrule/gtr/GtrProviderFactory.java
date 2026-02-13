package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrNetworkTestWebhookHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrReceiveTxIdWebhookHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrVerifyAddressWebhookHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.RequestIdGenerator;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrTransferHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrVerifyPiiWebhookHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.Curve25519Encryptor;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.HashingService;
import org.apache.commons.lang3.StringUtils;

/**
 * Factory for Global Travel Rule (GTR) provider.
 */
public class GtrProviderFactory implements ITravelRuleProviderFactory {

    private final GtrConfiguration configuration;
    private final GtrService gtrService;
    private final GtrTransferHandler gtrTransferHandler;
    private final GtrVerifyPiiWebhookHandler gtrVerifyPiiWebhookHandler;
    private final GtrProviderRegistry gtrProviderRegistry;
    private final GtrAuthService gtrAuthService;
    private final HashingService hashingService;

    /**
     * Constructor.
     *
     * @param configuration Configuration for access to Global Travel Rule (GTR) API.
     */
    public GtrProviderFactory(GtrConfiguration configuration, TravelRuleExtensionContext context) {
        this.configuration = configuration;

        GtrCertificateLoaderService gtrCertificateLoaderService = new GtrCertificateLoaderService(context);
        GtrApiFactory gtrApiFactory = new GtrApiFactory(configuration, gtrCertificateLoaderService);
        GtrApi gtrApi = gtrApiFactory.getGtrApi();
        gtrAuthService = new GtrAuthService(gtrApi, configuration);
        GtrApiService gtrApiService = new GtrApiService(gtrAuthService);
        GtrApiWrapper gtrApiWrapper = new GtrApiWrapper(gtrApi, gtrApiService);
        Curve25519Encryptor curve25519Encryptor = new Curve25519Encryptor();
        ObjectMapper objectMapper = new ObjectMapper();
        GtrObjectMapper gtrObjectMapper = new GtrObjectMapper(objectMapper);
        gtrTransferHandler = new GtrTransferHandler();
        gtrProviderRegistry = new GtrProviderRegistry();
        GtrVerifyPiiService verifyPiiService = new GtrVerifyPiiService(
                gtrApiWrapper, curve25519Encryptor, gtrObjectMapper, gtrTransferHandler, gtrProviderRegistry, context
        );
        GtrValidator gtrValidator = new GtrValidator();

        if (configuration.isWebhooksEnabled()) {
            gtrVerifyPiiWebhookHandler = new GtrVerifyPiiWebhookHandler(verifyPiiService, gtrValidator);
            registerWebhookRequestHandlers(context);
        } else {
            gtrVerifyPiiWebhookHandler = null;
        }

        RequestIdGenerator requestIdGenerator = new RequestIdGenerator(configuration);
        GtrVerifyAddressService verifyAddressService = new GtrVerifyAddressService(gtrApiWrapper);
        gtrService = new GtrService(
                gtrApiWrapper, requestIdGenerator, verifyAddressService, verifyPiiService,
                gtrTransferHandler, gtrValidator, curve25519Encryptor
        );

        hashingService = new HashingService();
    }

    private void registerWebhookRequestHandlers(TravelRuleExtensionContext context) {
        GtrWebhookHandlerService handlerService = GtrWebhookHandlerService.getInstance();

        handlerService.registerHandler(GtrApiConstants.CallbackType.NETWORK_TEST, new GtrNetworkTestWebhookHandler());
        handlerService.registerHandler(GtrApiConstants.CallbackType.ADDRESS_VERIFICATION, new GtrVerifyAddressWebhookHandler(context));
        handlerService.registerHandler(GtrApiConstants.CallbackType.RECEIVE_TX_ID, new GtrReceiveTxIdWebhookHandler());
        handlerService.registerHandler(GtrApiConstants.CallbackType.PII_VERIFICATION, gtrVerifyPiiWebhookHandler);
    }

    @Override
    public String getProviderName() {
        return GtrProvider.NAME;
    }

    @Override
    public synchronized ITravelRuleProvider getProvider(ITravelRuleProviderCredentials credentials) {
        String vaspDid = credentials.getVaspDid();
        if (StringUtils.isBlank(vaspDid)) {
            return initializeGtrProvider(credentials);
        }

        GtrProvider alreadyInitializedGtrProvider = gtrProviderRegistry.get(vaspDid);
        if (alreadyInitializedGtrProvider != null) {
            GtrCredentials gtrCredentials = createGtrCredentials(credentials);
            alreadyInitializedGtrProvider.updateCredentials(gtrCredentials);

            return alreadyInitializedGtrProvider;
        }

        GtrProvider gtrProvider = initializeGtrProvider(credentials);
        gtrProviderRegistry.put(vaspDid, gtrProvider);

        return gtrProvider;
    }

    private GtrProvider initializeGtrProvider(ITravelRuleProviderCredentials credentials) {
        GtrCredentials gtrCredentials = createGtrCredentials(credentials);
        return new GtrProvider(gtrCredentials, configuration, gtrService, gtrTransferHandler, gtrVerifyPiiWebhookHandler, gtrAuthService);
    }

    private GtrCredentials createGtrCredentials(ITravelRuleProviderCredentials credentials) {
        return new GtrCredentials(credentials, hashingService::computeSha512);
    }

}
