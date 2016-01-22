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

package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IPaymentProcessor;
import com.generalbytes.batm.server.extensions.IPaymentProcessorPaymentResponse;
import com.generalbytes.batm.server.extensions.IPaymentProcessorPaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BitcoinPayPP implements IPaymentProcessor{
    private static final Logger log = LoggerFactory.getLogger(BitcoinPayPP.class);

    private String apiKey;
    private IBitcoinPay api;

    public BitcoinPayPP(String apiKey) {
        this.apiKey = apiKey;
        api = RestProxyFactory.createProxy(IBitcoinPay.class, "https://www.bitcoinpay.com/");
    }

    @Override
    public IPaymentProcessorPaymentResponse requestPayment(BigDecimal amount, String currency, String settledCurrency, String reference) {
        if (reference == null) {
            reference ="Empty";
        }
        BitcoinPayPaymentResponseDTO r = api.createNewPaymentRequest("Token " + apiKey, new BitcoinPayPaymentRequestRequestDTO(currency, amount, settledCurrency, reference));
        if (r != null) {
            return new BitcoinPayPPResponse(r.getData().address,r.getData().paid_amount,r.getData().paid_currency,r.getData().settled_amount,r.getData().settled_currency,r.getData().payment_id,r.getData().reference);
        }else{
            log.error("Payment request call to Payment Processor failed.");
            return null;
        }
    }

    @Override
    public IPaymentProcessorPaymentStatus getPaymentStatus(String paymentId) {
        if (paymentId != null) {
            BitcoinPayPaymentResponseDTO s = api.getPaymentStatus("Token " + apiKey, paymentId);
            if (s == null) {
                log.error("Payment status call to Payment Processor failed.");
            }else{
                String statusString = s.getData().status;
                int statusInt = IPaymentProcessorPaymentStatus.STATUS_INVALID;
                if ("pending".equalsIgnoreCase(statusString)) {
                    statusInt = IPaymentProcessorPaymentStatus.STATUS_PENDING;
                }else if ("received".equalsIgnoreCase(statusString)) {
                    statusInt = IPaymentProcessorPaymentStatus.STATUS_RECEIVED;
                }else if ("insufficient_amount".equalsIgnoreCase(statusString)) {
                    statusInt = IPaymentProcessorPaymentStatus.STATUS_INSUFFICIENT_AMOUNT;
                }else if ("invalid".equalsIgnoreCase(statusString)) {
                    statusInt = IPaymentProcessorPaymentStatus.STATUS_INVALID;
                }else if ("timeout".equalsIgnoreCase(statusString)) {
                    statusInt = IPaymentProcessorPaymentStatus.STATUS_TIMEOUT;
                }else if ("confirmed".equalsIgnoreCase(statusString)) {
                    statusInt = IPaymentProcessorPaymentStatus.STATUS_CONFIRMED;
                }
                return new BitcoinPayPPStatus(statusInt);
            }
        }else{
            log.error("Invalid payment id");
        }
        return null;

    }

    class BitcoinPayPPStatus implements IPaymentProcessorPaymentStatus {
        private int status;

        BitcoinPayPPStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "BitcoinPayPPStatus{" +
                    "status=" + status +
                    '}';
        }
    }

    class BitcoinPayPPResponse implements IPaymentProcessorPaymentResponse {
        private String cryptoAddress;
        private BigDecimal cryptoAmount;
        private String cryptoCurrency;
        private BigDecimal fiatAmount;
        private String fiatCurrency;
        private String id;
        private String reference;

        BitcoinPayPPResponse(String cryptoAddress, BigDecimal cryptoAmount, String cryptoCurrency, BigDecimal fiatAmount, String fiatCurrency, String id, String reference) {
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
            return "BitcoinPayPPResponse{" +
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
}
