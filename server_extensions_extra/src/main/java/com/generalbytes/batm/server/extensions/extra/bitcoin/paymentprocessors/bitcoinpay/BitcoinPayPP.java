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

package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.bitcoinpay;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.IPaymentProcessor;
import com.generalbytes.batm.server.extensions.IPaymentProcessorPaymentResponse;
import com.generalbytes.batm.server.extensions.IPaymentProcessorPaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class BitcoinPayPP implements IPaymentProcessor{
    private static final Logger log = LoggerFactory.getLogger(BitcoinPayPP.class);

    private String apiKey;
    private IBitcoinPay api;

    public BitcoinPayPP(String apiKey) {
        this.apiKey = apiKey;
        api = RestProxyFactory.createProxy(IBitcoinPay.class, "https://confirmo.net/");
    }

    @Override
    public IPaymentProcessorPaymentResponse requestPayment(BigDecimal fiatAmount, String fiatCurrency, String cryptoCurrency, String settledCurrency, String reference) {
        if (reference == null) {
            reference ="Empty";
        }
        try {
            BitcoinPayPaymentResponseDTO r = api.createNewPaymentRequest("Bearer " + apiKey, new BitcoinPayPaymentRequestRequestDTO(new BPProduct(), new BPInvoice(fiatAmount, fiatCurrency, cryptoCurrency), new BPSettlement(settledCurrency), null, null, null, reference));
            if (r != null) {
                String cryptoUri = r.getCryptoUri();
                StringTokenizer st = new StringTokenizer(cryptoUri, ":?=");
                String protocol = st.nextToken();
                String address = st.nextToken();
                st.nextToken();//amount
                BigDecimal cryptoAmount = new BigDecimal(st.nextToken());
                return new BitcoinPayPPResponse(address, cryptoAmount, cryptoCurrency, r.getMerchantAmount().getAmount(), r.getMerchantAmount().getCurrency(), r.getId(), r.getReference());
            } else {
                log.error("Payment request call to Payment Processor failed.");
            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP error {} : {}", e.getHttpStatusCode(), e.getHttpBody());
        } catch (IOException e) {
            log.error("Payment request call to Payment Processor failed.", e);
        }
        return null;
    }

    @Override
    public IPaymentProcessorPaymentStatus getPaymentStatus(String paymentId) {
        if (paymentId != null) {
            try {
                BitcoinPayPaymentResponseDTO s = api.getPaymentStatus("Bearer " + apiKey, paymentId);
                if (s == null) {
                    log.error("Payment status call to Payment Processor failed.");
                } else {
                    String statusString = s.getStatus();
                    int statusInt = IPaymentProcessorPaymentStatus.STATUS_INVALID;
                    if ("active".equalsIgnoreCase(statusString)) {
                        statusInt = IPaymentProcessorPaymentStatus.STATUS_PENDING;
                    } else if ("confirming".equalsIgnoreCase(statusString)) {
                        statusInt = IPaymentProcessorPaymentStatus.STATUS_RECEIVED;
                    } else if ("error".equalsIgnoreCase(statusString)) {
                        statusInt = IPaymentProcessorPaymentStatus.STATUS_INSUFFICIENT_AMOUNT;
                    } else if ("expired".equalsIgnoreCase(statusString)) {
                        statusInt = IPaymentProcessorPaymentStatus.STATUS_TIMEOUT;
                    } else if ("paid".equalsIgnoreCase(statusString)) {
                        statusInt = IPaymentProcessorPaymentStatus.STATUS_CONFIRMED;
                    }
                    return new BitcoinPayPPStatus(statusInt);
                }
            } catch (HttpStatusIOException e) {
                log.error("HTTP error {} : {}", e.getHttpStatusCode(), e.getHttpBody());
            } catch (IOException e) {
                log.error("Payment status call to Payment Processor failed.", e);
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
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        return result;

    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(FiatCurrency.USD.getCode());
        result.add(FiatCurrency.EUR.getCode());
        result.add(FiatCurrency.CZK.getCode());
        return result;
    }
}
