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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair;

import com.generalbytes.batm.server.extensions.ILightningChannel;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.AbstractLightningWallet;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Channel;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ReceivedInfo;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.SentInfo;
import com.generalbytes.batm.server.extensions.ThrowingSupplier;
import com.generalbytes.batm.server.coinutil.CoinUnit;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.ClientConfigUtil;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EclairWallet extends AbstractLightningWallet {

    private static final Logger log = LoggerFactory.getLogger(EclairWallet.class);
    private static final int API_POLL_MAX = 20000;
    private static final int API_POLL_INTERVAL = 1000;
    private static final long API_CALL_DELAY = 100;

    private final String url;
    private final EclairAPI api;

    public EclairWallet(String scheme, String host, int port, String password) {
        url = new HttpUrl.Builder().scheme(scheme).host(host).port(port).build().toString();
        final ClientConfig config = new ClientConfig();
        ClientConfigUtil.addBasicAuthCredentials(config, "", password);
        api = RestProxyFactory.createProxy(EclairAPI.class, url, config);
    }

    /**
     * @return paymentHash
     */
    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {

        Invoice invoice = callChecked(cryptoCurrency, () -> api.parseInvoice(destinationAddress));

        log.info("Paying {} to invoice {}", amount, invoice);

        if (invoice.amount != null) {
            log.info("Invoices with amount not supported");
            return null;
        }

        List<SentInfo> sentInfo = callChecked(() -> api.getSentInfoByPaymentHash(invoice.paymentHash));

        if (sentInfo.stream().anyMatch(i -> i.status.type == SentInfo.Status.Type.sent)) {
            log.info("Invoice already paid");
            return null;
        }
        Long amountMsat = CoinUnit.bitcoinToMSat(amount);
        String paymentId = callChecked(() -> api.payInvoice(destinationAddress, amountMsat));

        for (int millis = 0; millis < API_POLL_MAX; millis += API_POLL_INTERVAL) {
            sleep(API_POLL_INTERVAL);

            SentInfo sentInfo2 = callChecked(() -> api.getSentInfoById(paymentId).get(0));

            switch (sentInfo2.status.type) {
                case failed:
                    log.error("Payment failed: {}", sentInfo2);
                    return null;
                case sent:
                    log.error("Payment sent: {}", sentInfo2);
                    return sentInfo2.status.paymentPreimage;
                case pending:
                    log.error("Payment pending: {}", sentInfo2);
                    continue;
                default:
                    throw new IllegalArgumentException("Unsupported SentInfo.Status.Type");
            }
        }
        log.error("Payment result unknown; paymentId={}", paymentId);
        return null;
    }

    @Override
    public String getInvoice(BigDecimal cryptoAmount, String cryptoCurrency, Long paymentValidityInSec, String description) {
        return callChecked(cryptoCurrency, () -> api.createInvoice(CoinUnit.bitcoinToMSat(cryptoAmount), description, paymentValidityInSec).serialized);
    }

    private Map<String, String> channelAliases = null;

    @Override
    public List<? extends ILightningChannel> getChannels() {
        if (channelAliases == null) {
            channelAliases = callChecked(() -> api.getAllNodes().stream().collect(Collectors.toMap(o -> o.nodeId, o -> o.alias)));
        }

        List<Channel> channels = callChecked(api::getChannels);
        if (channels == null) {
            return Collections.emptyList();
        }
        for (Channel channel : channels) {
            channel.setLocalNodeAlias(channelAliases.get(channel.getLocalNodeId()));
            channel.setRemoteNodeAlias(channelAliases.get(channel.getRemoteNodeId()));
        }
        return channels;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> {
            List<? extends ILightningChannel> channels = getChannels();

            channels.stream().map(ch -> ch.getShortChannelId() + " " + ch.isOnline()
                + " Can send msat: " + ch.getBalanceMsat()
                + " Can receive msat: " + (ch.getCapacityMsat() - ch.getBalanceMsat())
                + " Capacity sat: " + ch.getCapacityMsat()
            ).forEach(log::info);

            return CoinUnit.mSatToBitcoin(channels.stream()
                .mapToLong(ILightningChannel::getBalanceMsat)
                .max().orElse(0l));
        });
    }

    /**
     * @return paymentHash
     */
    public BigDecimal getReceivedAmount(String destinationAddress, String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> {
            try {
                return getReceivedAmount(api.getReceivedInfoByInvoice(destinationAddress));
            } catch (ErrorResponseException e) {
                if (e.error.equals("Not found")) {
                    return BigDecimal.ZERO;
                }
                throw e;
            }
        });
    }

    private BigDecimal getReceivedAmount(ReceivedInfo receivedInfo) {
        switch (receivedInfo.status.type) {
            case received:
                return CoinUnit.mSatToBitcoin(receivedInfo.status.amount);
            case pending:
            case expired:
                return BigDecimal.ZERO;
            default:
                throw new IllegalArgumentException("Unsupported ReceivedInfo.Status.Type");
        }
    }

    @Override
    public String getPubKey() {
        return callChecked(() -> api.getInfo().nodeId);
    }

    protected <T> T callChecked(ThrowingSupplier<T> supplier) {
        return callChecked(supplier, 3);
    }

    private <T> T callChecked(ThrowingSupplier<T> supplier, int retries) {
        try {
            sleep(API_CALL_DELAY);
            return supplier.get();
        } catch (ErrorResponseException e) {
            log.error("Error response: {}", e.error);

            // workaround https://github.com/ACINQ/eclair/pull/1032
            if (e.error.endsWith("was malformed:\nSubstream Source cannot be materialized more than once") && retries > 0) {
                log.info("retrying {}", retries);
                return callChecked(supplier, retries - 1);
            }

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