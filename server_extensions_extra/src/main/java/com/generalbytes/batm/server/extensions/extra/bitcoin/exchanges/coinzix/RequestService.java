package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.requests.*;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.responses.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/v1")
@Consumes(MediaType.APPLICATION_JSON)
public interface RequestService {

    @GET
    @Path("public/book")
    OrderBookResponse getOrderBook(@QueryParam("pair") String pair) throws IOException, RuntimeException;

    @GET
    @Path("public/ticker")
    TickerResponse getTicker(@QueryParam("pair") String pair) throws IOException, RuntimeException;

    @POST
    @Path("private/balances")
    BalancesResponse getBalances(
            @HeaderParam("login-token") String token,
            @HeaderParam("x-auth-sign") String sign,
            BasicRequest request
    ) throws IOException, RuntimeException;

    @POST
    @Path("private/create-order")
    NewOrderResponse newOrder(
            @HeaderParam("login-token") String token,
            @HeaderParam("x-auth-sign") String sign,
            NewOrderRequest order) throws IOException, RuntimeException;

    @POST
    @Path("private/get-order")
    GetOrderResponse getOrder(
            @HeaderParam("login-token") String token,
            @HeaderParam("x-auth-sign") String sign,
            GetOrderRequest orderRequest) throws IOException, RuntimeException;

    @POST
    @Path("private/get-address")
    GetDepositAddressResponse getDepositAddress(
            @HeaderParam("login-token") String token,
            @HeaderParam("x-auth-sign") String sign,
            GetDepositAddressRequest getAddressRequest) throws IOException, RuntimeException;

    @POST
    @Path("withdraw")
    WithdrawResponse withdraw(
            @HeaderParam("login-token") String token,
            @HeaderParam("x-auth-sign") String sign,
            WithdrawRequest withdrawRequest
    ) throws IOException, RuntimeException;
}
