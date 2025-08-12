package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.exception.InvalidIdentityVerificationProviderException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.BaseWebhookBody;
import lombok.extern.slf4j.Slf4j;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * SumSubWebhookRestService provides REST endpoints for handling SumSub webhooks.
 * This class implements the IRestService interface and facilitates processing of received webhook
 * payloads, validating input, and delegating the data to the appropriate service layer.
 *
 * <p>An instance of SumSubInstanceModule is used to access necessary services such as the
 * SumSubWebhookParser and validation providers.
 */
@Slf4j
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SumSubWebhookRestService implements IRestService {
    public static final String DEFAULT_TEST_MESSAGE = "BATM server endpoint for SumSub webhooks";
    public static final String PREFIX_PATH = "/serverapi/apiv1/identity-check/sumsubwh/*";

    private final SumSubInstanceModule module;

    public SumSubWebhookRestService() {
        module = SumSubInstanceModule.getInstance();
    }

    @Override
    public String getPrefixPath() {
        return PREFIX_PATH;
    }

    @Override
    public Class getImplementation() {
        return getClass();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sumsubWebhookTest() {
        return DEFAULT_TEST_MESSAGE;
    }

    /**
     * Handles incoming webhook requests from the SumSub platform. Processes the raw payload
     * and header information, delegating the handling to a specific identity verification provider.
     * Returns an HTTP response based on the processing result.
     *
     * @param rawPayload the raw JSON payload received in the webhook request
     * @param payloadDigest the value of the "x-payload-digest" header, used for payload verification
     * @param payloadDigestAlg the value of the "X-Payload-Digest-Alg" header, specifying the digest algorithm
     * @return a {@link Response} object indicating the outcome of the webhook processing;
     *         may return a success status or an error response based on the processing result
     */
    @POST
    public Response sumsubWebhook(String rawPayload,
                                  @HeaderParam("x-payload-digest") String payloadDigest,
                                  @HeaderParam("X-Payload-Digest-Alg") String payloadDigestAlg) {

        String errorLabel = String.format("rawPayload: %s, payloadDigest: %s, alg: %s", rawPayload, payloadDigest, payloadDigestAlg);
        try {
            BaseWebhookBody baseWebhookBody = module.getSubWebhookParser().parse(rawPayload, BaseWebhookBody.class);
            getProvider(baseWebhookBody.getApplicantId()).processWebhook(rawPayload, baseWebhookBody, payloadDigest, payloadDigestAlg);
            return Response.ok().build();
        } catch (IdentityCheckWebhookException e) {
            log.error("Failed to process sumsub webhook: {}; {}", e.getResponseEntity(), errorLabel, e);
            return Response.status(e.getResponseStatus()).entity(e.getResponseEntity()).build();
        } catch (InvalidIdentityVerificationProviderException e) {
            log.warn(e.getMessage());
            // return OK so Sum&Sub doesn't resend the webhook
            return Response.ok().entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Failed to process sumsub webhook", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to process webhook").build();
        }
    }

    private SumSubIdentityVerificationProvider getProvider(String applicantId) throws InvalidIdentityVerificationProviderException {
        IIdentityVerificationProvider provider = module.getCtx().findIdentityVerificationProviderByApplicantId(applicantId);
        if (provider == null) {
            throw new InvalidIdentityVerificationProviderException("Cannot get Identity Verification Provider for " + applicantId);
        }
        if (!(provider instanceof SumSubIdentityVerificationProvider)) {
            throw new InvalidIdentityVerificationProviderException(
                    "Wrong type of Identity Verification Provider for " + applicantId + "; did the provider configuration change?"
            );
        }
        return (SumSubIdentityVerificationProvider) provider;
    }

}
