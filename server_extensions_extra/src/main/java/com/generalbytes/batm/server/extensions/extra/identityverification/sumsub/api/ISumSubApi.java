package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantIdResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityApplicantRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityVerificationSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionInfoResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * The ISumSubApi interface defines the contract for interacting with the SumSub API.
 * It provides methods to handle various operations like managing applicants, creating verification sessions,
 * and retrieving relevant inspection information required for the SumSub platform.
 *
 * <p><a href="https://docs.sumsub.com/reference/about-sumsub-api">See more about the API</a>
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface ISumSubApi {
    /**
     * Gets the applicant data by external ID.
     *
     * <p><a href="https://docs.sumsub.com/reference/get-applicant-data-via-externaluserid">documentation</a>
     */
    @GET
    @Path("/resources/applicants/-;externalUserId={externalUserId}/one")
    ApplicantInfoResponse getApplicantByExternalId(@PathParam("externalUserId") String externalUserId) throws IOException;

    /**
     * Creates an applicant.
     *
     * <p><a href="https://docs.sumsub.com/reference/create-applicant">documentation</a>
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/resources/applicants")
    ApplicantIdResponse createApplicant(
            CreateIdentityApplicantRequest request,
            @QueryParam("levelName") String levelName) throws IOException;

    /**
     * Creates a session.
     *
     * <p><a href="https://docs.sumsub.com/reference/generate-websdk-external-link">documentation</a>
     */
    @POST
    @Path("/resources/sdkIntegrations/levels/{levelName}/websdkLink")
    CreateIdentityVerificationSessionResponse createSession(
            @PathParam("levelName") String levelName,
            @QueryParam("ttlInSecs") int ttlInSecs,
            @QueryParam("externalUserId") String externalUserId,
            @QueryParam("lang") String lang) throws IOException;

    /**
     * Gets the documents requested in the flow for the applicant.
     */
    @GET
    @Path("/resources/inspections/{inspectionId}")
    InspectionInfoResponse getInspectionInfo(@PathParam("inspectionId") String inspectionId) throws IOException;
}
