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
        return extractAssetBalance(query("getbalance"));
    }

    @Override
    public BigDecimal getReceivedByAddress(String address) throws GenericRpcException {
        return getReceivedByAddress(address,0);
    }

    @Override
    public BigDecimal getReceivedByAddress(String address, int minConf) throws GenericRpcException {
        return extractAssetBalance(query("getreceivedbyaddress", address, minConf));
    }

    private BigDecimal extractAssetBalance(Object assetsBalanceInformation) {
        if (assetsBalanceInformation == null) {
            return null;
        }
        if (assetsBalanceInformation instanceof BigDecimal balance) {
            return balance;
        } else if (assetsBalanceInformation instanceof Map<?, ?> balancesByAssets) {
            return (BigDecimal) balancesByAssets.get(assetName);
        }
        return null;
    }
}
