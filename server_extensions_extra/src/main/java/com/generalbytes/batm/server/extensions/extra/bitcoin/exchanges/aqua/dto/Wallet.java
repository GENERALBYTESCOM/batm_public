package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aqua.dto;

import java.math.BigDecimal;

public class Wallet {
    public String symbol;
    public BigDecimal balance;
    public BigDecimal reservedBalance;
    public BigDecimal availableBalance;

    @Override
    public String toString() {
        return "Wallet{" +
            "symbol='" + symbol + '\'' +
            ", balance='" + balance + '\'' +
            ", reservedBalance='" + reservedBalance + '\'' +
            ", availableBalance='" + availableBalance + '\'' +
            '}';
    }
}