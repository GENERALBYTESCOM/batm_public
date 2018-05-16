package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit;

import com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit.dto.YobitResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/3/")
@Produces(MediaType.APPLICATION_JSON)
public interface IYobitAPI {
    @GET
    @Path("ticker/{cryptocurrency}_{fiatcurrency}")
    YobitResponse getTicker(@PathParam("cryptocurrency") String cryptoCurrency, @PathParam("fiatcurrency") String fiatCurrency);
}