package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class UserBalanceResponse {
    public String symbol;
    public String message;
    public BigDecimal availableBalance;
    public BigDecimal pendingTransfer;
    public BigDecimal totalBalance;
}
