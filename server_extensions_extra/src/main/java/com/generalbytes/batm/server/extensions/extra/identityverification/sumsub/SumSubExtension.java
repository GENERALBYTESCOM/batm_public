package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.common.sumsub.api.SumsubApiFactory;
import com.generalbytes.batm.server.extensions.common.sumsub.api.digest.SumsubSignatureDigest;
import com.generalbytes.batm.server.extensions.common.sumsub.api.digest.SumsubTimestampProvider;
import com.generalbytes.batm.server.extensions.util.ExtensionParameters;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.ISumSubApi;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The SumSubExtension class is an extension for integrating SumSub identity verification functionality.
 */
@Slf4j
public class SumSubExtension extends AbstractExtension {
    private static final String EXTENSION_PREFIX = "gbsumsub";
    // default session link expiry to seven day
    private static final int DEFAULT_LINK_EXPIRY_SECONDS = 7 * 24 * 3600;

    private Set<IRestService> restServices = null;

    private SumSubInstanceModule module;
    private SumsubApiFactory apiFactory;

    @Override
    public String getName() {
        return "BATM SumSub verification provider extension";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        module = SumSubInstanceModule.getInstance();
        module.addService(IExtensionContext.class, ctx);
        module.addService(SumSubWebhookParser.class, new SumSubWebhookParser());
        this.restServices = getServices();
        this.apiFactory = new SumsubApiFactory();
        log.info("SumSub extension initialized");
    }

    private Set<IRestService> getServices() {
        Set<IRestService> services = new HashSet<>();
        services.add(new SumSubWebhookRestService());
        return services;
    }

    @Override
    public Set<IRestService> getRestServices() {
        if (restServices == null) {
            throw new IllegalStateException("Extension not initialized yet");
        }
        return restServices;
    }

    @Override
    public IIdentityVerificationProvider createIdentityVerificationProvider(String colonDelimitedParameters, String gbApiKey) {
        ExtensionParameters params = ExtensionParameters.fromDelimited(colonDelimitedParameters);
        if (EXTENSION_PREFIX.equals(params.getPrefix())) {
            String token = getNonNullParameter(params.get(1), "token");
            String secret = getNonNullParameter(params.get(2), "secret");
            String webhookSecret = getNonNullParameter(params.get(3), "webhook secret");
            String levelName = getNonNullParameter(params.get(4), "level name");
            int linkExpiryTime = getLinkExpiryTime(params.get(5));


            return initializeProvider(token, secret, webhookSecret, levelName, linkExpiryTime);
        }
        return null;
    }

    private int getLinkExpiryTime(String linkExpiryTimeStr) {
        // get the larger value of DEFAULT_LINK_EXPIRY_SECONDS or the link expiry time passed in through parameters
        int linkExpiryTime = DEFAULT_LINK_EXPIRY_SECONDS;
        try {
            if (linkExpiryTimeStr != null && !linkExpiryTimeStr.isBlank()) {
                linkExpiryTime = Integer.parseInt(linkExpiryTimeStr);
                log.trace("SumSub applicant link expiry time set to {} seconds", linkExpiryTime);
            }
        } catch (NumberFormatException e) {
            log.warn("Could not parse {} to an integer, using default link expiry time of {} instead", linkExpiryTimeStr, linkExpiryTime);
        }
        return linkExpiryTime;
    }

    private String getNonNullParameter(String input, String parameterName) {
        return Objects.requireNonNull(input, String.format("SumSub parameter '%s' cannot be null", parameterName));
    }

    private SumSubIdentityVerificationProvider initializeProvider(String token,
                                                                  String secret,
                                                                  String webhookSecret,
                                                                  String levelName,
                                                                  int linkExpiryInSeconds) {
        ISumSubApi api = createApi(token, secret);
        SumSubApiService apiService = createSumSubApiService(api, levelName, linkExpiryInSeconds);
        SumSubWebhookProcessor webhookProcessor = createWebhookProcessor(webhookSecret, apiService);
        return new SumSubIdentityVerificationProvider(apiService, webhookProcessor);
    }

    private SumSubApiService createSumSubApiService(ISumSubApi api, String levelName, int linkExpiryInSeconds) {
        return new SumSubApiService(api, levelName, linkExpiryInSeconds);
    }

    private ISumSubApi createApi(String token, String secret) {
        return apiFactory.createSumsubIdentityVerificationApi(token, secret);
    }

    private SumSubWebhookProcessor createWebhookProcessor(String webhookSecret, SumSubApiService apiService) {
        return new SumSubWebhookProcessor(
                ctx, apiService, module.getSubWebhookParser(), new SumSubApplicantReviewedResultMapper(), webhookSecret
        );
    }

}
