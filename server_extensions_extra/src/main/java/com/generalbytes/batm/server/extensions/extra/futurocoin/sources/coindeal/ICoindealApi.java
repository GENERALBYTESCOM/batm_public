package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.coindeal;

import com.generalbytes.batm.server.extensions.extra.futurocoin.sources.coindeal.dto.CoindealResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/markets/market/info/")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoindealApi {
    @GET
    @Path("?pair={cryptocurrency}/{fiatcurrency}")
    CoindealResponse getRates(@PathParam("cryptocurrency") String cryptoCurrency, @PathParam("fiatcurrency") String fiatCurrency);
}
