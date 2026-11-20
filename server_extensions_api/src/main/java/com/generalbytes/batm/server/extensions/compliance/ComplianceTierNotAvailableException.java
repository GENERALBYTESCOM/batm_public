package com.generalbytes.batm.server.extensions.compliance;

/**
 * Thrown when limits are requested for a compliance tier that the terminal's compliance scheme does not configure.
 */
public class ComplianceTierNotAvailableException extends RuntimeException {

    private final long complianceTierId;
    private final String terminalSerialNumber;

    public ComplianceTierNotAvailableException(long complianceTierId, String terminalSerialNumber) {
        super("Compliance tier " + complianceTierId + " is not available on the compliance scheme of terminal "
            + terminalSerialNumber);
        this.complianceTierId = complianceTierId;
        this.terminalSerialNumber = terminalSerialNumber;
    }

    /**
     * @return the requested tier that is not available on the terminal's compliance scheme
     */
    public long getComplianceTierId() {
        return complianceTierId;
    }

    /**
     * @return serial number of the terminal the limits were requested for
     */
    public String getTerminalSerialNumber() {
        return terminalSerialNumber;
    }
}
