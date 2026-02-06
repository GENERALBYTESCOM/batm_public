package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece.IdScanIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece.SelfieIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.SumSubDocumentType;

import java.util.Set;

/**
 * Maps Sumsub document types to BATM identity pieces (IdScan or Selfie).
 */
public class SumsubIdentityPieceCreator {

    private static final Set<SumSubDocumentType> MAPPABLE_DOC_TYPES = Set.of(
        SumSubDocumentType.ID_CARD,
        SumSubDocumentType.PASSPORT,
        SumSubDocumentType.DRIVERS,
        SumSubDocumentType.SELFIE,
        SumSubDocumentType.VIDEO_SELFIE
    );

    /**
     * Supported media types as per <a href="https://docs.sumsub.com/reference/get-document-images">Sumsub API doc</a>
     */
    private static final Set<String> IMAGE_MEDIA_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "application/pdf"
    );
    private static final Set<String> VIDEO_MEDIA_TYPES = Set.of(
        "video/mp4",
        "video/webm",
        "video/quicktime"
    );

    /**
     * Returns true if the document type can be mapped to an identity piece (IdScan or Selfie).
     */
    public static boolean isMappableDocumentType(SumSubDocumentType docType) {
        return docType != null && MAPPABLE_DOC_TYPES.contains(docType);
    }

    /**
     * Creates an identity piece from the given document type, content type, and content.
     *
     * @param docType     the Sumsub document type
     * @param contentType the MIME type of the content
     * @param content     the raw content bytes
     * @return IdScanIdentityPiece for ID_CARD, PASSPORT, DRIVERS; SelfieIdentityPiece for SELFIE, VIDEO_SELFIE
     * @throws IllegalArgumentException if the document type is not mappable, or content type does not match the document type
     */
    public IIdentityPiece createIdentityPiece(SumSubDocumentType docType, String contentType, byte[] content) {
        validateContentType(docType, contentType);
        return switch (docType) {
            case ID_CARD, PASSPORT, DRIVERS -> new IdScanIdentityPiece(contentType, content);
            case SELFIE, VIDEO_SELFIE -> new SelfieIdentityPiece(contentType, content);
            default -> throw new IllegalArgumentException("Unmappable document type: " + docType);
        };
    }

    private void validateContentType(SumSubDocumentType docType, String contentType) {
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content type cannot be null or blank for document type: " + docType);
        }
        if (docType == SumSubDocumentType.VIDEO_SELFIE) {
            if (!VIDEO_MEDIA_TYPES.contains(contentType)) {
                throw new IllegalArgumentException(
                    "VIDEO_SELFIE requires video content type (video/mp4, video/webm, video/quicktime), got: " + contentType);
            }
        } else {
            if (!IMAGE_MEDIA_TYPES.contains(contentType)) {
                throw new IllegalArgumentException(
                    docType + " requires image content type (image/jpeg, image/png, application/pdf), got: " + contentType);
            }
        }
    }
}
