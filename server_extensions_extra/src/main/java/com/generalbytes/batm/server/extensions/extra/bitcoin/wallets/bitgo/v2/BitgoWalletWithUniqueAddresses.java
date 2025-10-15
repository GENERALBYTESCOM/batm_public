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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoTransfer;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoTransfersResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import lombok.extern.slf4j.Slf4j;
import si.mazi.rescu.HttpStatusIOException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class BitgoWalletWithUniqueAddresses extends BitgoWallet implements IGeneratesNewDepositCryptoAddress, IQueryableWallet {

    private static final String TRANSFER_STATE_CONFIRMED = "confirmed";
    private static final String TRANSFER_TYPE_RECEIVE = "receive";
    private static final Map<String, String> newAddressCryptoCurrency = Map.ofEntries(
        Map.entry(CryptoCurrency.USDT.getCode(), "eth"),
        Map.entry(CryptoCurrency.USDTTRON.getCode(), "trx"),
        Map.entry(CryptoCurrency.USDC.getCode(), "eth"),
        Map.entry(CryptoCurrency.USDCSOL.getCode(), "sol")
    );

    public BitgoWalletWithUniqueAddresses(String scheme,
                                          String host,
                                          int port,
                                          String token,
                                          String walletId,
                                          String walletPassphrase,
                                          Integer numBlocks,
                                          Integer feeRate,
                                          Integer maxFeeRate
    ) {
        super(scheme, host, port, token, walletId, walletPassphrase, numBlocks, feeRate, maxFeeRate);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        String bitgoCryptoCurrency = newAddressCryptoCurrency.getOrDefault(cryptoCurrency, cryptoCurrencies.get(cryptoCurrency));
        if (bitgoCryptoCurrency == null) {
            return null;
        }
        try {

            final BitGoCreateAddressRequest request = new BitGoCreateAddressRequest();
            request.setChain(0); // https://github.com/BitGo/unspents/blob/master/src/codes.ts ??? [0, UnspentType.p2sh, Purpose.external],
            request.setLabel(label);
            final BitGoAddressResponse response = api.createAddress(bitgoCryptoCurrency, walletId, request);
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
        String bitgoCryptoCurrency = cryptoCurrencies.get(cryptoCurrency);
        if (bitgoCryptoCurrency == null) {
            log.error("Wallet supports only {}, not {}", getCryptoCurrencies(), cryptoCurrency);
            return ReceivedAmount.ZERO;
        }

        try {
            BitGoTransfersResponse transfersResponse = api.getTransfers(
                bitgoCryptoCurrency,
                walletId,
                TRANSFER_STATE_CONFIRMED,
                TRANSFER_TYPE_RECEIVE,
                address
            );
            BigDecimal totalInBaseUnits = BigDecimal.ZERO;
            int confirmations = 0;
            List<String> transactionHashes = new ArrayList<>();

            for (BitGoTransfer confirmedTransfer : transfersResponse.getTransfers()) {
                BigDecimal valueInBaseUnits = fromSatoshis(cryptoCurrency, confirmedTransfer.getValue());

                totalInBaseUnits = totalInBaseUnits.add(valueInBaseUnits);
                if (confirmations == 0 || confirmedTransfer.getConfirmations() < confirmations) {
                    confirmations = confirmedTransfer.getConfirmations();
                }
                transactionHashes.add(confirmedTransfer.getTransactionHash());
            }

            ReceivedAmount receivedAmount = new ReceivedAmount(totalInBaseUnits, confirmations);
            receivedAmount.setTransactionHashes(transactionHashes);
            return receivedAmount;
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
