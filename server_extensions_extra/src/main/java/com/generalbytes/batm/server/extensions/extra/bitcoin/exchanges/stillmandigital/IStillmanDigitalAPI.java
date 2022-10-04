package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto.BalanceResponse;
import com.generalbytes.batm.server.extensions.util.net.RateLimitingInterceptor;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.Interceptor;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface IStillmanDigitalAPI {

    String API_EXPIRES_HEADER = "api-expires";

    static IStillmanDigitalAPI create(String apiKey, String apiSecret) throws GeneralSecurityException {
        return create(apiKey, apiSecret, "https://otc.stillmandigital.com");
    }

    static IStillmanDigitalAPI create(String apiKey, String apiSecret, String baseUrl) throws GeneralSecurityException {
        final ClientConfig config = new ClientConfig();
        config.addDefaultParam(HeaderParam.class, "api-key", apiKey);
        config.addDefaultParam(HeaderParam.class, API_EXPIRES_HEADER, new CurrentTimeFactory());
        config.addDefaultParam(HeaderParam.class, "api-signature", new StillmanDigitalDigest(apiSecret));
        Interceptor interceptor = new RateLimitingInterceptor(IStillmanDigitalAPI.class, 25, 30_000);
        return RestProxyFactory.createProxy(IStillmanDigitalAPI.class, baseUrl, config, interceptor);
    }

    @GET
    @Path("/trading/v1/balance")
    BalanceResponse getBalance() throws IOException;

}
