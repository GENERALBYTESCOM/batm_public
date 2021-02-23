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

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.nano.NanoCurrencySpecification;
import com.generalbytes.batm.server.extensions.extra.nano.util.StringTokenizerV2;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.oczadly.karl.jnano.model.HexData;
import uk.oczadly.karl.jnano.model.NanoAccount;
import uk.oczadly.karl.jnano.model.NanoAmount;
import uk.oczadly.karl.jnano.rpc.exception.RpcException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class NanoNodeWallet implements IWallet, IQueryableWallet, IGeneratesNewDepositCryptoAddress {

    private static final Logger log = LoggerFactory.getLogger(NanoNodeWallet.class);

    private final NanoCurrencySpecification currencySpec;
    private final NanoRPCClient rpcClient;
    private final NanoWSClient wsClient;
    private final String walletId;
    private final NanoAccount walletAccount;

    public NanoNodeWallet(NanoCurrencySpecification currencySpec, NanoRPCClient rpcClient, NanoWSClient wsClient,
                          String walletId, String walletAccount) {
        this.currencySpec = currencySpec;
        this.rpcClient = rpcClient;
        this.wsClient = wsClient;
        this.walletId = walletId;
        this.walletAccount = walletAccount != null ? currencySpec.parseAddress(walletAccount) : null;

        log.info("Created NanoNodeWallet instance. Using websocket: {}, using wallet: {}",
                wsClient != null, walletId != null && walletAccount != null);
    }


    public NanoWSClient getWebSocketClient() {
        return wsClient;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(currencySpec.getCurrencyCode());
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return currencySpec.getCurrencyCode();
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!currencySpec.getCurrencyCode().equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency.");
            return null;
        }

        NanoAmount nanoAmount = NanoAmount.valueOfNano(amount);
        NanoAccount destination = currencySpec.parseAddress(destinationAddress);

        log.info("Sending {} from node wallet {} to {}", nanoAmount, walletAccount, destinationAddress);
        try {
            String hash = rpcClient.sendFromWallet(walletId, walletAccount, destination, nanoAmount);
            log.debug("Sent funds, block hash = " + hash);
            return hash;
        } catch (RpcException | IOException e) {
            log.error("Couldn't send coins.", e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!currencySpec.getCurrencyCode().equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency.");
            return null;
        }
        return walletAccount.toAddress();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!currencySpec.getCurrencyCode().equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            return rpcClient.getBalanceConfirmed(walletAccount).getAsNano();
        } catch (RpcException | IOException e) {
            log.error("Couldn't retrieve balance.", e);
            return null;
        }
    }

    @Override
    public ReceivedAmount getReceivedAmount(String addrString, String cryptoCurrency) {
        if (!currencySpec.getCurrencyCode().equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            NanoAccount addr = currencySpec.parseAddress(addrString);

            // Check for a confirmed balance
            NanoAmount balConf = rpcClient.getTotalBalanceConfirmed(addr);
            if (balConf.compareTo(NanoAmount.ZERO) > 0) {
                return new ReceivedAmount(balConf.getAsNano(), 1);
            }

            // No balance; return pending blocks with confirmation 0
            NanoAmount balUnconf = rpcClient.getTotalBalanceUnconfirmed(addr);
            return new ReceivedAmount(balUnconf.getAsNano(), 0);
        } catch (RpcException | IOException e) {
            log.error("Couldn't retrieve received amount.", e);
            return null;
        }
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (!currencySpec.getCurrencyCode().equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            return rpcClient.newWalletAccount(walletId).toAddress();
        } catch (RpcException | IOException e) {
            log.error("Couldn't generate new deposit address.", e);
            return null;
        }
    }


    public static NanoNodeWallet create(NanoCurrencySpecification addrSpec, StringTokenizerV2 args) throws Exception {
        /*
         * ORDER OF CONFIGURATION PARAMETERS:
         *  0  Node IP or host
         *  1  RPC protocol (http/https)
         *  2  RPC port
         *  3  Websocket protocol (ws/wss)
         *  4  Websocket port
         *  5  Hot wallet ID
         *  6  Hot wallet account
         */
        String nodeHost = args.next();
        if (nodeHost.startsWith("[")) {
            // IPv6 address
            StringJoiner sj = new StringJoiner(":");
            String str = nodeHost;
            do {
                sj.add(str);
            } while (!(str = args.next()).endsWith("]"));
            sj.add(str);
            nodeHost = sj.toString();
        } else if (nodeHost.isEmpty()) {
            nodeHost = "[::1]";
        }
        String rpcProtocol = args.next();
        int rpcPort = Integer.parseInt(args.next());
        String wsProtocol = args.next();
        String wsPortStr = args.next();
        int wsPort = wsPortStr.isEmpty() ? 0 : Integer.parseInt(wsPortStr);
        String walletId = args.hasNext() ? args.next() : null;
        String walletAccount = args.hasNext() ? args.next() : null;

        NanoRPCClient rpcClient = new NanoRPCClient(new URL(rpcProtocol, nodeHost, rpcPort, ""));
        NanoWSClient wsClient = null;
        if (wsPort > 0 && !wsProtocol.isEmpty())
            wsClient = new NanoWSClient(addrSpec, new URI(wsProtocol, "", nodeHost, wsPort, "", "", ""));
        return new NanoNodeWallet(addrSpec, rpcClient, wsClient, walletId, walletAccount);
    }

}
