package com.generalbytes.batm.server.extensions.travelrule.notabene.api;

import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneGenerateAccessTokenRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneGenerateAccessTokenResponse;
import si.mazi.rescu.HttpStatusIOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Provides access to Notabene Authentication endpoints.
 *
 * @see <a href="https://devx.notabene.id/reference/api-reference">Notabene Documentation</a>
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface NotabeneAuthApi {

    /**
     * Generate a new access token.
     *
     * @param request The request.
     * @return The response.
     * @throws HttpStatusIOException If the request fails.
     * @see <a href="https://devx.notabene.id/docs/auth0">Notabene Documentation</a>
     * @see NotabeneGenerateAccessTokenRequest
     * @see NotabeneGenerateAccessTokenResponse
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/oauth/token")
    NotabeneGenerateAccessTokenResponse generateAccessToken(NotabeneGenerateAccessTokenRequest request) throws HttpStatusIOException;

}
