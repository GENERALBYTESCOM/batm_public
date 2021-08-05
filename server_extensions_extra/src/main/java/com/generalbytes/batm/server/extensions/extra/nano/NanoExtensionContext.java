package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.util.NanoUtil;

/**
 * Contains various contextual information for the Nano extension.
 */
public class NanoExtensionContext {

    private final CryptoCurrency currency;
    private final IExtensionContext extensionContext;
    private final NanoUtil nanoUtil;

    public NanoExtensionContext(CryptoCurrency currency, IExtensionContext extensionContext,
                                NanoUtil nanoUtil) {
        this.currency = currency;
        this.extensionContext = extensionContext;
        this.nanoUtil = nanoUtil;
    }


    public String getCurrencyCode() {
        return currency.getCode();
    }

    public IExtensionContext getExtensionContext() {
        return extensionContext;
    }

    public NanoUtil getUtil() {
        return nanoUtil;
    }

}
