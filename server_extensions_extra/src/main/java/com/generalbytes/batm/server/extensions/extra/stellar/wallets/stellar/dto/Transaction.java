package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

public class Transaction {
	 private String id;
	    private Amount amount;
	    private Amount fee;
	    private String status;

	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    public Amount getAmount() {
	        return amount;
	    }

	    public void setAmount(Amount amount) {
	        this.amount = amount;
	    }

	    public Amount getFee() {
	        return fee;
	    }

	    public void setFee(Amount fee) {
	        this.fee = fee;
	    }

	    public String getStatus() {
	        return status;
	    }

	    public void setStatus(String status) {
	        this.status = status;
	    }

	    @Override
	    public String toString() {
	        return "Transaction{" + "id='" + id + '\'' +
	            ", amount=" + amount +
	            ", fee=" + fee +
	            ", status='" + status + '\'' +
	            '}';
	    }
}
