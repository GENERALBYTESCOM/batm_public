package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto;

public class PaymentRequest {
    public String destination;
    public String payment_hash;
    public String num_satoshis;
    public String timestamp;
    public String expiry;
    public String description;
    public String description_hash;
    public String fallback_addr;
    public String cltv_expiry;
}
