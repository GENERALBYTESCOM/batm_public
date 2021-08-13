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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd;

import com.generalbytes.batm.server.extensions.ILightningChannel;
import com.generalbytes.batm.server.extensions.ThrowingSupplier;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.AbstractLightningWallet;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Channel;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.Payment;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.PaymentRequest;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.RouteResponse;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto.SendPaymentResponse;
import com.generalbytes.batm.server.coinutil.CoinUnit;
import com.generalbytes.batm.server.extensions.util.net.HexStringCertTrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.HeaderParam;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LndWallet extends AbstractLightningWallet {

    private static final Logger log = LoggerFactory.getLogger(LndWallet.class);

    private final String url;
    private final String feeLimit;
    private final LndAPI api;

    /**
     * @param feeLimit maximum fee allowed in satoshis when sending the payment (e.g. "10")
     *                 or percentage of the payment's amount used as the maximum fee allowed when sending the payment (e.g. "1%")
     *                 or null for default: 0
     */
    public LndWallet(String url, String macaroon, String certHexString, String feeLimit) throws GeneralSecurityException {
        this.url = url;
        this.feeLimit = feeLimit;
        final ClientConfig config = new ClientConfig();
        config.addDefaultParam(HeaderParam.class, "Grpc-Metadata-macaroon", macaroon);
        if(certHexString != null) {
            config.setSslSocketFactory(HexStringCertTrustManager.getSslSocketFactory(certHexString));
        }
        this.api = RestProxyFactory.createProxy(LndAPI.class, url, config);
    }

    /**
     * @return paymentHash
     */
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {

        PaymentRequest paymentRequest = callChecked(cryptoCurrency, () -> api.decodePaymentRequest(destinationAddress));


        if (paymentRequest.num_satoshis != null) {
            log.info("Invoices with amount not supported");
            return null;
        }

        Payment payment = new Payment();
        payment.amt = CoinUnit.bitcoinToSat(amount).toString();
        payment.payment_request = destinationAddress;
        payment.fee_limit = getFeeLimit(feeLimit);

        log.info("Sending payment: {}", payment);
        SendPaymentResponse paymentResponse = callChecked(() -> api.sendPayment(payment));

        if (paymentResponse == null) {
            log.warn("SendPayment failed");
            return null;
        }

        if (paymentResponse.payment_error != null) {
            log.warn("SendPayment failed: {}", paymentResponse.payment_error);
            return null;
        }
        return paymentResponse.payment_preimage;

    }

    private Payment.FeeLimit getFeeLimit(String fee) {
        if (fee == null) {
            return null;
        }
        Payment.FeeLimit feeLimit = new Payment.FeeLimit();
        if (fee.contains("%")) {
            feeLimit.percent = fee.replace("%", "");
        } else {
            feeLimit.fixed = fee;
        }
        return feeLimit;
    }

    @Override
    public String getInvoice(BigDecimal cryptoAmount, String cryptoCurrency, Long paymentValidityInSec, String description) {
        Invoice invoice = new Invoice();
        invoice.value = CoinUnit.bitcoinToSat(cryptoAmount);
        invoice.memo = description;
        invoice.expiry = paymentValidityInSec;
        return callChecked(cryptoCurrency, () -> api.addInvoice(invoice).payment_request);
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> CoinUnit.satToBitcoin(api.getBalance().getBalance()));
    }

    @Override
    public String getPubKey() {
        return callChecked(() -> api.getInfo().identity_pubkey);
    }

    /**
     * @return paymentHash
     */
    @Override
    public BigDecimal getReceivedAmount(String destinationAddress, String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> {
            PaymentRequest paymentRequest = api.decodePaymentRequest(destinationAddress);
            Invoice invoice = api.getInvoice(paymentRequest.payment_hash);
            if (invoice.settled) {
                return CoinUnit.mSatToBitcoin(Long.parseLong(invoice.amt_paid_msat));
            }
            return BigDecimal.ZERO;
        });
    }

    @Override
    public List<? extends ILightningChannel> getChannels() {
        List<Channel> channels = callChecked(() -> api.getChannels().channels);
        if (channels == null) {
            return Collections.emptyList();
        }
        setAliases(channels);
        return channels;
    }

    private static Map<String, String> aliasesByPubKey = null;
    private static String localAlias = null;

    public void setAliases(List<Channel> channels) {
        if (aliasesByPubKey == null) {
            aliasesByPubKey = callChecked(() -> api.getGraph()).nodes.stream()
                .filter(o -> o.pub_key != null && o.alias != null)
                .collect(Collectors.toMap(o -> o.pub_key, o -> o.alias));
        }
        if (localAlias == null) {
            localAlias = callChecked(() -> api.getInfo().alias);
        }
        if (aliasesByPubKey != null) {
            for (Channel channel : channels) {
                channel.setLocalNodeAlias(localAlias);
                channel.setRemoteNodeAlias(aliasesByPubKey.get(channel.getRemoteNodeId()));
            }
        }
    }

    @Override
    public boolean canSend(String invoice, BigDecimal amount, String cryptoCurrency) {
        PaymentRequest paymentRequest = callChecked(cryptoCurrency, () -> api.decodePaymentRequest(invoice));
        if (paymentRequest == null) {
            return false;
        }

        List<RouteResponse.Route> routes = callChecked(cryptoCurrency, () -> api.getRoute(paymentRequest.destination, CoinUnit.bitcoinToSat(amount)).routes);
        if (routes == null || routes.isEmpty()) {
            return false;
        }

        List<String> route = routes.get(0).hops.stream().map(h -> h.pub_key).collect(Collectors.toList());
        log.debug("Route for {} {} to {}: {}", amount, cryptoCurrency, invoice, route);
        return route != null && !route.isEmpty();
    }

    @Override
    protected <T> T callChecked(ThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (ErrorResponseException e) {
            log.error("Error response: {} (code {})", e.error, e.code);
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody(), e);
        } catch (ConnectException e) {
            log.error("Cannot connect. URL: '{}'", url, e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

}

