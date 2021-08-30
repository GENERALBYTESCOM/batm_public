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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.lnurl.LnurlRestService;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.DemoLightningWallet;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.EclairWallet;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.LndWallet;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

public class LightningBitcoinExtension extends AbstractExtension {
    private static final Logger log = LoggerFactory.getLogger(LightningBitcoinExtension.class);

    private static final ICryptoCurrencyDefinition DEFINITION = new LightningBitcoinDefinition();
    public static final IRestService LNURL_REST_SERVICE = new LnurlRestService();

    private static IExtensionContext ctx = null;

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        LightningBitcoinExtension.ctx = ctx;
    }

    @Override
    public String getName() {
        return "BATM Bitcoin Lightning extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
            if (walletLogin != null && !walletLogin.trim().isEmpty()) {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();

                if ("eclair".equalsIgnoreCase(walletType)) {
                    String scheme = st.nextToken();
                    String host = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String password = st.nextToken();

                    InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(host, port));
                    host = tunnelAddress.getHostString();
                    port = tunnelAddress.getPort();

                    if (scheme != null && host != null && password != null) {
                        return new EclairWallet(scheme, host, port, password);
                    }
                } else if ("lnd".equalsIgnoreCase(walletType)) {
                    // echo https://127.0.0.1:8080/:`xxd -ps -u -c10000 ~/.lnd/data/chain/bitcoin/mainnet/admin.macaroon`:`xxd -ps -u -c10000 ~/.lnd/tls.cert`
                    // scheme://host:port/:macaroon:[cert]
                    // scheme://host/path/:macaroon:[cert]
                    // scheme://host:port/path:macaroon:[cert]

                    String url = st.nextToken() + ":" + st.nextToken();
                    if (!url.endsWith("/")) {
                        url += ":" + st.nextToken();
                    }
                    HttpUrl parsedUrl = HttpUrl.parse(url);
                    if (parsedUrl == null) {
                        log.error("Invalid URL configured: {}", url);
                        return null;
                    }
                    InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(parsedUrl.host(), parsedUrl.port()));
                    url = new HttpUrl.Builder().scheme(parsedUrl.scheme()).host(tunnelAddress.getHostString()).port(tunnelAddress.getPort()).encodedPath(parsedUrl.encodedPath()).build().toString();
                    String macaroon = st.nextToken();
                    String cert = st.hasMoreTokens() ? st.nextToken() : null;
                    if (macaroon == null || macaroon.trim().isEmpty()) {
                        log.error("macaroon param missing");
                        return null;
                    }
                    String feeLimit = st.hasMoreTokens() ? st.nextToken() : null;
                    return new LndWallet(url, macaroon, cert, feeLimit);
                } else if ("lbtcdemo".equalsIgnoreCase(walletType)) {
                    boolean simulateFailure = st.hasMoreTokens() && st.nextToken().equals("fail");
                    return new DemoLightningWallet(simulateFailure);
                }
            }
        } catch (Exception e) {
            log.warn("createWallet failed", e);
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return Collections.singleton(CryptoCurrency.LBTC.getCode());
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return Collections.singleton(DEFINITION);
    }

    @Override
    public Set<IRestService> getRestServices() {
        return Collections.singleton(LNURL_REST_SERVICE);
    }

    public static IExtensionContext getExtensionContext() {
        return ctx;
    }
}
