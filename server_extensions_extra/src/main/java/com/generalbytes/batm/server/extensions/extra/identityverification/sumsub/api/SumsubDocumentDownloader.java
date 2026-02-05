package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionImage;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.SumSubDocumentType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Orchestrates downloading document images from Sumsub and storing them as identity pieces.
 * Uses {@link SumsubDocumentClient} for HTTP download and {@link SumsubIdentityPieceCreator} for piece creation.
 *
 * <p><a href="https://docs.sumsub.com/reference/get-document-images">Get document images</a>
 */
@Slf4j
public class SumsubDocumentDownloader {

    private final SumsubDocumentClient client;
    private final SumsubIdentityPieceCreator creator;
    private final int maxDownloadRetries;
    private final int retryDelaySeconds; // with increasing backoff (attemptNumber * retryDelaySeconds)

    public SumsubDocumentDownloader(SumsubDocumentClient client, SumsubIdentityPieceCreator creator,
                                    int maxDownloadRetries, int retryDelaySeconds) {
        this.client = client;
        this.creator = creator;
        this.maxDownloadRetries = maxDownloadRetries;
        this.retryDelaySeconds = retryDelaySeconds;
    }

    /**
     * Filters images to mappable types, downloads each, and stores as identity pieces.
     * Unmappable document types are skipped.
     */
    public void downloadAndStoreDocuments(String identityPublicId, String inspectionId,
                                          List<InspectionImage> images, IExtensionContext ctx) {
        if (images == null || images.isEmpty()) {
            return;
        }

        List<InspectionImage> mappableImages = images.stream()
            .filter(this::isMappableDocumentImage)
            .toList();

        if (mappableImages.isEmpty()) {
            return;
        }

        List<InspectionImage> failedImages = retryDownload(mappableImages, identityPublicId, inspectionId, ctx, 1);

        if (!failedImages.isEmpty()) {
            log.error("Failed to download the following images after {} attempts: {}", maxDownloadRetries, formatFailedImageDetails(failedImages));
        }
    }

    private List<InspectionImage> retryDownload(List<InspectionImage> images, String identityPublicId, String inspectionId,
                                                IExtensionContext extensionContext, int attempt) {
        if (attempt > maxDownloadRetries) {
            return images;
        }

        List<InspectionImage> failedThisRound = attemptDownload(images, identityPublicId, inspectionId, extensionContext, attempt);

        if (failedThisRound.isEmpty()) {
            log.info("All images downloaded successfully for applicantId: {}", identityPublicId);
            return Collections.emptyList();
        }

        waitBeforeRetry(attempt);
        return retryDownload(failedThisRound, identityPublicId, inspectionId, extensionContext, attempt + 1);
    }

    private List<InspectionImage> attemptDownload(List<InspectionImage> images,
                                                  String identityPublicId,
                                                  String inspectionId,
                                                  IExtensionContext ctx,
                                                  int attempt) {
        List<InspectionImage> failedImages = new ArrayList<>();
        for (InspectionImage image : images) {
            try {
                SumSubDocumentType docType = image.getIdDocDef().getIdDocType();
                log.info("Attempt {}: Downloading image ({}) for applicantId: {}", attempt, docType, identityPublicId);
                DownloadedDocument download = client.downloadDocument(inspectionId, String.valueOf(image.getImageId()));
                IIdentityPiece piece = creator.createIdentityPiece(docType, download.contentType(), download.content());
                ctx.addIdentityPiece(identityPublicId, piece);
                int fileSizeKiloBytes = download.content().length / 1000;
                log.info("Sumsub document ({}, {}) downloaded: {} kB", docType, image.getImageId(), fileSizeKiloBytes);
            } catch (IllegalArgumentException e) {
                log.warn("Failed to download and create identity piece for image ID: {}, type: {}", image.getImageId(),
                    image.getIdDocDef().getIdDocType(), e);
            } catch (IOException e) {
                log.warn("Attempt {} failed for image ID: {}, type: {}, error: {}", attempt, image.getImageId(),
                    image.getIdDocDef().getIdDocType(), e.getMessage());
                failedImages.add(image);
            }
        }
        return failedImages;
    }

    private boolean isMappableDocumentImage(InspectionImage img) {
        if (img.getImageId() == null) {
            log.warn("Skipping image with null imageId");
            return false;
        }
        if (img.getIdDocDef() == null) {
            log.warn("Skipping image with null idDocType");
            return false;
        }
        SumSubDocumentType docType = img.getIdDocDef().getIdDocType();
        if (!SumsubIdentityPieceCreator.isMappableDocumentType(docType)) {
            log.debug("Skipping unmappable document type from Sumsub: {}", docType);
            return false;
        }
        return true;
    }

    private List<String> formatFailedImageDetails(List<InspectionImage> failedImages) {
        return failedImages.stream()
            .map(img -> img.getImageId() + " (" + (img.getIdDocDef() != null ? img.getIdDocDef().getIdDocType() : "unknown") + ")")
            .toList();
    }

    private void waitBeforeRetry(int attempt) {
        try {
            TimeUnit.SECONDS.sleep((long) attempt * retryDelaySeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Retry interrupted", e);
        }
    }
}
