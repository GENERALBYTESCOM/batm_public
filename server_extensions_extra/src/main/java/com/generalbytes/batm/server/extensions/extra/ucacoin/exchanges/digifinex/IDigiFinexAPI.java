package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.Account;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.MarketTick;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.OrderBookSnapshot;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto.Symbol;

import si.mazi.rescu.HttpResponseAware;
import si.mazi.rescu.HttpStatusExceptionSupport;
import si.mazi.rescu.ParamsDigest;

/**
 * https://developers.bitpanda.com/exchange/#bitpanda-pro-api-public
 */
@Path("/v3")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IDigiFinexAPI {

    @POST
    @Path("/ticker")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    MarketTick marketTickerForInstrument(Symbol instrumentCode) throws ApiError;

    @GET
    @Path("/order_book")
    @Produces({ "application/json" })
    OrderBookSnapshot orderBook(
        @QueryParam("symbol") String instrumentCode,
        @QueryParam("limit") Integer level
    ) throws ApiError;

    @GET
    @Path("/spot/assets")
    @Produces({ "application/json" })
    Account balances(@HeaderParam("ACCESS-SIGN") ParamsDigest signer) throws ApiError;
/*
    @GET
    @Path("/account/orders/{order_id}")
    @Produces({ "application/json" })
    OrderState getOrder(@PathParam("order_id") UUID orderId) throws ApiError;

    @POST
    @Path("/account/orders")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Order createOrder(CreateOrder createOrder) throws ApiError;

    @GET
    @Path("/account/deposit/crypto/{currency_code}")
    @Produces({ "application/json" })
    DepositAddress cryptoDepositAddress(@PathParam("currency_code") String currencyCode) throws ApiError;

    @POST
    @Path("/account/deposit/crypto")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    DepositAddress createCryptoDepositAddress(CreateDepositAddress payload) throws ApiError;

    @POST
    @Path("/account/withdraw/crypto")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    CryptoWithdraw withdrawCrypto(WithdrawCrypto withdrawCrypto) throws ApiError;
*/
    final class ApiError extends HttpStatusExceptionSupport implements HttpResponseAware {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @JsonProperty("code")
        private String code;

        private Map<String, List<String>> headers;

        @Override public String getMessage() {
            return String.format(Locale.ENGLISH, "api request failed (errorCode=%s,http_status=%s)",
                this.code, this.getHttpStatusCode()
            );
        }

        @Override public void setResponseHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
        }

        @Override public Map<String, List<String>> getResponseHeaders() {
            return headers;
        }
    }

    
}
