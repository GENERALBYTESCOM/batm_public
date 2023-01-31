package com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece;

import com.generalbytes.batm.server.extensions.IIdentityPiece;

public class SelfieIdentityPiece extends DataIdentityPiece {
    public SelfieIdentityPiece(String mime, byte[] data) {
        super(mime, data);
    }

    @Override
    public int getPieceType() {
        return IIdentityPiece.TYPE_SELFIE;
    }

}
