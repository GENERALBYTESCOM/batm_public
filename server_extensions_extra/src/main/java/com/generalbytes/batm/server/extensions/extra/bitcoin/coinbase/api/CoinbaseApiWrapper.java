package com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api;

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

/**
 * Represents a general Coinbase API wrapper, that can be implemented
 * for any authorization or API access method.
 */
public interface CoinbaseApiWrapper {

    CBPriceResponse getPrice(String apiVersion, String currencyPair, String priceType) throws IOException, RuntimeException;

    CBNewAddressResponse getNewAddress(String apiVersion, String coinbaseTime, String accountId) throws IOException, RuntimeException;

    CBPaymentMethodsResponse listPaymentMethods(String apiVersion, String coinbaseTime) throws IOException, RuntimeException;

    CBSendCoinsResponse sendCoins(String apiVersion, String coinbaseTime, String accountId, CBSendCoinsRequest request) throws IOException, RuntimeException;

    CBOrderResponse getBuyOrder(String apiVersion, String coinbaseTime, String accountId, String orderId) throws IOException, RuntimeException;

    CBOrderResponse getSellOrder(String apiVersion, String coinbaseTime, String accountId, String orderId) throws IOException, RuntimeException;

    CBOrderResponse buyCoins(String apiVersion, String coinbaseTime, String accountId, CBOrderRequest request) throws IOException, RuntimeException;

    CBOrderResponse sellCoins(String apiVersion, String coinbaseTime, String accountId, CBOrderRequest request) throws IOException, RuntimeException;

    CBAccountsResponse getAccounts(String apiVersion, String coinbaseTime, String startingAfterAccountId) throws IOException, RuntimeException;

    CBTimeResponse getTime(String apiVersion) throws IOException, RuntimeException;

}
