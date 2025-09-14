package com.generalbytes.batm.server.extensions;

/**
 * Listener that receives printing data intended for a remote printer.
 */
public interface IRemotePrinterListener {

    /**
     * Called when receipt printing is requested.
     */
    void onReceiptPrintingRequested(ReceiptPrintingRequestedEvent event);
}
