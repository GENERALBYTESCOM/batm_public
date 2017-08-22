package com.generalbytes.batm.server.extensions.extra.potcoin.sources.coinmarketcap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoinmarketcapRateAPI {
    @GET
    @Path("/ticker/{cryptocurrency}/?convert={fiatcurrency}")
    CoinmarketcapResponse[] getRequest(@PathParam("cryptocurrency") String cryptoCurrency, @PathParam("fiatcurrency") String fiatCurrency);
}
