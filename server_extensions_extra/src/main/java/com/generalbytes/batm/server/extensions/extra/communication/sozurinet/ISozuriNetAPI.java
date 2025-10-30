package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * SozuriNet API interface using si.mazi.rescu
 * API documentation: <a href="https://sozuri.net/docs/text">here</a>
 * Note: API returns JSON responses with status, message_id, and error information
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/v1")
public interface ISozuriNetAPI {

    /**
     * Send SMS using SozuriNet API v1
     *
     * @param request JSON request body containing project, from, to, campaign, channel, apiKey, message, and type
     * @return JSON response object from API
     * @throws IOException if communication fails
     */
    @POST
    @Path("/messaging")
    SozuriNetJsonResponse sendSms(SozuriNetSmsRequest request) throws IOException;

}
