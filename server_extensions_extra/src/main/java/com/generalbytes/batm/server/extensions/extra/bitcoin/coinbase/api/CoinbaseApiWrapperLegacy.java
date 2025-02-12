package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.CoinbaseDigest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.ICoinbaseAPI;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBAccountsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBNewAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBOrderResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPaymentMethodsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBPriceResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBSendCoinsResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto.CBTimeResponse;

import java.io.IOException;

public class CoinbaseApiWrapperLegacy implements CoinbaseApiWrapper {

    private final ICoinbaseAPI api;
    private final String apiKey;
    private final String secretKey;

    public CoinbaseApiWrapperLegacy(ICoinbaseAPI api, String apiKey, String secretKey) {
        this.api = api;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    @Override
    public CBPriceResponse getPrice(String apiVersion, String currencyPair, String priceType) throws IOException, RuntimeException {
        return api.getPrice(apiVersion, currencyPair, priceType);
    }

    @Override
    public CBNewAddressResponse getNewAddress(String apiVersion,
                                              String coinbaseTime,
                                              String accountId) throws IOException, RuntimeException {
        CoinbaseDigest authorizationDigest = CoinbaseDigest.createInstance(secretKey);
        return api.getNewAddress(apiVersion, apiKey, authorizationDigest, coinbaseTime, accountId);
    }

    @Override
    public CBPaymentMethodsResponse listPaymentMethods(String apiVersion, String coinbaseTime) throws IOException, RuntimeException {
        CoinbaseDigest authorizationDigest = CoinbaseDigest.createInstance(secretKey);
        return api.listPaymentMethods(apiVersion, apiKey, authorizationDigest, coinbaseTime);
    }

    @Override
    public CBSendCoinsResponse sendCoins(String apiVersion,
                                         String coinbaseTime,
                                         String accountId,
                                         CBSendCoinsRequest request) throws IOException, RuntimeException {
        CoinbaseDigest authorizationDigest = CoinbaseDigest.createInstance(secretKey);
        return api.sendCoins(apiVersion, apiKey, authorizationDigest, coinbaseTime, accountId, request);
    }

    @Override
    public CBOrderResponse getBuyOrder(String apiVersion,
                                       String coinbaseTime,
                                       String accountId,
                                       String orderId) throws IOException, RuntimeException {
        CoinbaseDigest authorizationDigest = CoinbaseDigest.createInstance(secretKey);
        return api.getBuyOrder(apiVersion, apiKey, authorizationDigest, coinbaseTime, accountId, orderId);
    }

    @Override
    public CBOrderResponse getSellOrder(String apiVersion,
                                        String coinbaseTime,
                                        String accountId,
                                        String orderId) throws IOException, RuntimeException {
        CoinbaseDigest authorizationDigest = CoinbaseDigest.createInstance(secretKey);
        return api.getSellOrder(apiVersion, apiKey, authorizationDigest, coinbaseTime, accountId, orderId);
    }

    @Override
    public CBOrderResponse buyCoins(String apiVersion,
                                    String coinbaseTime,
                                    String accountId,
                                    CBOrderRequest request) throws IOException, RuntimeException {
        CoinbaseDigest authorizationDigest = CoinbaseDigest.createInstance(secretKey);
        return api.buyCoins(apiVersion, apiKey, authorizationDigest, coinbaseTime, accountId, request);
    }

    @Override
    public CBOrderResponse sellCoins(String apiVersion,
                                     String coinbaseTime,
                                     String accountId,
                                     CBOrderRequest request) throws IOException, RuntimeException {
        CoinbaseDigest authorizationDigest = CoinbaseDigest.createInstance(secretKey);
        return api.sellCoins(apiVersion, apiKey, authorizationDigest, coinbaseTime, accountId, request);
    }

    @Override
    public CBAccountsResponse getAccounts(String apiVersion,
                                          String coinbaseTime,
                                          String startingAfterAccountId) throws IOException, RuntimeException {
        CoinbaseDigest authorizationDigest = CoinbaseDigest.createInstance(secretKey);
        return api.getAccounts(apiVersion, apiKey, authorizationDigest, coinbaseTime, startingAfterAccountId);
    }

    @Override
    public CBTimeResponse getTime(String apiVersion) throws IOException, RuntimeException {
        return api.getTime(apiVersion);
    }

}
