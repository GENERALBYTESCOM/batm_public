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

/**
 * This class holds credentials for the Coinbase API.
 * It can either hold the legacy API Key and Secret
 * or the CDP Private Key and Name.
 */
public class CoinbaseCredentials {

    private String legacyApiKey;
    private String legacyApiSecret;

    private String cdpPrivateKey;
    private String cdpKeyName;

    private CoinbaseCredentials() {
    }

    /**
     * Create a new instance of CoinbaseCredentials for legacy credentials.
     *
     * @param legacyApiKey    The legacy API Key.
     * @param legacyApiSecret The legacy secret.
     * @return The new instance of CoinbaseCredentials.
     * @throws IllegalArgumentException If either apiKey or apiSecret is null or empty
     */
    public static CoinbaseCredentials createLegacy(String legacyApiKey, String legacyApiSecret) {
        validateNotNullOrEmpty(legacyApiKey, "apiKey");
        validateNotNullOrEmpty(legacyApiSecret, "apiSecret");

        CoinbaseCredentials credentials = new CoinbaseCredentials();
        credentials.legacyApiKey = legacyApiKey;
        credentials.legacyApiSecret = legacyApiSecret;
        return credentials;
    }

    /**
     * Create a new instance of CoinbaseCredentials for CDP credentials.
     *
     * @param cdpPrivateKey The CDP Private Key.
     * @param cdpKeyName    The CDP Key Name.
     * @return The new instance of CoinbaseCredentials.
     * @throws IllegalArgumentException If either cdpPrivateKey or cdpKeyName is null or empty
     */
    public static CoinbaseCredentials createCdp(String cdpPrivateKey, String cdpKeyName) {
        validateNotNullOrEmpty(cdpPrivateKey, "cdpPrivateKey");
        validateNotNullOrEmpty(cdpKeyName, "cdpKeyName");

        CoinbaseCredentials credentials = new CoinbaseCredentials();
        credentials.cdpPrivateKey = cdpPrivateKey;
        credentials.cdpKeyName = cdpKeyName;
        return credentials;
    }

    private static void validateNotNullOrEmpty(String value, String name) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(name + " cannot be null or empty");
        }
    }

    public boolean isLegacy() {
        return legacyApiKey != null && legacyApiSecret != null;
    }

    public boolean isCdp() {
        return cdpPrivateKey != null && cdpKeyName != null;
    }

    public String getLegacyApiKey() {
        return legacyApiKey;
    }

    public String getLegacyApiSecret() {
        return legacyApiSecret;
    }

    public String getCdpPrivateKey() {
        return cdpPrivateKey;
    }

    public String getCdpKeyName() {
        return cdpKeyName;
    }

}
