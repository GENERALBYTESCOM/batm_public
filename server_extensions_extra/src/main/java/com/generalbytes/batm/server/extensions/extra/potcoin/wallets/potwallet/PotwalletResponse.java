package com.generalbytes.batm.server.extensions.extra.potcoin.wallets.potwallet;

import java.math.BigDecimal;

public class PotwalletResponse {
    private Boolean success;
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
