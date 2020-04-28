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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class CoinbaseWalletV2WithUniqueAddresses extends CoinbaseWalletV2 implements IGeneratesNewDepositCryptoAddress {
    private static final Logger log = LoggerFactory.getLogger(CoinbaseWalletV2WithUniqueAddresses.class);

    public CoinbaseWalletV2WithUniqueAddresses(String apiKey, String apiSecret, String accountName) {
        super(apiKey, apiSecret, accountName);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only " + Arrays.toString(getCryptoCurrencies().toArray()) + " not " + cryptoCurrency);
            return null;
        }
        initIfNeeded(cryptoCurrency);
        long timeStamp = getTimestamp();
        CBCreateAddressResponse addressesResponse = api.createAddress(apiKey, API_VERSION, CBDigest.createInstance(apiSecret, timeStamp), timeStamp, accountIds.get(cryptoCurrency), new CBCreateAddressRequest(label));
        if (addressesResponse != null && addressesResponse.getData() != null) {
            CBAddress address = addressesResponse.getData();
            String network = getNetworkName(cryptoCurrency);
            if (network == null || !address.getNetwork().equalsIgnoreCase(network)) {
                log.warn("network does not match");
                return null;
            }
            return address.getAddress();
        }
        if (addressesResponse != null && addressesResponse.getErrors() != null) {
            log.error("generateNewDepositCryptoAddress - " + addressesResponse.getErrorMessages());
        }
        return null;
    }
}
