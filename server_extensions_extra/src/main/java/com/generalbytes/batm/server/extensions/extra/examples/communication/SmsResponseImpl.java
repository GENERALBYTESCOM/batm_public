package com.generalbytes.batm.server.extensions.extra.examples.communication;

import com.generalbytes.batm.server.extensions.communication.ISmsErrorResponse;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;

import java.math.BigDecimal;

public class SmsResponseImpl implements ISmsResponse {
    private final String sid;
    private final ResponseStatus status;
    private final BigDecimal price;
    private final ISmsErrorResponse errorResponse;

    public SmsResponseImpl(String sid, ResponseStatus status, BigDecimal price, ISmsErrorResponse errorResponse) {
        this.sid = sid;
        this.status = status;
        this.price = price;
        this.errorResponse = errorResponse;
    }

    @Override
    public String getSid() {
        return sid;
    }

    @Override
    public ResponseStatus getStatus() {
        return status;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public ISmsErrorResponse getErrorResponse() {
        return errorResponse;
    }
}