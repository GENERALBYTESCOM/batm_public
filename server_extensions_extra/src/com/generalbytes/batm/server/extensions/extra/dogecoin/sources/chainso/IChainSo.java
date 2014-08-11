package com.generalbytes.batm.server.extensions.extra.dogecoin.sources.chainso;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by b00lean on 8/11/14.
 */
@Path("/api/v2/")
@Produces(MediaType.APPLICATION_JSON)
public interface IChainSo {
    @GET
    @Path("get_price/{crypto_currency}/{fiat_currency}")
    ChainSoResponse getPrices(@PathParam("crypto_currency") String crypto_currency, @PathParam("fiat_currency") String fiat_currency);

}
