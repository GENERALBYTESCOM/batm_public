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
package com.generalbytes.batm.server.extensions.extra.nano.test;

import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoRPCClient;
import uk.oczadly.karl.jnano.model.NanoAccount;
import uk.oczadly.karl.jnano.model.NanoAmount;

import java.math.BigDecimal;
import java.net.URL;

/**
 * @author Karl Oczadly
 */
public class TestRPCClient {

//    public static void main(String[] args) throws Exception {
//        // Creates a new wallet for testing
//        RpcQueryNode node = new RpcQueryNode();
//        String newWalletId = node.processRequest(new RequestWalletCreate()).getWalletId().toHexString();
//        System.out.printf("Wallet ID: %s%n", newWalletId);
//        System.out.printf("Account: %s%n", node.processRequest(new RequestAccountCreate(newWalletId)).getAccountAddress());
//    }

    public static void main(String[] args) {
        // You need to have node running: i.e.: nano_node --daemon with rpc enabled and a wallet set up.

        try {
            String host = "http://[::1]:7076";
            String walletId = "DEC85FE85400D1979622EEB7CE62DD6A455ED423C3FA58A92171E1C623288056";
            NanoAccount sourceAddress = NanoAccount.parse(
                "nano_3wbf3ojxic8rmaduob7mkn374zt9ro411d7piduduy9rm64ifzkb1qazwdpo");
            NanoAccount destinationAddress = NanoAccount.parse(
                "nano_1rom5kz795itb5tb8t7ogygqjppsg8656uc9oatrsjc6zr6fmsjeodntq8z8");
            NanoAmount amount = NanoAmount.valueOfNano(new BigDecimal("0.000001"));


            NanoRPCClient client = new NanoRPCClient(new URL(host));

            NanoAmount balInitial = client.getTotalBalanceConfirmed(sourceAddress);
            System.out.printf("Initial balance (+ pending) of %s: %s%n", sourceAddress, balInitial);

            String hash = client.sendFromWallet(walletId, sourceAddress, destinationAddress, amount);
            System.out.printf("Sent %s from wallet, hash: %s%n", amount, hash);
            Thread.sleep(100);

            NanoAmount balAfter = client.getTotalBalanceUnconfirmed(sourceAddress);
            System.out.printf("New (unconfirmed) balance (+ pending) of %s: %s%n", sourceAddress, balAfter);

            if (balInitial.subtract(amount).compareTo(balAfter) != 0) {
                System.err.println("Balance after was an incorrect amount.");
                return;
            }

            // Generate new wallet address
            NanoAccount newAccount = client.newWalletAccount(walletId);
            System.out.println("Created new wallet account: " + newAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
