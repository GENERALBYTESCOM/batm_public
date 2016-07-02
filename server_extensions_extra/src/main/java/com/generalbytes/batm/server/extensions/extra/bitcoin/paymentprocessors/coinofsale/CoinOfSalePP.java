/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.coinofsale;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IPaymentProcessor;
import com.generalbytes.batm.server.extensions.IPaymentProcessorPaymentResponse;
import com.generalbytes.batm.server.extensions.IPaymentProcessorPaymentStatus;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class CoinOfSalePP implements IPaymentProcessor {
    private String token;
    private String pin;

    private ICoinOfSaleAPI api;

    public CoinOfSalePP(String token, String pin) {
        this.token = token;
        this.pin = pin;

        api = RestProxyFactory.createProxy(ICoinOfSaleAPI.class, "https://coinofsale.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
        return result;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.USD);
        result.add(ICurrencies.EUR);
        result.add(ICurrencies.CZK);
        return result;
    }

    @Override
    public IPaymentProcessorPaymentResponse requestPayment(BigDecimal amount, String currency, String settledCurrency, String reference) {
        CoSPaymentResponseDTO res = api.createPayment(token, pin, amount, currency);
        if (res != null) {
            return new COSPPResponse(res.getAddress(),res.getBitcoin_price(),ICurrencies.BTC,res.getFiat_price(),res.getFiat_currency(),res.getAddress(),reference);
        }
        return null;
    }

    @Override
    public IPaymentProcessorPaymentStatus getPaymentStatus(String paymentId) {
        CoSStatusResponseDTO res = api.getPaymentStatus(paymentId, token, true);
        if (res != null) {
            if ("unpaid".equalsIgnoreCase(res.getStatus())) {
                return new COSPPStatus(IPaymentProcessorPaymentStatus.STATUS_PENDING);
            }else if ("paid".equalsIgnoreCase(res.getStatus())) {
                return new COSPPStatus(IPaymentProcessorPaymentStatus.STATUS_RECEIVED);
            }else if ("error".equalsIgnoreCase(res.getStatus())) {
                return new COSPPStatus(IPaymentProcessorPaymentStatus.STATUS_INVALID);
            }
        }
        return null;
    }

    class COSPPStatus implements IPaymentProcessorPaymentStatus {
        private int status;

        COSPPStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "COSPPStatus{" +
                    "status=" + status +
                    '}';
        }
    }

    class COSPPResponse implements IPaymentProcessorPaymentResponse {
        private String cryptoAddress;
        private BigDecimal cryptoAmount;
        private String cryptoCurrency;
        private BigDecimal fiatAmount;
        private String fiatCurrency;
        private String id;
        private String reference;

        COSPPResponse(String cryptoAddress, BigDecimal cryptoAmount, String cryptoCurrency, BigDecimal fiatAmount, String fiatCurrency, String id, String reference) {
            this.cryptoAddress = cryptoAddress;
            this.cryptoAmount = cryptoAmount;
            this.cryptoCurrency = cryptoCurrency;
            this.fiatAmount = fiatAmount;
            this.fiatCurrency = fiatCurrency;
            this.id = id;
            this.reference = reference;
        }

        public String getCryptoAddress() {
            return cryptoAddress;
        }

        public BigDecimal getCryptoAmount() {
            return cryptoAmount;
        }

        public String getCryptoCurrency() {
            return cryptoCurrency;
        }

        public BigDecimal getFiatAmount() {
            return fiatAmount;
        }

        public String getFiatCurrency() {
            return fiatCurrency;
        }

        public String getId() {
            return id;
        }

        public String getReference() {
            return reference;
        }

        @Override
        public String toString() {
            return "COSPPResponse{" +
                    "cryptoAddress='" + cryptoAddress + '\'' +
                    ", cryptoAmount=" + cryptoAmount +
                    ", cryptoCurrency='" + cryptoCurrency + '\'' +
                    ", fiatAmount=" + fiatAmount +
                    ", fiatCurrency='" + fiatCurrency + '\'' +
                    ", id='" + id + '\'' +
                    ", reference='" + reference + '\'' +
                    '}';
        }
    }
}
