package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.lnurl;

import java.util.Objects;

public class LnurlRestServiceException extends Exception {

    private final String clientMessage;

    /**
     * @param clientMessage a message displayed to the end user in their wallet
     */
    public LnurlRestServiceException(String clientMessage) {
        this.clientMessage = Objects.requireNonNull(clientMessage);
    }

    public LnurlRestServiceException() {
        this("Error processing LNURL");
    }

    public String getClientMessage() {
        return clientMessage;
    }

    @Override
    public String getMessage() {
        return "Client message: " + clientMessage;
    }
}
