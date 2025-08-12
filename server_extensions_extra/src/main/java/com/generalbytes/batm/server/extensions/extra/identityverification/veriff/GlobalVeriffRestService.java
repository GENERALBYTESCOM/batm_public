package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.IdentityCheckWebhookRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GlobalVeriffRestService implements IRestService {
    private static final Logger log = LoggerFactory.getLogger(GlobalVeriffRestService.class);

    @Override
    public String getPrefixPath() {
        // absolute path to set the same URL as it was before moving to extensions; don't use this in new providers
        return "/serverapi/apiv1/identity-check/veriffwh-cloud/*";
    }

    @Override
    public Class getImplementation() {
        return getClass();
    }

    /**
     * https://hostname:7743/serverapi/apiv1/identity-check/veriffwh-cloud
     * https://hostname:8743/server/serverapi/apiv1/identity-check/veriffwh-cloud (when using `batm-manage install-reverse-proxy`)
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String veriffWebhookTestCloud() throws IdentityCheckWebhookException {
        return "BATM server endpoint for Veriff webhooks (cloud)";
    }

    /**
     * Used (on the cloud server) when "GB cloud" is configured by an operator on their own standalone server
     * or on the same cloud server (without their own veriff api keys).
     * API keys taken from /batm/config.
     *
     * https://hostname:7743/serverapi/apiv1/identity-check/veriffwh-cloud
     * https://hostname:8743/server/serverapi/apiv1/identity-check/veriffwh-cloud (when using `batm-manage install-reverse-proxy`)
     */
    @POST
    public Response veriffWebhookCloud(String rawPayload, @HeaderParam("x-auth-client") String apiKey, @HeaderParam("x-hmac-signature") String signature) {
        String errorLabel = String.format("rawPayload: %s, apiKey: %s, signature: %s", rawPayload, apiKey, signature);
        return IdentityCheckWebhookRunnable.getResponse(errorLabel, () -> {
            log.info("Veriff webhook (cloud) received, getting verification provider for global server.");
            VeriffIdentityVerificationProvider.getForGlobalServer().processWebhookEvent(rawPayload, signature);
        });
    }
}
