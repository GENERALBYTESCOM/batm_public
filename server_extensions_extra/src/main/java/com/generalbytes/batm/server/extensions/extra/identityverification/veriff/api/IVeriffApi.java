package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api;

import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.SessionDecisionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.SubmitSessionRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.SubmitSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.UploadMediaRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.UploadMediaResponse;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.clients.HttpConnectionType;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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
        config.setConnectionType(HttpConnectionType.apache);
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

    /**
     * Uploads media to a session.
     * @param sessionId ID of the session
     * @param request Request with media data
     * @return Response with status
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sessions/{sessionId}/media")
    UploadMediaResponse uploadMedia(@PathParam("sessionId") String sessionId, UploadMediaRequest request) throws IOException;

    /**
     * Submits a session for verification.
     * @param sessionId ID of the session
     * @param request Request with submission data
     * @return Response with status
     */
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sessions/{sessionId}")
    SubmitSessionResponse submitSession(@PathParam("sessionId") String sessionId, SubmitSessionRequest request) throws IOException;

    /**
     * Gets the decision for a session.
     * @param sessionId ID of the session
     * @return Response with decision data
     */
    @GET
    @Path("/sessions/{sessionId}/decision")
    SessionDecisionResponse getSessionDecision(@PathParam("sessionId") String sessionId) throws IOException;
}
