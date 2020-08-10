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
package com.generalbytes.batm.server.extensions.extra.dash.wallets;

import java.net.MalformedURLException;
import java.util.Map;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import wf.bitcoin.javabitcoindrpcclient.GenericRpcException;
import com.generalbytes.batm.server.extensions.extra.dash.wallets.MapWrapper;
import java.util.logging.Logger;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;

public class DashRPCClient extends RPCClient {

    private static final Logger logger = Logger.getLogger(DashRPCClient.class.getCanonicalName());


    public DashRPCClient(String rpcUrl) throws MalformedURLException {
        super(CryptoCurrency.DASH.getCode(), rpcUrl);
    }

    @Override
    public Transaction getTransaction(String txId) {
        return new DashTransactionWrapper((Map) query("gettransaction", txId));
    }

    @SuppressWarnings("serial")
    class DashTransactionWrapper extends MapWrapper implements DashTransaction, Serializable {

        @SuppressWarnings("rawtypes")
        public DashTransactionWrapper(Map m) {
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