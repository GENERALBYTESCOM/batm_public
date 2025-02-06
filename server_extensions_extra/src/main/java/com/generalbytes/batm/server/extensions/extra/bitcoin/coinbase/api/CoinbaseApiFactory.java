/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.CoinbaseException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.ICoinbaseAPI;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.ICoinbaseV2API;
import com.generalbytes.batm.server.extensions.util.net.CompatSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Factory for creating Coinbase API proxies.
 */
public class CoinbaseApiFactory {

    private static final Logger log = LoggerFactory.getLogger(CoinbaseApiFactory.class);

    /**
     * Creates a proxy instance for interacting with the Coinbase API.
     *
     * @return A configured proxy instance of {@link ICoinbaseAPI}.
     * @throws CoinbaseException If the proxy creation fails.
     */
    public static ICoinbaseAPI createCoinbaseApiLegacy() {
        try {
            ClientConfig config = new ClientConfig();
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);
            CompatSSLSocketFactory socketFactory = new CompatSSLSocketFactory(sslcontext.getSocketFactory());
            config.setSslSocketFactory(socketFactory);
            config.setIgnoreHttpErrorCodes(true);
            return RestProxyFactory.createProxy(ICoinbaseAPI.class, "https://api.coinbase.com", config);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("Failed to create ICoinbaseAPILegacy proxy.", e);
            throw new CoinbaseException("Unable to create ICoinbaseAPILegacy proxy");
        }
    }

    /**
     * Creates a proxy instance for interacting with the Coinbase API.
     *
     * @return A configured proxy instance of {@link ICoinbaseV2API}.
     * @throws CoinbaseException If the proxy creation fails.
     */
    public static ICoinbaseV2API createCoinbaseV2ApiLegacy() {
        ClientConfig config = new ClientConfig();
        config.setIgnoreHttpErrorCodes(true);
        return RestProxyFactory.createProxy(ICoinbaseV2API.class, "https://api.coinbase.com", config);
    }

}
