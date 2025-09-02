package com.generalbytes.batm.server.extensions;

import lombok.Data;

import java.util.Map;

/**
 * Event object carrying data for a remote printer receipt printing request.
 */
@Data
public class ReceiptPrintingRequestedEvent {
    /**
     * serial number of the terminal
     */
    private String terminalSerialNumber;

    /**
     * identifier of the remote printer configured in CAS
     */
    private String remotePrinterId;

    /**
     * language selected by the customer on the terminal (e.g. "en")
     */
    private String customerLanguage;

    /**
     * custom string name of the print receipt template
     */
    private String templateName;

    /**
     * print receipt template unresolved content (with placeholders) for the current transaction
     */
    private String templateContent;

    /**
     * map of key-value pairs with all data used in the template placeholders
     */
    private Map<String, String> placeholders;
}
