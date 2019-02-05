package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain;

import java.io.IOException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.DVChainException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.marketdata.DVChainMarketResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainNewLimitOrder;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainNewMarketOrder;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainTrade;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain.dto.trade.DVChainTradesResponse;

@Path("api/v4")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DVChain {

  @GET
  @Path("trades")
  DVChainTradesResponse getTrades(@HeaderParam("Authorization") String apiKey,
                                  @HeaderParam("Pragma") String nocache,
                                  @HeaderParam("Cache-Control") String cache)
      throws DVChainException, IOException;

  @POST
  @Path("trade")
  DVChainTrade placeLimitOrder(
      DVChainNewLimitOrder newOrder, @HeaderParam("Authorization") String apiKey)
      throws DVChainException, IOException;

  @POST
  @Path("trade")
  DVChainTrade placeMarketOrder(
      DVChainNewMarketOrder newOrder, @HeaderParam("Authorization") String apiKey)
      throws DVChainException, IOException;

  @GET
  @Path("prices")
  DVChainMarketResponse getPrices(@HeaderParam("Authorization") String apiKey)
      throws DVChainException, IOException;

  @DELETE
  @Path("trades/{tradeId}")
  String cancelOrder(
      @PathParam("tradeId") String tradeId, @HeaderParam("Authorization") String apiKey)
      throws DVChainException, IOException;
}
