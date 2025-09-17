package com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece;

import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.IdScanDocumentType;

public class IdScanIdentityPiece extends DataIdentityPiece {
    private IdScanDocumentType idScanDocumentType;

    public IdScanIdentityPiece(String mime, byte[] data) {
        super(mime, data);
    }

    @Override
    public int getPieceType() {
        return IIdentityPiece.TYPE_ID_SCAN;
    }

    @Override
    public IdScanDocumentType getIdScanDocumentType() {
        return idScanDocumentType;
    }

    public void setIdScanDocumentType(IdScanDocumentType idScanDocumentType) {
        this.idScanDocumentType = idScanDocumentType;
    }
}
