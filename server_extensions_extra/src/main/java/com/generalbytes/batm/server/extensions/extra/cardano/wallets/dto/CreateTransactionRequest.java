package com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto;

import java.util.List;

public class CreateTransactionRequest {
    private String passphrase;
    private List<Payment> payments;

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}
