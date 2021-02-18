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

import java.net.URL;
import java.util.UUID;
import java.io.IOException;
import java.math.BigInteger;

import uk.oczadly.karl.jnano.model.NanoAccount;
import uk.oczadly.karl.jnano.model.NanoAmount;
import uk.oczadly.karl.jnano.model.HexData;
import uk.oczadly.karl.jnano.rpc.RpcQueryNode;
import uk.oczadly.karl.jnano.rpc.exception.RpcEntityNotFoundException;
import uk.oczadly.karl.jnano.rpc.request.node.RequestAccountBalance;
import uk.oczadly.karl.jnano.rpc.request.wallet.RequestSend;
import uk.oczadly.karl.jnano.rpc.response.*;
import uk.oczadly.karl.jnano.rpc.request.wallet.RequestAccountCreate;
import uk.oczadly.karl.jnano.rpc.request.node.RequestAccountInfo;
import uk.oczadly.karl.jnano.rpc.request.node.RequestBlockInfo;
import uk.oczadly.karl.jnano.rpc.request.node.RequestPending;
import uk.oczadly.karl.jnano.rpc.exception.RpcException;

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


    public HexData sendFromWallet(String walletId, NanoAccount sourceAcc, NanoAccount destination, NanoAmount amount)
            throws IOException, RpcException {
        ResponseBlockHash hash = rpc.processRequest(new RequestSend(
                walletId, sourceAcc.toAddress(), destination.toAddress(), amount.getAsRaw(),
                UUID.randomUUID().toString()));
        return hash.getBlockHash();
    }


//    public static void main(String[] args) {
//        // You need to have node running: i.e.: nano_node --daemon with rpc enabled as
//        // well as a wallet set up.
//
//        try {
//            NanoRPCClient client = new NanoRPCClient("http://[::1]:7076");
//            String walletId = "2D74D34B4892288866EEE29AC3A233ADC6BB6EB8021ECE3EADC87F3FED1FACF3";
//            String sourceAddress = "nano_15erffyd59fiy1muzwn7pkq37o197pixbz8gcjwpi8mwiuuxgqe7zy7uyw65";
//            String destinationAddress = "nano_3pszk9xf4mogtf8yebwurjcyhbtscun4hq596sxym7roxwt9gy8ieou1jj9i";
//
//            BigInteger balance = client.getBalance(sourceAddress);
//            BigDecimal amount = new BigDecimal("0.00001");
//            String hash = client.sendFrom(walletId, sourceAddress, destinationAddress, amount);
//
//            ResponseBlockInfo blockInfo = client.getBlockInfo(new HexData(hash));
//            if (blockInfo.getAmount().getAsNano().compareTo(amount) != 0) {
//                System.out.println("Incorrect blockInfo response");
//                return;
//            }
//
//            if (balance.subtract(NanoAmount.valueOfNano(amount).getAsRaw())
//                    .compareTo(client.getBalance(sourceAddress)) != 0) {
//                System.out.println("Sending from wallet not working correctly");
//                return;
//            }
//
//            // Generate new crypto address
//            client.newAccount(walletId);
//
//            System.out.println("Completed!");
//        } catch (IOException e) {
//            System.out.println(e.toString());
//        } catch (RpcException e) {
//            System.out.println(e.toString());
//        }
//    }

}
