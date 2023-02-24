package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
