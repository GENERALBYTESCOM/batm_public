package com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite;

import com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite.dto.RegisterApplicantRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite.dto.ServerAPIResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/verification")
public interface VerificationSiteAPI {

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ServerAPIResponse registerApplicant(RegisterApplicantRequest request);
}
