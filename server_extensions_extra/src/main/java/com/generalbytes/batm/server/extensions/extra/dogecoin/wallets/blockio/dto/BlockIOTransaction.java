package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto;

import java.util.List;

public class BlockIOTransaction {
    private String tx_type;
    private String tx_hex;
    private List<BlockIOTransactionSignature> signatures;

    public String getTx_type() {
        return tx_type;
    }

    public void setTx_type(String tx_type) {
        this.tx_type = tx_type;
    }

    public String getTx_hex() {
        return tx_hex;
    }

    public void setTx_hex(String tx_hex) {
        this.tx_hex = tx_hex;
    }

    public List<BlockIOTransactionSignature> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<BlockIOTransactionSignature> signatures) {
        this.signatures = signatures;
    }
}
