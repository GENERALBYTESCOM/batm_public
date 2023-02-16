package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api;

import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

// https://developers.veriff.com/#endpoints
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface IVeriffApi {

    String BASE_URL = "https://stationapi.veriff.com/v1";
    String HEADER_PUBLIC_KEY = "X-AUTH-CLIENT";
    String HEADER_SIGNATURE = "X-HMAC-SIGNATURE";


    static IVeriffApi create(String publicKey, VeriffDigest veriffDigest) {
        ClientConfig config = new ClientConfig();
        config.addDefaultParam(HeaderParam.class, HEADER_PUBLIC_KEY, publicKey);
        config.addDefaultParam(HeaderParam.class, HEADER_SIGNATURE, veriffDigest);
        config.setJacksonObjectMapperFactory(new NonNullObjectMapperFactory()); // the API is sensitive about sending a json property with a null value vs. not sending the property at all
        return RestProxyFactory.createProxy(IVeriffApi.class, BASE_URL, config);
    }

    /**
     * Creates a session with specified verification data.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sessions")
    CreateIdentityVerificationSessionResponse createSession(CreateIdentityVerificationSessionRequest request) throws IOException;

    /**
     * Gets list of media objects (metadata, not the actual file content) with the given sessionId
     */
    @GET
    @Path("/sessions/{sessionId}/media")
    SessionMediaInfo getSessionMediaInfo(@PathParam("sessionId") String sessionId) throws IOException;

}
