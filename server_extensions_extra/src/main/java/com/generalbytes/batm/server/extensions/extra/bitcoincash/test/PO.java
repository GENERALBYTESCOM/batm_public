package com.generalbytes.batm.server.extensions.extra.bitcoincash.test;

import com.generalbytes.batm.server.extensions.payment.IPaymentOutput;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PO implements IPaymentOutput{

    private String address;
    private BigDecimal amount;

    PO(String address, BigDecimal amount) {
        this.address = address;
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void removeAmount(BigDecimal amountToRemove) {
        amount = amount.subtract(amountToRemove).setScale(8, RoundingMode.HALF_DOWN);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PO{" +
            "address='" + address + '\'' +
            ", amount=" + amount +
            '}';
    }
}