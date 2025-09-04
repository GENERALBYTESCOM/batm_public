package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.IdentityCheckWebhookRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VeriffRestService implements IRestService {
    private static final Logger log = LoggerFactory.getLogger(VeriffRestService.class);
    private static final VeriffWebhookParser veriffWebhookParser = new VeriffWebhookParser();

    @Override
    public String getPrefixPath() {
        // absolute path to set the same URL as it was before moving to extensions; don't use this in new providers
        return "/serverapi/apiv1/identity-check/veriffwh/*";
    }

    @Override
    public Class getImplementation() {
        return getClass();
    }

    /**
     * The GET version is here just so you can test your URL is correct in a browser.
     * The URL where this endpoint is available needs to be configured in
     * Veriff Station under Integrations -> your integration -> Settings -> Webhook Decisions URL,
     *
     * https://hostname:7743/serverapi/apiv1/identity-check/veriffwh
     * https://hostname:8743/server/serverapi/apiv1/identity-check/veriffwh (when using `batm-manage install-reverse-proxy`)
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String veriffWebhookTest() {
        return "BATM server endpoint for Veriff webhooks";
    }

    /**
     * Processes Decision Webhook from Veriff.
     * Called by Veriff when the verification is finished and it contains the verification result.
     * <p>
     * Used on both standalone and global server with operator's custom Veriff API keys (taken from Organization).
     *
     * https://hostname:7743/serverapi/apiv1/identity-check/veriffwh
     * https://hostname:8743/server/serverapi/apiv1/identity-check/veriffwh (when using `batm-manage install-reverse-proxy`)
     *
     * @return 200 HTTP response in case of a successfully processed webhook.
     * Any non-200 response will make Veriff retry once in every hour for up to a week
     */
    @POST
    public Response veriffWebhook(String rawPayload, @HeaderParam("x-auth-client") String apiKey, @HeaderParam("x-hmac-signature") String signature) {
        String errorLabel = String.format("rawPayload: %s, apiKey: %s, signature: %s", rawPayload, apiKey, signature);
        return IdentityCheckWebhookRunnable.getResponse(errorLabel, () -> {
            log.info("Veriff webhook received, getting verification provider");
            String applicantId = veriffWebhookParser.getApplicantId(rawPayload);
            getProvider(applicantId).processWebhookEvent(rawPayload, signature);
        });
    }

    private VeriffIdentityVerificationProvider getProvider(String applicantId) throws IdentityCheckWebhookException {
        IExtensionContext ctx = VeriffExtension.getExtensionContext();
        IIdentityVerificationProvider provider = ctx.findIdentityVerificationProviderByApplicantId(applicantId);
        if (provider == null) {
            throw new IdentityCheckWebhookException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), "",
                "Cannot get Identity Verification Provider for " + applicantId);
        }
        if (!(provider instanceof VeriffIdentityVerificationProvider)) {
            throw new IdentityCheckWebhookException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), "",
                "Wrong type of Identity Verification Provider for " + applicantId + "; did the provider configuration change?");
        }
        return (VeriffIdentityVerificationProvider) provider;
    }
}
