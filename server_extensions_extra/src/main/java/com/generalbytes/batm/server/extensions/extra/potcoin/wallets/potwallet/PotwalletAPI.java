package com.generalbytes.batm.server.extensions.extra.potcoin.wallets.potwallet;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public interface PotwalletAPI {
    @GET
    @Path("/balance")
    PotwalletResponse getCryptoBalance(@HeaderParam("Access-Public") String publicKey, @HeaderParam("Access-Hash") String accessHash, @HeaderParam("Access-Nonce") String nonce) throws IOException;
    @GET
    @Path("/address")
    PotwalletResponse getAddress(@HeaderParam("Access-Public") String publicKey, @HeaderParam("Access-Hash") String accessHash, @HeaderParam("Access-Nonce") String nonce) throws IOException;
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    PotwalletResponse sendPots(@HeaderParam("Access-Public") String publicKey, @HeaderParam("Access-Hash") String accessHash, @HeaderParam("Access-Nonce") String nonce, PotwalletSendRequest request) throws IOException;
}
