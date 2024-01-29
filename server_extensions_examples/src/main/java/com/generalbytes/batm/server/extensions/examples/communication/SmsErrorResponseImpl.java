package com.generalbytes.batm.server.extensions.examples.communication;

import com.generalbytes.batm.server.extensions.communication.ISmsErrorResponse;

public class SmsErrorResponseImpl implements ISmsErrorResponse {

    private final String errorMessage;
    private final boolean blacklisted;

    public SmsErrorResponseImpl(String errorMessage) {
        this.errorMessage = errorMessage;
        this.blacklisted = false;
    }

    public SmsErrorResponseImpl(String errorMessage, boolean blacklisted) {
        this.errorMessage = errorMessage;
        this.blacklisted = blacklisted;
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
