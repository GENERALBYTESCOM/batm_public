package com.generalbytes.batm.server.services.amlkyc.verification.veriff.api;

import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

// https://developers.veriff.com/#endpoints
@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface IVeriffApi {

    static IVeriffApi create(String publicKey, VeriffDigest veriffDigest) {
        ClientConfig config = new ClientConfig();
        config.addDefaultParam(HeaderParam.class, "X-AUTH-CLIENT", publicKey);
        config.addDefaultParam(HeaderParam.class, "X-HMAC-SIGNATURE", veriffDigest);
        config.setJacksonObjectMapperFactory(new NonNullObjectMapperFactory()); // the API is sensitive about sending a json property with a null value vs. not sending the property at all
        return RestProxyFactory.createProxy(IVeriffApi.class, "https://stationapi.veriff.com", config);
    }

    /**
     * Creates a session with specified verification data.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sessions")
    CreateIdentityVerificationSessionResponse createSession(CreateIdentityVerificationSessionRequest request) throws IOException;


}
