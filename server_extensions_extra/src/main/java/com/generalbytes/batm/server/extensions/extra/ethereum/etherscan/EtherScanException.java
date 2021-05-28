package com.generalbytes.batm.server.extensions.extra.ethereum.etherscan;

import si.mazi.rescu.HttpStatusExceptionSupport;

public class EtherScanException extends HttpStatusExceptionSupport {

    public String status;
    public String message;
    public String result;

    @Override
    public String getMessage() {
        return status + ": " + message + ", " + result;
    }
}
