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

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseCreateOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseOrderSide;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbasePriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseServerTimeResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.dto.CoinbaseTransactionResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBNewAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBTimeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CoinbaseApiWrapperCdp implements CoinbaseApiWrapper {

    private static final Logger log = LoggerFactory.getLogger(CoinbaseApiWrapperCdp.class);
    private final ICoinbaseV3Api api;
    private final String privateKey;
    private final String keyName;

    public CoinbaseApiWrapperCdp(ICoinbaseV3Api api, String privateKey, String keyName) {
        this.api = api;
        this.privateKey = privateKey;
        this.keyName = keyName;
    }

    @Override
    public CBPriceResponse getPrice(String apiVersion, String currencyPair, String priceType) throws IOException, RuntimeException {
        try {
            CoinbasePriceResponse response = api.getPrice(currencyPair, priceType);
            return CoinbaseApiMapper.mapPriceResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            log.error("Failed to get price for currency pair {} from Coinbase: {}", currencyPair, e.getMessage());
            return CoinbaseApiMapper.mapExceptionToLegacyResponse(e, new CBPriceResponse());
        }
    }

    @Override
    public CBNewAddressResponse getNewAddress(String apiVersion,
                                              String coinbaseTime,
                                              String accountId) throws IOException, RuntimeException {
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbaseCreateAddressResponse response = api.createAddress(authorizationDigest, accountId, null);
            return CoinbaseApiMapper.mapCreateAddressResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            log.error("Failed to create a new address at Coinbase: {}", e.getMessage());
            return CoinbaseApiMapper.mapExceptionToLegacyResponse(e, new CBNewAddressResponse());
        }
    }

    @Override
    public CBPaymentMethodsResponse listPaymentMethods(String apiVersion, String coinbaseTime) throws IOException, RuntimeException {
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbasePaymentMethodsResponse response = api.getPaymentMethods(authorizationDigest);
            return CoinbaseApiMapper.mapPaymentMethodsResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            log.error("Failed to get payment methods from Coinbase: {}", e.getMessage());
            return CoinbaseApiMapper.mapExceptionToLegacyResponse(e, new CBPaymentMethodsResponse());
        }
    }

    @Override
    public CBSendCoinsResponse sendCoins(String apiVersion,
                                         String coinbaseTime,
                                         String accountId,
                                         CBSendCoinsRequest legacyRequest) throws IOException, RuntimeException {
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        CoinbaseSendCoinsRequest request = CoinbaseApiMapper.mapLegacySendCoinsRequestToRequest(legacyRequest);
        try {
            CoinbaseTransactionResponse response = api.sendCoins(authorizationDigest, accountId, request);
            return CoinbaseApiMapper.mapSendCoinsResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            log.error("Failed to send coins via Coinbase: {}", e.getMessage());
            return CoinbaseApiMapper.mapExceptionToLegacyResponse(e, new CBSendCoinsResponse());
        }
    }

    @Override
    public CBOrderResponse getBuyOrder(String apiVersion,
                                       String coinbaseTime,
                                       String accountId,
                                       String orderId) throws IOException, RuntimeException {
        return getOrder(orderId);
    }

    @Override
    public CBOrderResponse getSellOrder(String apiVersion,
                                        String coinbaseTime,
                                        String accountId,
                                        String orderId) throws IOException, RuntimeException {
        return getOrder(orderId);
    }

    private CBOrderResponse getOrder(String orderId) {
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbaseOrderResponse response = api.getOrder(authorizationDigest, orderId);
            return CoinbaseApiMapper.mapOrderResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            log.error("Failed to get order from Coinbase: {}", e.getMessage());
            return CoinbaseApiMapper.mapExceptionToLegacyResponse(e, new CBOrderResponse());
        }
    }

    @Override
    public CBOrderResponse buyCoins(String apiVersion,
                                    String coinbaseTime,
                                    String accountId,
                                    CBOrderRequest legacyRequest) throws IOException, RuntimeException {
        return createOrder(legacyRequest, CoinbaseOrderSide.BUY);
    }

    @Override
    public CBOrderResponse sellCoins(String apiVersion,
                                     String coinbaseTime,
                                     String accountId,
                                     CBOrderRequest legacyRequest) throws IOException, RuntimeException {
        return createOrder(legacyRequest, CoinbaseOrderSide.SELL);
    }

    private CBOrderResponse createOrder(CBOrderRequest legacyRequest, CoinbaseOrderSide side) {
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        CoinbaseCreateOrderRequest request = CoinbaseApiMapper.mapLegacyCreateOrderRequestToRequest(legacyRequest, side);
        try {
            CoinbaseCreateOrderResponse response = api.createOrder(authorizationDigest, request);
            if (response == null) {
                log.error("Failed to create a {} order at Coinbase. Response is null.", side.name());
            } else if (!response.isSuccess()) {
                log.error("Failed to create a {} order at Coinbase. Error response: {}", side.name(), response.getErrorResponse());
            }
            return CoinbaseApiMapper.mapCreateOrderResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            log.error("Failed to create a {} order at Coinbase: {}", side.name(), e.getMessage());
            return CoinbaseApiMapper.mapExceptionToLegacyResponse(e, new CBOrderResponse());
        }
    }

    @Override
    public CBAccountsResponse getAccounts(String apiVersion,
                                          String coinbaseTime,
                                          String startingAfterAccountId) throws IOException, RuntimeException {
        CoinbaseCdpDigest authorizationDigest = new CoinbaseCdpDigest(privateKey, keyName);
        try {
            CoinbaseAccountsResponse response = api.getAccounts(authorizationDigest, null, startingAfterAccountId);
            return CoinbaseApiMapper.mapAccountsResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            log.error("Failed to get accounts from Coinbase: {}", e.getMessage());
            return CoinbaseApiMapper.mapExceptionToLegacyResponse(e, new CBAccountsResponse());
        }
    }

    @Override
    public CBTimeResponse getTime(String apiVersion) throws IOException, RuntimeException {
        try {
            CoinbaseServerTimeResponse response = api.getServerTime();
            return CoinbaseApiMapper.mapServerTimeResponseToLegacyResponse(response);
        } catch (CoinbaseApiException e) {
            log.error("Failed to get server time from Coinbase: {}", e.getMessage());
            return CoinbaseApiMapper.mapExceptionToLegacyResponse(e, new CBTimeResponse());
        }
    }

}
