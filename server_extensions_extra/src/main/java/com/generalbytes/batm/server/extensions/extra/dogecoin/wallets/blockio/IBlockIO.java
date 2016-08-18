package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v2/")
@Produces(MediaType.APPLICATION_JSON)
public interface IBlockIO {

    String PRIORITY_LOW = "low";
    String PRIORITY_MEDIUM = "medium";
    String PRIORITY_HIGH = "high";

    @GET
    @Path("get_my_addresses/?api_key={apikey}")
    BlockIOResponseAddresses getAddresses(@PathParam("apikey") String apikey);

    @GET
    @Path("get_balance/?api_key={apikey}")
    BlockIOResponseBalance getBalance(@PathParam("apikey") String apikey);

    @GET
    @Path("withdraw/?api_key={apikey}&amounts={amount}&to_addresses={payment_address}&pin={pin}&priority={priority}")
    BlockIOResponseWithdrawal withdraw(@PathParam("apikey") String apikey, @PathParam("pin") String pin, @PathParam("amount") String amount, @PathParam("payment_address") String payment_address, @PathParam("priority") String priority);
}
