package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import com.generalbytes.batm.server.extensions.common.sumsub.api.CustomObjectMapperFactory;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.digest.SumSubSignatureDigest;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.digest.SumSubTimestampProvider;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantIdResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityApplicantRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityVerificationSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionInfoResponse;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
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

    String BASE_URL = "https://api.sumsub.com";

    String HEADER_APP_TOKEN = "X-App-Token";
    String HEADER_APP_SIG = "X-App-Access-Sig";
    String HEADER_APP_TS = "X-App-Access-Ts";

    /**
     * Creates an instance of the ISumSubApi interface for interacting with the SumSub API. This method
     * configures the necessary headers and settings required for API communication, including the
     * authentication token, timestamp provider, and signature digest.
     *
     * @param token           the API token used for authenticating requests to the SumSub API
     * @param signatureDigest an instance of SumSubSignatureDigest used to generate secure HMAC-SHA256 signatures
     * @param timestampDigest an instance of SumSubTimestampProvider to provide a timestamp for the requests
     * @return a configured instance of ISumSubApi ready for sending requests to the SumSub API
     */
    static ISumSubApi create(String token, SumSubSignatureDigest signatureDigest, SumSubTimestampProvider timestampDigest) {
        ClientConfig config = new ClientConfig();
        config.addDefaultParam(HeaderParam.class, HEADER_APP_TOKEN, token);
        config.addDefaultParam(HeaderParam.class, HEADER_APP_TS, timestampDigest);
        config.addDefaultParam(HeaderParam.class, HEADER_APP_SIG, signatureDigest);
        config.setJacksonObjectMapperFactory(new CustomObjectMapperFactory());
        return RestProxyFactory.createProxy(ISumSubApi.class, BASE_URL, config);
    }

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
