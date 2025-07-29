package com.generalbytes.batm.server.extensions.extra.stellar.source.bpventure;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Produces(MediaType.APPLICATION_JSON)
@Path("/v1")
public interface FXFeedAPI {
    @GET
    @Path("/exchange-rates/")
    FXFeedResponse getPrice(@QueryParam("currency") String currency) throws IOException;
}
