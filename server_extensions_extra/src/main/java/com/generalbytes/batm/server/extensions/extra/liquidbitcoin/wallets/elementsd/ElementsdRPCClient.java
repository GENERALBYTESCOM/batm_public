package com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd;

import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import wf.bitcoin.javabitcoindrpcclient.GenericRpcException;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.Map;

public class ElementsdRPCClient extends RPCClient {
    private final String assetName;
    public ElementsdRPCClient(String cryptoCurrency, String rpcUrl, String assetName) throws MalformedURLException {
        super(cryptoCurrency, rpcUrl);
        this.assetName = assetName;
    }

    @Override
    public BigDecimal getBalance() throws GenericRpcException {
        Object result = query("getbalance");
        if (result == null) {
            return null;
        }
        if (result instanceof BigDecimal balance) {
            return balance;
        } else if (result instanceof Map<?, ?> assets) {
            return (BigDecimal) assets.get(assetName);
        }
        return null;
    }
}
