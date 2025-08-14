package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;


import com.generalbytes.batm.server.extensions.communication.ISmsErrorResponse;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SmsResponse implements ISmsResponse {
    private final String sid;
    private final ResponseStatus status;
    private final BigDecimal price;
    private final ISmsErrorResponse errorResponse;

    public SmsResponse(String sid, ResponseStatus status, BigDecimal price, String errorMessage) {
        this.sid = sid;
        this.status = status;
        this.price = price;
        errorResponse = initializeErrorResponse(errorMessage);
    }

    private ISmsErrorResponse initializeErrorResponse(String errorMessage) {
        if (errorMessage == null) {
            return null;
        }
        return new ISmsErrorResponse() {
            @Override
            public String getErrorMessage() {
                return errorMessage;
            }
            @Override
            public boolean isBlacklisted() {
                return false;
            }
        };
    }
}