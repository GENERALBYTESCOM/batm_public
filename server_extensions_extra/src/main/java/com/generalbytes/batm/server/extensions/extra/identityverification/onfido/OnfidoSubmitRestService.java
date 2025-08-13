package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.IdentityCheckWebhookRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OnfidoSubmitRestService implements IRestService {
    private static final Logger log = LoggerFactory.getLogger(OnfidoSubmitRestService.class);

    @Override
    public String getPrefixPath() {
        // absolute path to set the same URL as it was before moving to extensions; don't use this in new providers
        return "/serverapi/apiv1/identity-check/submit/*";
        // Separate classes from webhook and submit methods, otherwise "identity-check/*" would also catch other identity check calls
    }

    @Override
    public Class getImplementation() {
        return getClass();
    }

    /**
     * Called by the verification website when the verification is finished
     *
     * https://hostname:7743/serverapi/apiv1/identity-check/submit/{applicantId}
     * https://hostname:8743/server/serverapi/apiv1/identity-check/submit/{applicantId}
     */
    @GET
    @Path("/{applicantId}")
    public String submit(@PathParam("applicantId") String applicantId) {
        try {
            return getProviderByApplicantId(applicantId).submitCheck(applicantId);
        } catch (RuntimeException | IdentityCheckWebhookException e) {
            log.error("Cannot submit verification", e);
            return null;
        }
    }

    private OnfidoIdentityVerificationProvider getProviderByApplicantId(String applicantId) throws IdentityCheckWebhookException {
        IExtensionContext ctx = OnfidoExtension.getExtensionContext();
        IIdentityVerificationProvider provider = ctx.findIdentityVerificationProviderByApplicantId(applicantId);
        return OnfidoIdentityVerificationProvider.cast(provider, "applicant " + applicantId);
    }
}
