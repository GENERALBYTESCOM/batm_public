package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.walletofsatoshi.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    public BigDecimal amount;
    public String description;
    public String address;
    public String currency;
}
