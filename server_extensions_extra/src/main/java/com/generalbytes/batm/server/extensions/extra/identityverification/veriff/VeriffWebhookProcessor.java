package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.common.data.Organization;
import com.generalbytes.batm.server.common.data.amlkyc.ApplicantCheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.Identity;
import com.generalbytes.batm.server.common.data.amlkyc.IdentityApplicant;
import com.generalbytes.batm.server.core.tp.IdentityHelper;
import com.generalbytes.batm.server.dao.JPADao;
import com.generalbytes.batm.server.dao.JPAUtil;
import com.generalbytes.batm.server.services.amlkyc.verification.VerificationResultProcessor;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.IVeriffApi;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.SessionMediaInfo;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.VeriffDigest;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.VeriffMediaDownloader;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationEventWebhookRequest;
import com.generalbytes.batm.server.services.web.IdentityCheckWebhookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VeriffWebhookProcessor {
    private static final Logger log = LoggerFactory.getLogger("batm.master.VeriffWebhookProcessor");
    private static final VeriffWebhookParser veriffWebhookParser = new VeriffWebhookParser();
    private static final ExecutorService documentDownloadExecutorService = Executors.newSingleThreadExecutor();

    private final VeriffDigest veriffDigest;
    private final IVeriffApi api;
    private final VeriffMediaDownloader mediaDownloader;
    private final VeriffVerificationResultMapper checkResultMapper = new VeriffVerificationResultMapper();
    private final VerificationResultProcessor verificationResultProcessor = new VerificationResultProcessor();

    public VeriffWebhookProcessor(String publicKey, VeriffDigest veriffDigest, IVeriffApi api) {
        this.veriffDigest = veriffDigest;
        this.api = api;
        this.mediaDownloader = new VeriffMediaDownloader(publicKey, veriffDigest);
    }

    /**
     * @return organization for the applicant that started the verification session.
     * Request signature must be verified after getting the organization.
     */
    public static Organization getOrganization(String rawPayload, JPADao jpaDao) /* TODO throws IdentityCheckWebhookException*/ {
        String applicantId = veriffWebhookParser.getApplicantId(rawPayload);
        IdentityApplicant identityApplicant = jpaDao.getApplicantByApplicantId(applicantId);
        if (identityApplicant == null) {
            throw new RuntimeException(); // TODO IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR, "identity applicant not found by verification session ID", rawPayload);
        }
        Organization organization = identityApplicant.getOrganization();
        log.info("Organization found: {} for applicant ID: {}", organization, applicantId);
        return organization;
    }

    public void process(String rawPayload, String signature) throws IdentityCheckWebhookException {
        verifySignature(rawPayload, signature);
        veriffWebhookParser.accept(rawPayload, this::processDecisionWebhook, this::processEventWebhook);
    }

    private void processEventWebhook(String rawPayload, VerificationEventWebhookRequest request) {
        log.info("Veriff event webhook received: {}", rawPayload);
    }

    private void processDecisionWebhook(String rawPayload, VerificationDecisionWebhookRequest request) throws IdentityCheckWebhookException {
        log.info("Veriff decision webhook received: {}", rawPayload);
        ApplicantCheckResult result = processDecisionWebhook(request);
        downloadMedia(result);
    }

    private ApplicantCheckResult processDecisionWebhook(VerificationDecisionWebhookRequest request) throws IdentityCheckWebhookException {
        try {
            JPADao jpaDao = JPADao.getInstance();
            String applicantId = request.getApplicantId();
            IdentityApplicant identityApplicant = jpaDao.getApplicantByApplicantId(applicantId);
            if (identityApplicant == null) {
                throw new RuntimeException(); // TODO IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR, "applicant not found", applicantId);
            }
            // TODO save result but also be able to modify identity directly / custom fields etc...
            ApplicantCheckResult result = checkResultMapper.mapResult(request, identityApplicant);
            verificationResultProcessor.process(result);
            jpaDao.update(result);
            return result;
        } finally {
            JPAUtil.releaseEntityManagerWithCommit();
        }
    }

    private void downloadMedia(ApplicantCheckResult result) {
        Identity identity = result.getIdentityApplicant().getIdentity();
        String applicantId = result.getIdentityApplicant().getApplicantId();
        // we have just a few seconds to reply to the webhook, better download the documents in a background thread
        documentDownloadExecutorService.submit(() -> {
            try {
                downloadMedia(identity, applicantId);
            } catch (HttpStatusIOException e) {
                log.error("Error downloading documents from Veriff, HTTP response code: {}, body: {}", e.getHttpStatusCode(), e.getHttpBody());
            } catch (Exception e) {
                log.error("Error downloading documents for applicant ID: {}, {}", applicantId, identity, e);
            }
        });
    }

    private void downloadMedia(Identity identity, String applicantId) throws IOException {
        List<SessionMediaInfo.Image> images = api.getSessionMediaInfo(applicantId).images;
        if (images == null) {
            return;
        }
        for (SessionMediaInfo.Image image : images) {
            String type = image.context;
            log.info("Downloading image ({}) for {}, applicantId: {}", type, identity, applicantId);
            byte[] content = download(image);
            if ("face".equals(type)) {
                IdentityHelper.addSelfieToIdentityInCurrentTransaction(identity, "image/jpeg", content);
            } else {
                IdentityHelper.addIdScanToIdentityInCurrentTransaction(identity, "image/jpeg", content);
            }
        }
    }

    private byte[] download(SessionMediaInfo.Image image) throws IOException {
        long downloadStartNanos = System.nanoTime();
        byte[] content = mediaDownloader.downloadMedia(image.id);
        int fileSizeKiloBytes = content.length / 1000;
        long downloadMillis = Duration.ofNanos(System.nanoTime() - downloadStartNanos).toMillis();
        log.info("Veriff media ({}, {}) downloaded: {} kB in {} ms", image.context, image.id, fileSizeKiloBytes, downloadMillis);
        return content;
    }

    private void verifySignature(String rawPayload, String signature) throws IdentityCheckWebhookException {
        String computedSignature = veriffDigest.digest(rawPayload);
        if (!computedSignature.equals(signature)) {
            throw new IdentityCheckWebhookException(Status.UNAUTHORIZED.getStatusCode(), "signature verification failure",
                "Wrong api secret used? computed: '" + computedSignature + "', received: '" + signature + "', payload: '" + rawPayload + "'. " +
                    "Are Veriff API keys configured properly? Is this an old webhook with different API keys that got re-sent?");
        }
    }

}
