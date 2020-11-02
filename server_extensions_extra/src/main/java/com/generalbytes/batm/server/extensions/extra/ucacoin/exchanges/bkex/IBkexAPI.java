package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex;

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
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.CreateOrder;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.DepositAddress;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.Account;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.DepositAddressResponse;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.MarketTick;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.OrderBookResponse;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.OrderBookSnapshot;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.OrderStates;

import si.mazi.rescu.HttpResponseAware;
import si.mazi.rescu.HttpStatusExceptionSupport;
import si.mazi.rescu.ParamsDigest;

@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IBkexAPI {

    @GET
    @Path("/q/tickers")
    @Consumes({ "application/json" })
    MarketTick marketTickerForInstrument(String symbol) throws ApiError;

    @GET
    @Path("/contract/q/depth")
    @Produces({ "application/json" })
    OrderBookResponse orderBook(@QueryParam("symbol") String instrumentCode, @QueryParam("limit") Integer level)
            throws ApiError;

    @GET
    @Path("/u/account/balance")
    @Produces({ "application/json" })
    Account balances(@HeaderParam("X_SIGNATURE") ParamsDigest signer) throws ApiError;

    @GET
    @Path("/u/order/openOrders")
    @Produces({ "application/json" })
    OrderStates getOrderStates(@QueryParam("order_id") String[] orderIds,
            @HeaderParam("X_SIGNATURE") ParamsDigest signer) throws ApiError;

    @POST
    @Path("/u/order/create")
    @Consumes({ "application/json" })
    Object createOrder(CreateOrder request, @HeaderParam("X_SIGNATURE") ParamsDigest signer) throws ApiError;

    @GET
    @Path("/u/wallet/address")
    @Produces({ "application/json" })
    DepositAddressResponse getDepositAddresses(
        @QueryParam("currency") String currency,
        @HeaderParam("X_SIGNATURE") ParamsDigest signer) throws ApiError;
/*
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
                this, this.getHttpStatusCode()
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
