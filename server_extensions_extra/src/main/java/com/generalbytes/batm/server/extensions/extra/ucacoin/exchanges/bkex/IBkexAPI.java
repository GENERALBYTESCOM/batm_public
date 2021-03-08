package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.CreateOrder;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.CreatedOrder;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.Account;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.DepositAddressResponse;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.MarketTick;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.OrderBookResponse;
import com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto.OrderStateResponse;

import si.mazi.rescu.ParamsDigest;

@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IBkexAPI {

    @GET
    @Path("/q/ticker/price")
    @Produces({ "application/json" })
    MarketTick marketTickerForInstrument(@QueryParam("symbol") String symbol);

    @GET
    @Path("/q/depth")
    @Produces({ "application/json" })
    OrderBookResponse orderBook(@QueryParam("symbol") String instrumentCode, @QueryParam("limit") Integer level);

    @GET
    @Path("/u/account/balance")
    @Produces({ "application/json" })
    Account balances(@HeaderParam("X_SIGNATURE") ParamsDigest signer);

    @GET
    @Path("/u/order/openOrder/detail")
    @Produces({ "application/json" })
    OrderStateResponse getOrderState(@QueryParam("order_id") String orderId,
            @HeaderParam("X_SIGNATURE") ParamsDigest signer);

    @GET
    @Path("/u/order/create")
    @Produces({ "application/json" })
    CreatedOrder createOrder(@QueryParam("direction") String direction,
    @QueryParam("price") float price, 
    @QueryParam("source") String source,
    @QueryParam("symbol") String symbol,
    @QueryParam("type") String type,
    @QueryParam("volume") float volume,CreateOrder request, @HeaderParam("X_SIGNATURE") ParamsDigest signer);

    @GET
    @Path("/u/wallet/address")
    @Produces({ "application/json" })
    DepositAddressResponse getDepositAddresses(
        @QueryParam("currency") String currency,
        @HeaderParam("X_SIGNATURE") ParamsDigest signer);

}
