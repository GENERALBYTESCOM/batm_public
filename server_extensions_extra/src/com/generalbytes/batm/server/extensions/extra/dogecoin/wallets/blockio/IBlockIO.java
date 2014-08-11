package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by b00lean on 8/11/14.
 */
@Path("/api/v1/")
@Produces(MediaType.APPLICATION_JSON)
public interface IBlockIO {
    @GET
    @Path("get_my_addresses/?api_key={apikey}")
    BlockIOResponseAddresses getAddresses(@PathParam("apikey") String apikey);

    @GET
    @Path("get_balance/?api_key={apikey}")
    BlockIOResponseBalance getBalance(@PathParam("apikey") String apikey);

    @GET
    @Path("withdraw/?api_key={apikey}&amount={amount}&payment_address={payment_address}&pin={pin}")
    BlockIOResponseWithdrawal withdraw(@PathParam("apikey") String apikey, @PathParam("pin") String pin, @PathParam("amount") String amount, @PathParam("payment_address") String payment_address);

}
