/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.GenericRpcException;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RPCClient extends BitcoinJSONRPCClient {
    private String rpcURL;
    private String cryptoCurrency;


    public RPCClient(String cryptoCurrency, String rpcUrl) throws MalformedURLException {
        super(rpcUrl);
        this.rpcURL = rpcUrl;
        this.cryptoCurrency = cryptoCurrency;
    }

    /**
     * bitcoin-abc >= 0.20 needs estimatefee without any params
     *
     * @return
     * @throws BitcoinRPCException
     */
    public double getEstimateFee() throws BitcoinRPCException {
        return ((Number) query("estimatefee")).doubleValue();
    }

    public double getEstimateFee(int numberOfBlocks) throws BitcoinRPCException {
        return ((Number)query("estimatefee",numberOfBlocks)).doubleValue();
    }

    public String signRawTransactionWithWallet(String hex, String sigHashType) {

        Map result = (Map) query("signrawtransactionwithwallet", hex, null, sigHashType); //if sigHashType is null it will return the default "ALL"
        if ((Boolean) result.get("complete"))
            return (String) result.get("hex");
        else
            throw new GenericRpcException("Incomplete");
    }

    /**
     * See https://bitcoin.org/en/developer-reference#sendmany
     * @param addressAmouts map of destination addresses and amounts to be sent to each of them
     * @param comment
     * @return txid
     * @throws GenericRpcException
     */
    public String sendMany(Map<String, BigDecimal> addressAmouts, String comment) throws GenericRpcException {
        return (String) query("sendmany", "", addressAmouts, null, comment);
    }

    public interface ReceivedAddress {
        String address();
        String account();
        double amount();
        int confirmations();
        String label();
        List<String> txids();
    }
    public List<ReceivedAddress> listReceivedByAddress2(int minConf) throws BitcoinRPCException {
        return new ReceivedAddressListWrapper((List)this.query("listreceivedbyaddress", minConf));
    }


    private static class ReceivedAddressListWrapper extends AbstractList<ReceivedAddress> {
        private final List<Map<String, Object>> wrappedList;

        public ReceivedAddressListWrapper(List<Map<String, Object>> wrappedList) {
            this.wrappedList = wrappedList;
        }

        public ReceivedAddress get(int index) {
            final Map<String, Object> e = (Map)this.wrappedList.get(index);
            return new ReceivedAddress() {
                public String address() {
                    return RPCClient.cleanAddressFromPossiblePrefix((String)e.get("address"));
                }

                public String label() {
                    return (String)e.get("label");
                }

                public String account() {
                    return (String)e.get("account");
                }

                public double amount() {
                    return ((Number)e.get("amount")).doubleValue();
                }

                public int confirmations() {
                    return ((Number)e.get("confirmations")).intValue();
                }
                public List<String> txids() {
                    return ((List<String>)e.get("txids"));
                }

                public String toString() {
                    return e.toString();
                }
            };
        }

        public int size() {
            return this.wrappedList.size();
        }
    }


    /**
     *
     * @param label
     * @return address -> {"purpose" -> "receive"}
     * @throws GenericRpcException
     */
    public Map<String, Map<String, String>> getAddressesByLabel(String label) throws GenericRpcException {
        return (Map<String, Map<String, String>>) query("getaddressesbylabel", label);
    }

    public static String cleanAddressFromPossiblePrefix(String address) {
        if (address.contains(":")) {
            address = address.substring(address.indexOf(":")+1);
        }
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RPCClient rpcClient = (RPCClient) o;
        return Objects.equals(rpcURL, rpcClient.rpcURL) &&
            Objects.equals(cryptoCurrency, rpcClient.cryptoCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rpcURL, cryptoCurrency);
    }
}
