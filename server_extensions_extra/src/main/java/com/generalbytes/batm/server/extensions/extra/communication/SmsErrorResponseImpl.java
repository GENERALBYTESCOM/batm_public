package com.generalbytes.batm.server.extensions.extra.communication;

import com.generalbytes.batm.server.extensions.communication.ISmsErrorResponse;

public class SmsErrorResponseImpl implements ISmsErrorResponse {

    private final String errorMessage;
    private final boolean blacklisted;

    public SmsErrorResponseImpl(String errorMessage) {
        this.errorMessage = errorMessage;
        this.blacklisted = false;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isBlacklisted() {
        return blacklisted;
    }
}
