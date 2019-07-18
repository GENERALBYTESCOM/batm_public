package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto;

public class Balance {
    /**
     * Sum of channels balances denominated in satoshis
     */
    public String balance;

    public Long getBalance() {
        return balance == null ? 0l : Long.parseLong(balance);
    }
}
