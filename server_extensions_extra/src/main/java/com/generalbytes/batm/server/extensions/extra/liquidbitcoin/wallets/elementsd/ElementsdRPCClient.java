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
        return getAssetBalance(query("getbalance"));
    }

    @Override
    public BigDecimal getReceivedByAddress(String address) throws GenericRpcException {
        return getReceivedByAddress(address,0);
    }

    @Override
    public BigDecimal getReceivedByAddress(String address, int minConf) throws GenericRpcException {
        return getAssetBalance(query("getreceivedbyaddress", address, minConf));
    }

    private BigDecimal getAssetBalance(Object balance) {
        if (balance == null) {
            return null;
        }
        if (balance instanceof BigDecimal) {
            return (BigDecimal) balance;
        } else if (balance instanceof Map<?, ?> assets) {
            return (BigDecimal) assets.get(assetName);
        }
        return null;
    }
}
