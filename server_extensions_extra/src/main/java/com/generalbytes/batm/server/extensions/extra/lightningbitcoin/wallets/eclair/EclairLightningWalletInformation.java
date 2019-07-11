package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair;

import com.generalbytes.batm.server.extensions.extra.lightning.ILightningWalletInformation;

class EclairLightningWalletInformation implements ILightningWalletInformation {

    private String pubKey;

    EclairLightningWalletInformation(String pubKey) {
        this.pubKey = pubKey;
    }

    @Override
    public String getPubKey() {
        return pubKey;
    }
}
