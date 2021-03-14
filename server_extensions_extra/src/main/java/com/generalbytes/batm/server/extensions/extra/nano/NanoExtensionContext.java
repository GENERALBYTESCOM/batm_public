package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;

/**
 * Contains various contextual information for the Nano extension.
 */
public class NanoExtensionContext {

    private volatile NanoRpcClient rpcClient = null;
    private final CryptoCurrency currency;
    private final IExtensionContext extensionContext;
    private final NanoCurrencyUtil nanoCurrencyUtil;

    public NanoExtensionContext(CryptoCurrency currency, IExtensionContext extensionContext,
                                NanoCurrencyUtil nanoCurrencyUtil) {
        this.currency = currency;
        this.extensionContext = extensionContext;
        this.nanoCurrencyUtil = nanoCurrencyUtil;
    }


    public String getCurrencyCode() {
        return currency.getCode();
    }

    public NanoRpcClient getRpcClient() {
        return rpcClient;
    }

    public void setRpcClient(NanoRpcClient rpcClient) {
        if (rpcClient != null)
            this.rpcClient = rpcClient;
    }

    public IExtensionContext getExtensionContext() {
        return extensionContext;
    }

    public NanoCurrencyUtil getUtil() {
        return nanoCurrencyUtil;
    }

}
