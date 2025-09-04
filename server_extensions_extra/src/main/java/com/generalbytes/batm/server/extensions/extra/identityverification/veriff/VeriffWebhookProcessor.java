package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece.IdScanIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece.SelfieIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.IVeriffApi;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.SessionMediaInfo;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.VeriffDigest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.VeriffMediaDownloader;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.webhook.VerificationEventWebhookRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class VeriffWebhookProcessor {
    private static final Logger log = LoggerFactory.getLogger("batm.master.VeriffWebhookProcessor");
    private static final VeriffWebhookParser veriffWebhookParser = new VeriffWebhookParser();
    private static final ExecutorService documentDownloadExecutorService = Executors.newSingleThreadExecutor();

    private final VeriffDigest veriffDigest;
    private final IVeriffApi api;
    private final VeriffMediaDownloader mediaDownloader;
    private final VeriffVerificationResultMapper checkResultMapper = new VeriffVerificationResultMapper();

    public VeriffWebhookProcessor(String publicKey, VeriffDigest veriffDigest, IVeriffApi api) {
        this.veriffDigest = veriffDigest;
        this.api = api;
        this.mediaDownloader = new VeriffMediaDownloader(publicKey, veriffDigest);
    }

    public void process(String rawPayload, String signature) throws IdentityCheckWebhookException {
        verifySignature(rawPayload, signature);
        veriffWebhookParser.accept(rawPayload, this::processDecisionWebhook, this::processEventWebhook);
    }

    private void processEventWebhook(String rawPayload, VerificationEventWebhookRequest request) {
        log.info("Veriff event webhook received: {}", rawPayload);
    }

    private void processDecisionWebhook(String rawPayload, VerificationDecisionWebhookRequest request) {
        log.info("Veriff decision webhook received: {}", rawPayload);
        ApplicantCheckResult result = checkResultMapper.mapResult(request);
        VeriffExtension.getExtensionContext().processIdentityVerificationResult(rawPayload, result);
        downloadMedia(result);
    }

    private void downloadMedia(ApplicantCheckResult result) {
        String applicantId = result.getIdentityApplicantId();
        IIdentity identity = VeriffExtension.getExtensionContext()
            .findIdentityVerificationApplicant(applicantId)
            .getIdentity();

        if (identity == null) {
            log.debug("Skipping media download, missing identity");
            return;
        }

        // we have just a few seconds to reply to the webhook, better download the documents in a background thread
        documentDownloadExecutorService.submit(() -> {
            try {
                downloadMedia(identity.getPublicId(), applicantId);
            } catch (HttpStatusIOException e) {
                log.error("Error downloading documents from Veriff, HTTP response code: {}, body: {}", e.getHttpStatusCode(), e.getHttpBody());
            } catch (Exception e) {
                log.error("Error downloading documents for applicant ID: {}, {}", applicantId, identity.getPublicId(), e);
            }
        });
    }

    private void downloadMedia(String identityPublicId, String applicantId) throws IOException {
        List<SessionMediaInfo.Image> images = api.getSessionMediaInfo(applicantId).images;
        if (images == null) {
            return;
        }

        final int maxRetries = 3;
        List<SessionMediaInfo.Image> failedImages = retryDownload(images, identityPublicId, applicantId, maxRetries, 1);

        if (!failedImages.isEmpty()) {
            List<String> failedImageDetails = failedImages.stream()
                    .map(image -> image.id + " (" + image.context + ")")
                    .collect(Collectors.toList());
            log.error("Failed to download the following images after {} attempts: {}", maxRetries, failedImageDetails);
        }
    }

    private List<SessionMediaInfo.Image> retryDownload(List<SessionMediaInfo.Image> images,
                                                       String identityPublicId,
                                                       String applicantId,
                                                       int maxRetries,
                                                       int attempt) {
        if (attempt > maxRetries) {
            return images;
        }

        List<SessionMediaInfo.Image> failedThisRound = attemptDownload(images, identityPublicId, applicantId, attempt);

        if (failedThisRound.isEmpty()) {
            log.info("All images downloaded successfully for applicantId: {}", applicantId);
            return Collections.emptyList();
        }

        addRetryDelay(attempt);
        return retryDownload(failedThisRound, identityPublicId, applicantId, maxRetries, attempt + 1);
    }

    private List<SessionMediaInfo.Image> attemptDownload(List<SessionMediaInfo.Image> images,
                                                         String identityPublicId,
                                                         String applicantId,
                                                         int attempt) {
        List<SessionMediaInfo.Image> failedImages = new ArrayList<>();
        for (SessionMediaInfo.Image image : images) {
            try {
                String type = image.context;
                log.info("Attempt {}: Downloading image ({}) for applicantId: {}", attempt, type, applicantId);
                byte[] content = download(image);
                VeriffExtension.getExtensionContext().addIdentityPiece(identityPublicId, getIdentityPiece(type, content));
            } catch (IOException e) {
                log.warn("Attempt {} failed for image ID: {}, type: {}, error: {}", attempt, image.id, image.context, e.getMessage());
                failedImages.add(image);
            }
        }
        return failedImages;
    }

    private void addRetryDelay(int attempt) {
        try {
            TimeUnit.SECONDS.sleep(attempt);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Retry interrupted", e);
        }
    }


    private IIdentityPiece getIdentityPiece(String type, byte[] content) {
        if ("face".equals(type)) {
            return new SelfieIdentityPiece("image/jpeg", content);
        }
        return new IdScanIdentityPiece("image/jpeg", content);
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
