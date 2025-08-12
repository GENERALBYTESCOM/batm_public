package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.IdentityCheckWebhookRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OnfidoWebhookRestService implements IRestService {
    private static final Logger log = LoggerFactory.getLogger(OnfidoWebhookRestService.class);

    @Override
    public String getPrefixPath() {
        // absolute path to set the same URL as it was before moving to extensions; don't use this in new providers
        return "/serverapi/apiv1/identity-check/onfidowh/*";
        // Separate classes from webhook and submit methods, otherwise "identity-check/*" would also catch other identity check calls
    }

    @Override
    public Class getImplementation() {
        return getClass();
    }

    /**
     * Used on both standalone and global server when operator uses their custom API keys (taken from Organization).
     *
     * @return 200 HTTP response in case of a successfully processed webhook.
     * Any non-200 response will make Onfido retry
     */
    // https://hostname:7743/serverapi/apiv1/identity-check/onfidowh/{webhookKey}
    // https://hostname:8743/server/serverapi/apiv1/identity-check/onfidowh/{webhookKey}
    @POST
    @Path("/{webhookKey}")
    public Response onfidoWebhook(String rawPayload, @HeaderParam("X-SHA2-Signature") String signature, @PathParam("webhookKey") String webhookKey) {
        String errorLabel = String.format("rawPayload: %s, webhookKey: %s, signature: %s", rawPayload, webhookKey, signature);
        return IdentityCheckWebhookRunnable.getResponse(errorLabel, () -> {
            log.info("Onfido webhook event received; webhookKey: {} rawPayload: {}", webhookKey, rawPayload);
            getProviderByWebhookKey(webhookKey).processWebhookEvent(rawPayload, signature, webhookKey);
        });
    }

    private OnfidoIdentityVerificationProvider getProviderByWebhookKey(String webhookKey) throws IdentityCheckWebhookException {
        long organizationId = Long.parseLong(webhookKey);
        IExtensionContext ctx = OnfidoExtension.getExtensionContext();
        IIdentityVerificationProvider provider = ctx.findIdentityVerificationProviderByOrganizationId(organizationId);
        return OnfidoIdentityVerificationProvider.cast(provider, "webhookKey " + webhookKey);
    }

}
