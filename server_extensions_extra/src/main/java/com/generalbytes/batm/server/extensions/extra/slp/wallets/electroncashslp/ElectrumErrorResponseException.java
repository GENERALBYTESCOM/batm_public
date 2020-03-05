package com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp;

import java.io.IOException;

public class ElectrumErrorResponseException extends IOException {
    public ElectrumErrorResponseException(String message) {
        super(message);
    }
}
