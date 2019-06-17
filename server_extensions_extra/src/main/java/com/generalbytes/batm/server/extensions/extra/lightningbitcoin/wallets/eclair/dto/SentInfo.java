package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

public class SentInfo {
    public String id;
    public String paymentHash;
    public Status status;

    public enum Status {
        PENDING, FAILED, SUCCEEDED;
    }
}
