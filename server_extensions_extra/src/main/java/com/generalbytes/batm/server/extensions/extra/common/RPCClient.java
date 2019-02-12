/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RPCClient extends BitcoinJSONRPCClient {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.RPCClient");
    private String rpcURL;


    public RPCClient(String rpcUrl) throws MalformedURLException {
        super(rpcUrl);
        this.rpcURL = rpcUrl;
        setHostnameVerifier((s, sslSession) -> true);
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {}

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {}

                @Override
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }}, null);
            setSslSocketFactory(sslcontext.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            log.error("Error", e);
        } catch (KeyManagementException e) {
            log.error("Error", e);
        }
    }

    public double getEstimateFee(int numberOfBlocks) throws BitcoinRPCException {
        return ((Number)query("estimatefee",numberOfBlocks)).doubleValue();
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
        return Objects.equals(rpcURL, rpcClient.rpcURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rpcURL);
    }
}
