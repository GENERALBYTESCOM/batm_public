package com.generalbytes.batm.server.extensions.extra.nano.wallet.node;

import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoWsClient;

/**
 * @author Karl Oczadly
 */
public interface INanoRpcWallet {

    NanoRpcClient getRpcClient();

    NanoWsClient getWsClient();

    void moveFundsToHotWallet(String depositAddress);

}
