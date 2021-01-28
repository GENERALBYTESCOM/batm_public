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
package com.generalbytes.batm.server.extensions.extra.nano.wallets;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import uk.oczadly.karl.jnano.model.NanoAmount;
import uk.oczadly.karl.jnano.model.HexData;
import uk.oczadly.karl.jnano.rpc.RpcQueryNode;
import uk.oczadly.karl.jnano.rpc.request.wallet.RequestSend;
import uk.oczadly.karl.jnano.rpc.response.ResponseBlockHash;
import uk.oczadly.karl.jnano.rpc.request.wallet.RequestAccountCreate;
import uk.oczadly.karl.jnano.rpc.response.ResponseAccount;
import uk.oczadly.karl.jnano.rpc.request.node.RequestAccountInfo;
import uk.oczadly.karl.jnano.rpc.response.ResponseAccountInfo;
import uk.oczadly.karl.jnano.rpc.request.node.RequestBlockInfo;
import uk.oczadly.karl.jnano.rpc.response.ResponseBlockInfo;
import uk.oczadly.karl.jnano.rpc.request.node.RequestPending;
import uk.oczadly.karl.jnano.rpc.response.ResponsePending;
import uk.oczadly.karl.jnano.rpc.exception.RpcException;

public class NanoRPCClient {

    public static final Charset QUERY_CHARSET = Charset.forName("ISO8859-1");

    // Count is just an unattainble number in case of pending bloat attacks.
    final int pendingBlocksCount = 1000000;

    RpcQueryNode rpc;

    public NanoRPCClient(String url) throws MalformedURLException {
        rpc = new RpcQueryNode(new URL(url));
    }

    public BigInteger getBalance(final String address) throws IOException, RpcException {
        RequestAccountInfo request = new RequestAccountInfo(address);
        ResponseAccountInfo accountInfo = rpc.processRequest(request);
        return accountInfo.getBalanceConfirmed().getAsRaw();
    }

    public ResponseBlockInfo getBlockInfo(HexData hash) throws IOException, RpcException {
        RequestBlockInfo blockInfoRequest = new RequestBlockInfo(hash.toHexString());
        return rpc.processRequest(blockInfoRequest);
    }

    public BigInteger getConfirmedBalance(final String address) throws IOException, RpcException {
        RequestAccountInfo request = new RequestAccountInfo(address);
        ResponseAccountInfo accountInfo = rpc.processRequest(request);
        HexData confirmation_height_frontier = accountInfo.getConfirmationHeightFrontier();
        return getBlockInfo(confirmation_height_frontier).getBalance().getAsRaw();
    }

    public String newAccount(String walletId) throws IOException, RpcException {
        RequestAccountCreate request = new RequestAccountCreate(walletId);
        ResponseAccount account = rpc.processRequest(request);
        return account.getAccountAddress().toAddress();
    }

    public BigInteger getPendingBalance(final String address) throws IOException, RpcException {
        return getPendingBalanceImpl(address, false);
    }

    public BigInteger getConfirmedPendingBalance(final String address) throws IOException, RpcException {
        return getPendingBalanceImpl(address, true);
    }

    public BigInteger getPendingBalanceImpl(final String address, final boolean includeOnlyConfirmed)
            throws IOException, RpcException {
        RequestPending request = new RequestPending(address, pendingBlocksCount, new BigInteger("0"), false, true,
                includeOnlyConfirmed);
        ResponsePending response = rpc.processRequest(request);
        Map<HexData, ResponsePending.PendingBlock> pendingBlocks = response.getPendingBlocks();
        BigInteger total = new BigInteger("0");
        for (ResponsePending.PendingBlock pendingBlock : pendingBlocks.values()) {
            total = total.add(pendingBlock.getAmount().getAsRaw());
        }
        return total;
    }

    public String sendFrom(String walletId, String fromAccount, String toAddress, BigDecimal amount)
            throws IOException, RpcException {
        int id = (int) Math.floor(Math.random() * Integer.MAX_VALUE);
        RequestSend request = new RequestSend(walletId, fromAccount, toAddress,
                NanoAmount.valueOfNano(amount).getAsRaw(), Integer.toString(id));
        ResponseBlockHash hash = rpc.processRequest(request);
        return hash.getBlockHash().toHexString();
    }

    public static void main(String[] args) {

        // You need to have node running: i.e.: nano_node --daemon with rpc enabled as
        // well as a wallet set up.

        try {
            NanoRPCClient client = new NanoRPCClient("http://[::1]:7076");
            String walletId = "2D74D34B4892288866EEE29AC3A233ADC6BB6EB8021ECE3EADC87F3FED1FACF3";
            String sourceAddress = "nano_15erffyd59fiy1muzwn7pkq37o197pixbz8gcjwpi8mwiuuxgqe7zy7uyw65";
            String destinationAddress = "nano_3pszk9xf4mogtf8yebwurjcyhbtscun4hq596sxym7roxwt9gy8ieou1jj9i";

            BigInteger balance = client.getBalance(sourceAddress);
            BigDecimal amount = new BigDecimal("0.00001");
            String hash = client.sendFrom(walletId, sourceAddress, destinationAddress, amount);

            ResponseBlockInfo blockInfo = client.getBlockInfo(new HexData(hash));
            if (blockInfo.getAmount().getAsNano().compareTo(amount) != 0) {
                System.out.println("Incorrect blockInfo response");
                return;
            }

            if (balance.subtract(NanoAmount.valueOfNano(amount).getAsRaw())
                    .compareTo(client.getBalance(sourceAddress)) != 0) {
                System.out.println("Sending from wallet not working correctly");
                return;
            }

            // Generate new crypto address
            client.newAccount(walletId);

            System.out.println("Completed!");
        } catch (IOException e) {
            System.out.println(e.toString());
        } catch (RpcException e) {
            System.out.println(e.toString());
        }
    }
}
