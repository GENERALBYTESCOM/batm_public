package com.generalbytes.batm.server.extensions;

import java.util.Map;

/**
 * Listener that receives printing data intended for a remote printer.
 */
public interface IRemotePrinterListener {

    /**
     * Called when receipt printing is requested.
     *
     * @param terminalSerialNumber serial number of the terminal
     * @param remotePrinterId      identifier of the remote printer configured in CAS
     * @param customerLanguage     language selected by the customer on the terminal (e.g. "en")
     * @param templateName         custom string name of the print receipt template
     * @param templateContent      print receipt template unresolved content (with placeholders) for the current transaction
     * @param placeholders         map of key-value pairs with all data used in the template placeholders
     */
    void onReceiptPrintingRequested(String terminalSerialNumber,
                                    String remotePrinterId,
                                    String customerLanguage,
                                    String templateName,
                                    String templateContent,
                                    Map<String, String> placeholders);
}
