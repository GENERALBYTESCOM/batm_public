package com.generalbytes.batm.server.extensions.event;

public class ApplicationEvent {
    private boolean extensionsInitialized;
    private boolean paymentMgrInitialized;

    public boolean isExtensionsInitialized() {
        return extensionsInitialized;
    }

    public void setExtensionsInitialized(boolean extensionsInitialized) {
        this.extensionsInitialized = extensionsInitialized;
    }

    public boolean isPaymentMgrInitialized() {
        return paymentMgrInitialized;
    }

    public void setPaymentMgrInitialized(boolean paymentMgrInitialized) {
        this.paymentMgrInitialized = paymentMgrInitialized;
    }
}
