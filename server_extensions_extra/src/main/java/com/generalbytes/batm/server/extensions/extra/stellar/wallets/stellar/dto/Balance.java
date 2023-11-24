package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

public class Balance {
	private Amount available;
    private Amount reward;
    private Amount total;
    
    public Amount getAvailable() {
        return available;
    }

    public void setAvailable(Amount available) {
        this.available = available;
    }

    public Amount getReward() {
        return reward;
    }

    public void setReward(Amount reward) {
        this.reward = reward;
    }

    public Amount getTotal() {
        return total;
    }

    public void setTotal(Amount total) {
        this.total = total;
    }
}
