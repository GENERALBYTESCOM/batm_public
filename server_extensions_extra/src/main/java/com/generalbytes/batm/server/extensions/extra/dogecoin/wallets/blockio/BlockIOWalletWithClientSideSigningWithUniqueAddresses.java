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
package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseNewAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.util.Random;

public class BlockIOWalletWithClientSideSigningWithUniqueAddresses extends BlockIOWalletWithClientSideSigning implements IGeneratesNewDepositCryptoAddress {
    private static final Logger log = LoggerFactory.getLogger(BlockIOWalletWithClientSideSigningWithUniqueAddresses.class);

    public BlockIOWalletWithClientSideSigningWithUniqueAddresses(String apiKey, String pin, String priority, String fromLabel) {
        super(apiKey, pin, priority, fromLabel);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseNewAddress response = api.getNewAddress(formatAddressLabel(label));
            if (response != null && response.getData() != null && response.getData().getAddress() != null && !response.getData().getAddress().isEmpty()) {
                return response.getData().getAddress();
            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP error in getUniqueCryptoAddress: {}", e.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }


    private String formatAddressLabel(String label) {
        // label has to be unique and can only contain letters, numbers, _, -, @, ., and !.
        return label.replaceAll(" ", "-").replaceAll("[^-_@.!a-zA-Z0-9]", "") + "-" + new Random().nextInt();
    }
}
