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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.math.BigDecimal;

public class BitgoWalletWithUniqueAddresses extends BitgoWallet implements IGeneratesNewDepositCryptoAddress, IQueryableWallet {
    private static final Logger log = LoggerFactory.getLogger(BitgoWalletWithUniqueAddresses.class);

    public BitgoWalletWithUniqueAddresses(String scheme, String host, int port, String token, String walletId, String walletPassphrase, Integer numBlocks) {
        super(scheme, host, port, token, walletId, walletPassphrase, numBlocks);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();
        try {

            final BitGoCreateAddressRequest request = new BitGoCreateAddressRequest();
            request.setChain(0); // https://github.com/BitGo/unspents/blob/master/src/codes.ts ??? [0, UnspentType.p2sh, Purpose.external],
            request.setLabel(label);
            final BitGoAddressResponse response = api.createAddress(cryptoCurrency, walletId, request);
            if (response == null) {
                return null;
            }
            String address = response.getAddress();
            if (address == null || address.isEmpty()) {
                log.error("address missing in response: '{}'", address);
                return null;
            }
            return address;
        } catch (HttpStatusIOException hse) {
            log.debug("create address error: {}", hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("create address error, HTTP status: {}, error: {}", e.getHttpStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("create address error", e);
        }
        return null;
    }

    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Wallet supports only {}, not {}", getCryptoCurrencies(), cryptoCurrency);
            return ReceivedAmount.ZERO;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();

        try {
            BitGoAddressResponse resp = api.getAddress(cryptoCurrency, walletId, address);
            if (resp.getBalance().getConfirmedBalance().compareTo(BigDecimal.ZERO) > 0) {
                return new ReceivedAmount(divideBalance(cryptoCurrency, resp.getBalance().getConfirmedBalance()), 999);
            }
            return new ReceivedAmount(divideBalance(cryptoCurrency, resp.getBalance().getBalance()), 0);
        } catch (HttpStatusIOException e) {
            log.debug("get address error: {}", e.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("get address error, HTTP status: {}, error: {}", e.getHttpStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("get address error", e);
        }
        return ReceivedAmount.ZERO;
    }
}
