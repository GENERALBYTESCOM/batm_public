package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

import com.generalbytes.batm.server.common.data.OnfidoRegion;
import com.generalbytes.batm.server.common.data.Organization;
import com.generalbytes.batm.server.common.data.amlkyc.IdentityApplicant;
import com.generalbytes.batm.server.dao.JPADao;
import com.generalbytes.batm.server.dao.JPAUtil;
import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.aml.verification.CreateApplicantResponse;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite.VerificationSiteClient;
import com.onfido.Onfido;
import com.onfido.exceptions.OnfidoException;
import com.onfido.models.Applicant;
import com.onfido.models.Check;
import com.onfido.models.SdkToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Supplier;

public class OnfidoIdentityVerificationProvider implements IIdentityVerificationProvider {

    private static final Logger log = LoggerFactory.getLogger("batm.master.OnfidoIdentityVerificationProvider");

    public final Onfido onfido;
    private final JPADao jpaDao;
    private final String referrer;
    private final String verificationSiteUrl;
    private final OnfidoWebhookProcessor webhookProcessor;

    public OnfidoIdentityVerificationProvider(String onfidoApiKey, String verificationSiteUrl, OnfidoRegion region, String webhookKey) {
        this(
            buildOnfido(onfidoApiKey, region),
            JPADao.getInstance(),
            verificationSiteUrl,
            webhookKey);
    }

    private static Onfido buildOnfido(String apiKey, OnfidoRegion region) {
        Objects.requireNonNull(apiKey, "onfido api key cannot be null");
        Onfido.Builder builder = Onfido.builder().apiToken(apiKey);
        Objects.requireNonNull(region, "region cannot be null");
        switch (region) {
            case US:
                return builder.regionUS().build();
            case CA:
                return builder.regionCA().build();
            case EU:
            default:
                return builder.regionEU().build();
        }
    }

    public OnfidoIdentityVerificationProvider(Onfido onfido, JPADao jpaDao, String verificationSiteUrl, String webhookKey) {
        if (verificationSiteUrl == null) {
            throw new IllegalArgumentException("verificationSiteUrl must be configured!");
        }
        this.onfido = onfido;
        this.jpaDao = jpaDao;
        this.referrer = getReferrer(verificationSiteUrl);
        this.verificationSiteUrl = verificationSiteUrl;
        this.webhookProcessor = new OnfidoWebhookProcessor(onfido, jpaDao, webhookKey);
    }

    @Override
    public CreateApplicantResponse createApplicant(IIdentity identity, String gbApiKey, String customerLanguage, String vendorData) {
        Applicant applicant = callInTry(() -> onfido.applicant.create(
            // We don't want to ask users for their name on the terminal so they don't walk away.
            // John Doe will be displayed in the list of verifications on Onfido web
            // but the real name from the uploaded ID will be available in verification detail in onfido dashboard
            Applicant.request().firstName("John").lastName("Doe")
        ));
        if (applicant != null) {
            String token = getSdkToken(applicant.getId());
//            Organization org = jpaDao.findOrganizationByApiKey(gbApiKey);
//            if (org == null) {
//               throw new IllegalArgumentException("No organization found for gbApiKey " + gbApiKey);
//            }
            String verificationWebUrl = getVerificationUrl(customerLanguage, applicant.getId());

            VerificationSiteClient.create(this.verificationSiteUrl).notifyAboutApplicant(applicant.getId(), token);

            return new CreateApplicantResponse(applicant.getId(), token, verificationWebUrl);
        }
        return null;
    }

    private String getVerificationUrl(String customerLanguage, String applicantId) {
        String verificationUrl = this.verificationSiteUrl + "?a=" + applicantId;
        if (customerLanguage != null) {
            verificationUrl += "&lang=" + customerLanguage;
        }
        return verificationUrl;
    }

    @Override
    public String submitCheck(String applicantId) {
        try {
            log.info("Submitting check for applicant {}", applicantId);
            IdentityApplicant identityApplicant = jpaDao.getApplicantByApplicantId(applicantId);
            if (identityApplicant == null) {
                log.error("Non existent applicantId: {}", applicantId);
                return null;
            }
            callInTry(() -> onfido.check.create(
                Check.request().applicantId(applicantId).reportNames("document", "facial_similarity_photo")
            ));
        } catch (Exception e) {
            log.error("submitCheck", e);
        } finally {
            JPAUtil.releaseEntityManagerWithCommit();
        }
        return null;
    }

    @Override
    public void processWebhookEvent(String rawPayload, String signature, String webhookKey) throws IdentityCheckWebhookException {
        webhookProcessor.process(rawPayload, signature, webhookKey);
    }

    private String getSdkToken(String applicantId) {
        return callInTry(() -> onfido.sdkToken.generate(
            SdkToken.request().applicantId(applicantId).referrer(referrer)
        ));
    }

    private String getReferrer(String verificationSiteUrl) {
        if (!verificationSiteUrl.startsWith("http")) {
            verificationSiteUrl = "https://" + verificationSiteUrl;
        }
        try {
            URL url = new URL(verificationSiteUrl);
            return url.getProtocol() + "://" + url.getHost() + "/*";
        } catch (MalformedURLException e) {
            log.error("Error parsing url " + verificationSiteUrl);
            return verificationSiteUrl + "*";
        }
    }

    public static OnfidoIdentityVerificationProvider cast(IIdentityVerificationProvider provider, String label) throws IdentityCheckWebhookException {
        if (provider == null) {
            throw new IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "",
                "Cannot get Identity Verification Provider for " + label);
        }

        if (!(provider instanceof OnfidoIdentityVerificationProvider)) {
            throw new IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "",
                "Wrong type of Identity Verification Provider for " + label + "; did the provider configuration change?");
        }
        return (OnfidoIdentityVerificationProvider) provider;
    }

    public static <OUT> OUT callInTry(OnfidoCallSupplier<OUT> fn) {
        return fn.get();
    }
}

@FunctionalInterface
interface OnfidoCallSupplier<T> extends Supplier<T> {
    Logger log = LoggerFactory.getLogger(OnfidoIdentityVerificationProvider.class);

    @Override
    default T get() {
        try {
            return getThrows();
        } catch (final Exception e) {
            log.error("onfido API error:", e);
        }
        return null;
    }

    T getThrows() throws OnfidoException;
}
