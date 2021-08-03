package com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto;

public class Amount {
    private Long quantity;
    private String unit;

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Amount{" + "quantity=" + quantity +
            ", unit='" + unit + '\'' +
            '}';
    }
}
