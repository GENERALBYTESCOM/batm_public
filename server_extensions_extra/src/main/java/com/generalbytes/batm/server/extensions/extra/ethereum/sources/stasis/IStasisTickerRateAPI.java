package com.generalbytes.batm.server.extensions.extra.ethereum.sources.stasis;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v3/")
@Produces(MediaType.APPLICATION_JSON)
public interface IStasisTickerRateAPI {
    @GET
    @Path("info")
    StasisTickerResponse getPrices();
}
