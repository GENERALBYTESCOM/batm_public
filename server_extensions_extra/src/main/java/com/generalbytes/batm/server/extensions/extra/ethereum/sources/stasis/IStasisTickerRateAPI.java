package com.generalbytes.batm.server.extensions.extra.ethereum.sources.stasis;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v3/")
@Produces(MediaType.APPLICATION_JSON)
public interface IStasisTickerRateAPI {
    @GET
    @Path("info")
    StasisTickerResponse getPrices();
}
