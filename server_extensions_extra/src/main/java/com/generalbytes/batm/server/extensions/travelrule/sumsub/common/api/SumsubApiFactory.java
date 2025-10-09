package com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api;

import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api.digest.SumsubSignatureDigest;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api.digest.SumsubTimestampProvider;
import jakarta.ws.rs.HeaderParam;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.clients.HttpConnectionType;

// TODO BATM-7594:
//  after the Sumsub TR implementation is completed and then moved to batm_public,
//  the 'com.generalbytes.batm.server.extensions.travelrule.sumsub.common' package must be unified
//  with the 'com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api' package

/**
 * Factory for creating Sumsub API proxies.
 */
public class SumsubApiFactory {

    private static final String BASE_URL = "https://api.sumsub.com";

    private static final String HEADER_APP_TOKEN = "X-App-Token";
    private static final String HEADER_APP_SIG = "X-App-Access-Sig";
    public static final String HEADER_APP_TS = "X-App-Access-Ts";

    /**
     * Creates an instance of the SumsubTravelRuleApi interface for interacting with the Sumsub API.
     * This method configures the necessary headers and settings required for API communication,
     * including the authentication token, timestamp provider and signature digest.
     *
     * @param token  The API token used for authenticating requests to the Sumsub API.
     * @param secret The secret key used for authenticating requests to the Sumsub API.
     * @return A configured instance of SumsubTravelRuleApi ready for sending requests to the Sumsub API.
     */
    public SumsubTravelRuleApi createSumsubTravelRuleApi(String token, String secret) {
        SumsubTimestampProvider timestampDigest = new SumsubTimestampProvider();
        SumsubSignatureDigest signatureDigest = new SumsubSignatureDigest(secret);

        ClientConfig config = new ClientConfig();
        config.addDefaultParam(HeaderParam.class, HEADER_APP_TOKEN, token);
        config.addDefaultParam(HeaderParam.class, HEADER_APP_TS, timestampDigest);
        config.addDefaultParam(HeaderParam.class, HEADER_APP_SIG, signatureDigest);
        config.setJacksonObjectMapperFactory(new CustomObjectMapperFactory());
        config.setConnectionType(HttpConnectionType.apache);
        return RestProxyFactory.createProxy(SumsubTravelRuleApi.class, BASE_URL, config);
    }

}
