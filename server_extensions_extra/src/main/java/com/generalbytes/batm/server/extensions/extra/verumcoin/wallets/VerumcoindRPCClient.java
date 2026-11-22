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
package com.generalbytes.batm.server.extensions.extra.verumcoin.wallets;

import java.net.MalformedURLException;
import java.util.Map;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import wf.bitcoin.javabitcoindrpcclient.GenericRpcException;
import java.util.logging.Logger;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;

public class VerumcoindRPCClient extends RPCClient {

    private static final Logger logger = Logger.getLogger(VerumcoindRPCClient.class.getCanonicalName());


    public VerumcoindRPCClient(String rpcUrl) throws MalformedURLException {
        super(CryptoCurrency.VERUM.getCode(), rpcUrl);
    }

    @Override
    public Transaction getTransaction(String txId) {
        return new VerumcoinTransactionWrapper((Map) query("gettransaction", txId));
    }

    @Override
    public double getEstimateFee(int numberOfBlocks) throws BitcoinRPCException {
        Map result = (Map) query("estimatesmartfee", numberOfBlocks);
        Object estimate = result.get("feerate");
        if (estimate == null) {
            throw new GenericRpcException("getEstimateFee - no result: " + result.toString());
        }
        return ((Number)estimate).doubleValue();
    }

    @Override
    public String signRawTransactionWithWallet(String hex, String sigHashType) {
        Map result = (Map) query("signrawtransaction", hex, null, null, sigHashType); //if sigHashType is null it will return the default "ALL"
        if ((Boolean) result.get("complete"))
            return (String) result.get("hex");
        else
            throw new GenericRpcException("Incomplete");
    }


    @SuppressWarnings("serial")
    class VerumcoinTransactionWrapper extends MapWrapper implements VerumcoinTransaction, Serializable {

        @SuppressWarnings("rawtypes")
        public VerumcoinTransactionWrapper(Map m) {
            super(m);
        }

        @Override
        public String account() {
            return mapStr(m, "account");
        }

        @Override
        public String address() {
            return mapStr(m, "address");
        }

        @Override
        public String category() {
            return mapStr(m, "category");
        }

        @Override
        public BigDecimal amount() {
            return mapBigDecimal(m, "amount");
        }

        @Override
        public BigDecimal fee() {
            return mapBigDecimal(m, "fee");
        }

        @Override
        public int confirmations() {
            return mapInt(m, "confirmations");
        }

        @Override
        public String blockHash() {
            return mapStr(m, "blockhash");
        }

        @Override
        public int blockIndex() {
            return mapInt(m, "blockindex");
        }

        @Override
        public Date blockTime() {
            return mapCTime(m, "blocktime");
        }

        @Override
        public String txId() {
            return mapStr(m, "txid");
        }

        @Override
        public Date time() {
            return mapCTime(m, "time");
        }

        @Override
        public Date timeReceived() {
            return mapCTime(m, "timereceived");
        }

        @Override
        public String comment() {
           return mapStr(m, "comment");
        }

        @Override
        public String commentTo() {
            return mapStr(m, "to");
        }

        @Override
        public boolean generated() {
            return mapBool(m, "generated");
        }

        @Override
        public boolean instantlock() {
            return mapBool(m, "instantlock");
        }

        @Override
        public boolean instantlock_internal() {
            return mapBool(m, "instantlock_internal");
        }

        private RawTransaction raw = null;

        @Override
        public RawTransaction raw() {
        if (raw == null)
            try {
                raw = getRawTransaction(txId());
            } catch (GenericRpcException ex) {
                logger.warning(ex.getMessage());
            }
            return raw;
        }

        @Override
        public String toString() {
            return m.toString();
        }
    }
}