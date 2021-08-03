package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto;

public class BlockIORequestSubmitTransaction {

    private final BlockIOTransaction transaction_data;

    public BlockIORequestSubmitTransaction(BlockIOTransaction transaction_data) {
        this.transaction_data = transaction_data;
    }

    public BlockIOTransaction getTransaction_data() {
        return transaction_data;
    }
}
