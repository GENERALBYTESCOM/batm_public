package com.generalbytes.batm.server.extensions.compliance;

/**
 * Thrown when limits are requested without a compliance tier for a terminal that uses a compliance scheme.
 */
public class ComplianceTierRequiredException extends RuntimeException {

    private final String terminalSerialNumber;

    public ComplianceTierRequiredException(String terminalSerialNumber) {
        super("Terminal " + terminalSerialNumber + " uses a compliance scheme, so a compliance tier is required");
        this.terminalSerialNumber = terminalSerialNumber;
    }

    /**
     * @return serial number of the terminal the limits were requested for
     */
    public String getTerminalSerialNumber() {
        return terminalSerialNumber;
    }
}
