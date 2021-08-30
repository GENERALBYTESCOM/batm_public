package com.generalbytes.batm.server.extensions.extra.nano.wallet.node;

import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoWsClient;

import java.math.BigInteger;

/**
 * @author Karl Oczadly
 */
public interface INanoRpcWallet {

    NanoRpcClient getRpcClient();

    NanoWsClient getWsClient();

    BigInteger sendAllFromWallet(String depositAddress, String destination);

    BigInteger moveFundsToHotWallet(String depositAddress);

}
