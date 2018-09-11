package com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface LskBinanceAPI { 	
    @GET
    @Path("/api/v3/account")
    Map<String, Object> getCryptoBalance(@HeaderParam("X-MBX-APIKEY") String apiKey, @QueryParam("recvWindow") String recvWindow, @QueryParam("timestamp") String timeStamp, @QueryParam("signature") String signature ) throws IOException;
    @POST
    @Path("wapi/v1/withdraw.html")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    LskSendCoinResponse sendLsks(@HeaderParam("X-MBX-APIKEY") String apiKey, @QueryParam("asset") String asset, @QueryParam("address") String address, @QueryParam("amount") String amount, @QueryParam("name") String name, @QueryParam("recvWindow") String recvWindow, @QueryParam("timestamp") String timeStamp, @QueryParam("signature") String signature) throws IOException;
} 