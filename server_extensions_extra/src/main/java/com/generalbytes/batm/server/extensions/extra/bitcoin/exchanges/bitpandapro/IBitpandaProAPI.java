package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.Account;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.CreateDepositAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.CreateOrder;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.CryptoWithdraw;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.DepositAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.MarketTick;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.Order;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.OrderBookSnapshot;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.OrderState;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto.WithdrawCrypto;

import si.mazi.rescu.HttpResponseAware;
import si.mazi.rescu.HttpStatusExceptionSupport;

/**
 * https://developers.bitpanda.com/exchange/#bitpanda-pro-api-public
 */
@Path("/public/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IBitpandaProAPI {

    @GET
    @Path("/market-ticker/{instrument_code}")
    @Produces({ "application/json" })
    MarketTick marketTickerForInstrument(@PathParam("instrument_code") String instrumentCode) throws ApiError;

    @GET
    @Path("/order-book/{instrument_code}")
    @Produces({ "application/json" })
    OrderBookSnapshot orderBook(
        @PathParam("instrument_code") String instrumentCode,
        @QueryParam("level") Integer level,
        @QueryParam("depth") Integer depth
    ) throws ApiError;

    @GET
    @Path("/account/balances")
    @Produces({ "application/json" })
    Account balances() throws ApiError;

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

    final class ApiError extends HttpStatusExceptionSupport implements HttpResponseAware {

        @JsonProperty("error")
        private String error;

        private Map<String, List<String>> headers;

        @Override public String getMessage() {
            return String.format(Locale.ENGLISH, "api request failed (error=%s,http_status=%s,bp-request-id=%s)",
                this.error, this.getHttpStatusCode(), this.bpRequestId()
            );
        }

        public String bpRequestId() {
            if (headers == null) {
                return null;
            } // not yet injected

            final List<String> values = headers.get("bp-request-id");
            if (values != null && !values.isEmpty()) {
                return values.get(0);
            } // found the header

            return null;
        }

        @Override public void setResponseHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
        }

        @Override public Map<String, List<String>> getResponseHeaders() {
            return headers;
        }
    }
}
