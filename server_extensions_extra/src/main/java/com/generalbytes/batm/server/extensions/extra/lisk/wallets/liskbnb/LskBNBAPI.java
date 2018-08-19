package com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbnb;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface LskBNBAPI { 
	
	@GET
    @Path("/api/v3/account")
	Map<String, Object> getCryptoBalance(@HeaderParam("X-MBX-APIKEY") String apikey, @QueryParam("recvWindow") String recvWindow, @QueryParam("timestamp") String timestamp, @QueryParam("signature") String signature ) throws IOException;
	
	@POST
    @Path("wapi/v1/withdraw.html")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	LskSendCoinResponse sendLsks(@HeaderParam("X-MBX-APIKEY") String apikey, @QueryParam("asset") String asset, @QueryParam("address") String address, @QueryParam("amount") String amount, @QueryParam("name") String name, @QueryParam("recvWindow") String recvWindow, @QueryParam("timestamp") String timestamp, @QueryParam("signature") String signature) throws IOException;
} 