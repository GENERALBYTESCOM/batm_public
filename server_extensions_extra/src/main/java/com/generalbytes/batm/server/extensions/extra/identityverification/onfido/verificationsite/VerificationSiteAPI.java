package com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite;

import com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite.dto.RegisterApplicantRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite.dto.ServerAPIResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/verification")
public interface VerificationSiteAPI {

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ServerAPIResponse registerApplicant(RegisterApplicantRequest request);
}
