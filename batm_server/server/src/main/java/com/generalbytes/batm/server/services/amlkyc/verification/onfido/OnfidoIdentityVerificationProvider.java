package com.generalbytes.batm.server.services.amlkyc.verification.onfido;

import com.generalbytes.batm.server.ServerConfigForAll;
import com.generalbytes.batm.server.common.data.OnfidoRegion;
import com.generalbytes.batm.server.common.data.Organization;
import com.generalbytes.batm.server.common.data.amlkyc.ApplicantCheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.Identity;
import com.generalbytes.batm.server.common.data.amlkyc.IdentityApplicant;
import com.generalbytes.batm.server.dao.JPADao;
import com.generalbytes.batm.server.dao.JPAUtil;
import com.generalbytes.batm.server.services.amlkyc.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.services.amlkyc.verification.IdentityVerificationBillingHelper;
import com.generalbytes.batm.server.services.amlkyc.verification.VerificationResultProcessor;
import com.generalbytes.batm.server.services.web.client.VerificationSiteClient;
import com.generalbytes.batm.server.services.web.client.dto.CreateApplicantResponse;
import com.onfido.Onfido;
import com.onfido.exceptions.OnfidoException;
import com.onfido.models.Applicant;
import com.onfido.models.Check;
import com.onfido.models.SdkToken;
import com.onfido.models.Webhook;
import com.onfido.webhooks.WebhookEvent;
import com.onfido.webhooks.WebhookEventVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class OnfidoIdentityVerificationProvider implements IIdentityVerificationProvider {

    private static final Logger log = LoggerFactory.getLogger("batm.master.OnfidoIdentityVerificationProvider");
    private static Webhook HOOK;

    public final Onfido onfido;
    private final JPADao jpaDao;
    private final OnfidoVerificationResultMapper checkResultMapper;
    private final String referrer;
    private final String verificationSiteUrl;
    private final VerificationResultProcessor verificationResultProcessor = new VerificationResultProcessor();
    private IdentityVerificationBillingHelper identityVerificationBillingHelper = new IdentityVerificationBillingHelper();

    public OnfidoIdentityVerificationProvider() {
        this(ServerConfigForAll.getOnfidoApiKey(),
            ServerConfigForAll.getOnfidoVerificationSiteUrl(),
            OnfidoRegion.valueOf(ServerConfigForAll.getOnfidoRegion()));
    }

    public OnfidoIdentityVerificationProvider(String onfidoApiKey, String verificationSiteUrl, OnfidoRegion region) {
        this(
            buildOnfido(onfidoApiKey, region),
            JPADao.getInstance(),
            verificationSiteUrl
        );
    }

    private static Onfido buildOnfido(String apiKey, OnfidoRegion region) {
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

    public OnfidoIdentityVerificationProvider(Onfido onfido, JPADao jpaDao, String verificationSiteUrl) {
        if (verificationSiteUrl == null) {
            throw new IllegalArgumentException("verificationSiteUrl must be configured!");
        }
        this.onfido = onfido;
        this.jpaDao = jpaDao;
        this.checkResultMapper = new OnfidoVerificationResultMapper(onfido);
        this.referrer = getReferrer(verificationSiteUrl);
        this.verificationSiteUrl = verificationSiteUrl;
        ensureWebhook();
    }

    @Override
    public CreateApplicantResponse createApplicant(Identity identity, String gbApiKey, String customerLanguage, String vendorData) {
        Applicant applicant = callInTry(() -> onfido.applicant.create(
            // We don't want to ask users for their name on the terminal so they don't walk away.
            // John Doe will be displayed in the list of verifications on Onfido web
            // but the real name from the uploaded ID will be available in verification detail in onfido dashboard
            Applicant.request().firstName("John").lastName("Doe")
        ));
        if (applicant != null) {
            String token = getSdkToken(applicant.getId());
            Organization org = jpaDao.findOrganizationByApiKey(gbApiKey);
            if (org == null) {
               throw new IllegalArgumentException("No organization found for gbApiKey " + gbApiKey);
            }
            jpaDao.update(new IdentityApplicant(identity, applicant.getId(), org));
            log.info("New IdentityApplicant({}) created", applicant.getId());

            VerificationSiteClient.create(this.verificationSiteUrl, org).notifyAboutApplicant(applicant.getId(), token);

            return new CreateApplicantResponse(applicant.getId(), token, getVerificationUrl(customerLanguage, applicant.getId()));
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
            Check check = callInTry(() -> onfido.check.create(
                Check.request().applicantId(applicantId).reportNames("document", "facial_similarity_photo")
            ));
            if (check != null) {
                identityVerificationBillingHelper.createBillingRecord(identityApplicant.getOrganization(), check.getResult());
            }
        } catch (Exception e) {
            log.error("submitCheck", e);
        } finally {
            JPAUtil.releaseEntityManagerWithCommit();
        }
        return null;
    }

    @Override
    public void processWebhookEvent(String rawPayload, String signature) {
        try {
            WebhookEvent event = callInTry(() -> new WebhookEventVerifier(HOOK.getToken()).readPayload(rawPayload, signature));
            if (event == null) {
                log.error("Failed to parse event: {}", rawPayload);
                return;
            }
            log.debug("Received onfido event {}: id {} status {}", event.getAction(), event.getObject().getId(), event.getObject().getStatus());

            if (event.getAction().equals("check.completed")) {
                Check check = callInTry(() -> onfido.check.find(event.getObject().getId()));
                IdentityApplicant identityApplicant = jpaDao.getApplicantByApplicantId(check.getApplicantId());
                if (identityApplicant == null) {
                    log.error("Identity applicant {} not found. Skipping check result processing.", check.getApplicantId());
                    return;
                }
                ApplicantCheckResult result = checkResultMapper.mapResult(check, identityApplicant);
                verificationResultProcessor.process(result);
                jpaDao.update(result);
            }
        } catch (Exception e) {
            log.error("processWebhookEvent - ERROR", e);
        } finally {
            JPAUtil.releaseEntityManagerWithCommit();
        }
    }

    private void ensureWebhook() {
        callInTry(() -> {
            if (HOOK == null) {
                String webhookMasterUrl = ServerConfigForAll.getMasterServerProxyAddress() + "/serverapi/apiv1/identity-check/onfidowh";
                List<Webhook> webhooks = onfido.webhook.list();
                HOOK = webhooks.stream().filter(webhook -> webhook.getUrl().equals(webhookMasterUrl)).findFirst().orElse(null);
                if (HOOK == null) {
                    log.info("creating new webhook with url={}", webhookMasterUrl);
                    HOOK = onfido.webhook.create(Webhook.request().url(webhookMasterUrl));
                }
            }
            return null;
        });
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

    public static  <OUT> OUT callInTry(OnfidoCallSupplier<OUT> fn) {
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
