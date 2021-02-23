/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.nano.wallets.node;

import uk.oczadly.karl.jnano.model.HexData;
import uk.oczadly.karl.jnano.model.NanoAccount;
import uk.oczadly.karl.jnano.model.NanoAmount;
import uk.oczadly.karl.jnano.rpc.RpcQueryNode;
import uk.oczadly.karl.jnano.rpc.exception.RpcEntityNotFoundException;
import uk.oczadly.karl.jnano.rpc.exception.RpcException;
import uk.oczadly.karl.jnano.rpc.request.node.RequestAccountBalance;
import uk.oczadly.karl.jnano.rpc.request.node.RequestAccountInfo;
import uk.oczadly.karl.jnano.rpc.request.node.RequestBlockInfo;
import uk.oczadly.karl.jnano.rpc.request.node.RequestPending;
import uk.oczadly.karl.jnano.rpc.request.wallet.RequestAccountCreate;
import uk.oczadly.karl.jnano.rpc.request.wallet.RequestSend;
import uk.oczadly.karl.jnano.rpc.response.ResponseAccountInfo;
import uk.oczadly.karl.jnano.rpc.response.ResponseBlockHash;
import uk.oczadly.karl.jnano.rpc.response.ResponseBlockInfo;
import uk.oczadly.karl.jnano.rpc.response.ResponsePending;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.UUID;

public class NanoRPCClient {

    private static final int MAX_PENDING_BLOCKS = 250;
    private static final BigInteger PENDING_THRESHOLD = new BigInteger("100000000000000000000");

    private final RpcQueryNode rpc;

    public NanoRPCClient(URL url) {
        rpc = new RpcQueryNode(url);
    }


    public NanoAccount newWalletAccount(String walletId) throws IOException, RpcException {
        return rpc.processRequest(new RequestAccountCreate(walletId)).getAccountAddress();
    }

    /** Returns the confirmed account balance. */
    public NanoAmount getBalanceConfirmed(NanoAccount address) throws IOException, RpcException {
        try {
            // Get the account frontier hash
            ResponseAccountInfo accountInfo = rpc.processRequest(new RequestAccountInfo(address.toAddress()));
            if (accountInfo.getConfirmationHeightFrontier() != null) {
                // Get the balance of the block
                ResponseBlockInfo block = rpc.processRequest(new RequestBlockInfo(
                    accountInfo.getFrontierBlockHash().toHexString()));
                if (block.isConfirmed()) // Assertion, should be confirmed already
                    return block.getBalance();
            }
        } catch (RpcEntityNotFoundException ignored) {} // Not found = account not created yet
        return NanoAmount.ZERO;
    }

    /** Returns account balance + pending block amount which is confirmed. */
    public NanoAmount getTotalBalanceConfirmed(NanoAccount address) throws IOException, RpcException {
        // Get the balance
        NanoAmount balance = getBalanceConfirmed(address);

        // Get pending blocks (confirmed only)
        ResponsePending pendingBlocks = rpc.processRequest(new RequestPending(
            address.toAddress(), MAX_PENDING_BLOCKS, PENDING_THRESHOLD, false, true, true));
        // Sum pending blocks
        NanoAmount pending = pendingBlocks.getPendingBlocks().values().stream()
            .map(ResponsePending.PendingBlock::getAmount)
            .reduce(NanoAmount.ZERO, NanoAmount::add);

        return balance.add(pending);
    }

    /** Returns account balance + pending block amount which may contain unconfirmed transactions. */
    public NanoAmount getTotalBalanceUnconfirmed(NanoAccount address) throws IOException, RpcException {
        return rpc.processRequest(new RequestAccountBalance(address.toAddress())).getTotal();
    }


    public String sendFromWallet(String walletId, NanoAccount sourceAcc, NanoAccount destination, NanoAmount amount)
            throws IOException, RpcException {
        ResponseBlockHash hash = rpc.processRequest(new RequestSend(
                walletId, sourceAcc.toAddress(), destination.toAddress(), amount.getAsRaw(),
                UUID.randomUUID().toString()));
        return hash.getBlockHash().toHexString();
    }

}
