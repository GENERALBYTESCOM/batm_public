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
     * @throws IllegalArgumentException if the document type is not mappable
     */
    public IIdentityPiece createIdentityPiece(SumSubDocumentType docType, String contentType, byte[] content) {
        return switch (docType) {
            case ID_CARD, PASSPORT, DRIVERS -> new IdScanIdentityPiece(contentType, content);
            case SELFIE, VIDEO_SELFIE -> new SelfieIdentityPiece(contentType, content);
            default -> throw new IllegalArgumentException("Unmappable document type: " + docType);
        };
    }
}
