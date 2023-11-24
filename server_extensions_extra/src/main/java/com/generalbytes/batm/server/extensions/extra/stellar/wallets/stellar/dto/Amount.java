package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

import java.math.BigDecimal;

public class Amount {
	 private BigDecimal quantity;
	    private String unit;
	    
	    public BigDecimal getQuantity() {
	        return quantity;
	    }

	    public void setQuantity(BigDecimal quantity) {
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
