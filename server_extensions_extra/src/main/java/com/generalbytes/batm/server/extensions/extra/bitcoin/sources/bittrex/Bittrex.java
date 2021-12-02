package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bittrex;

import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.bittrex.dto.BittrexTickerDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/v3")
public interface Bittrex {

    @GET
    @Path("/markets/{marketSymbol}/ticker")
    BittrexTickerDto getTicker(@PathParam("marketSymbol") String marketSymbol);

}
