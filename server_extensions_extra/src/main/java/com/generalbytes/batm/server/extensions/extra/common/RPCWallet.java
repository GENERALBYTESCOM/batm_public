/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.common;

import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import com.generalbytes.batm.server.extensions.IWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RPCWallet implements IWallet, IRPCWallet {
    private static final Logger log = LoggerFactory.getLogger(RPCWallet.class);
    private String cryptoCurrency;

    private String label;
    private RPCClient client;


    public RPCWallet(String rpcURL, String label, String cryptoCurrency) {
        this.label = label;
        this.cryptoCurrency = cryptoCurrency;
        client = createClient(cryptoCurrency, rpcURL);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(cryptoCurrency);
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return cryptoCurrency;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("RPCWallet wallet error: unknown cryptocurrency.");
            return null;
        }

        log.info("RPCWallet sending {} {} to {}", amount, cryptoCurrency, destinationAddress);
        try {
            String result = client.sendToAddress(destinationAddress, amount, description);
            log.debug("result = " + result);
            return result;
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("RPCWallet wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            Map<String, Map<String, String>> addresses = client.getAddressesByLabel(label);
            if (addresses != null && addresses.size() > 0) {
                for (Map.Entry<String, Map<String, String>> e : addresses.entrySet()) {
                    if (e.getValue().get("purpose") == null || "receive".equals(e.getValue().get("purpose"))) {
                        return e.getKey();
                    }
                }
            }
        } catch (BitcoinRPCException e) {
            try {
                if (e.getRPCError() != null && e.getRPCError().getMessage() != null && e.getRPCError().getMessage().contains("No addresses with label")) {
                    log.warn("generating new address. " + e.getMessage());
                    return generateNewDepositCryptoAddress(cryptoCurrency, label);
                }
                if (e.getResponseCode() == 404) {
                    log.warn("getaddressesbylabel not supported, using deprecated Accounts API. " + e.getResponse());
                    List<String> addressesByAccount = client.getAddressesByAccount(label);
                    if (addressesByAccount != null)
                        if (addressesByAccount.size() > 0) {
                            return addressesByAccount.get(0);
                        } else {
                            log.warn("getAddressesByAccount returned no address, generating new address");
                            return generateNewDepositCryptoAddress(cryptoCurrency, label);
                        }
                }
            } catch (Exception e1) {
                log.error("Error", e1);
            }
            log.error("Error", e);
        }
        return null;
    }

    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("RPCWallet wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            return client.getNewAddress(label);
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("RPCWallet wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            return client.getBalance();
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
            return null;
        }
    }

    private static RPCClient createClient(String cryptoCurrency, String rpcURL) {
        try {
            return new RPCClient(cryptoCurrency, rpcURL);
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }
        return null;
    }

    public RPCClient getClient() {
        return client;
    }
}
