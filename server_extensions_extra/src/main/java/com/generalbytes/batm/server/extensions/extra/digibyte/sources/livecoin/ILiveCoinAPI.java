package com.generalbytes.batm.server.extensions.extra.digibyte.sources.livecoin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/exchange")
@Produces(MediaType.APPLICATION_JSON)
public interface ILiveCoinAPI {

  @GET
  @Path("/ticker?currencyPair={currencyPair}")
  LiveCoinTicker getTicker(@PathParam("currencyPair") String currencyPair);
}
