package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCoinRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
public interface IBitgoAPI {

    @POST
    @Path("/{coin}/wallet/{id}/sendcoins")
    @Consumes(MediaType.APPLICATION_JSON)
    Map<String, String> sendCoins(@HeaderParam("Authorization") String accessToken, @HeaderParam("Content-Type") String contentType, @PathParam("coin") String coin, @PathParam("id") String id, BitGoCoinRequest request);

    @GET
    @Path("/{coin}/wallet/balances")
    Map<String, Object> getTotalBalances(@HeaderParam("Authorization") String accessToken, @PathParam("coin") String coin);

    @GET
    @Path("/{coin}/wallet")
    Map<String, Object> getWallets(@HeaderParam("Authorization") String accessToken, @PathParam("coin") String coin);

    @GET
    @Path("/{coin}/wallet/{id}")
    Map<String, Object> getWalletById(@HeaderParam("Authorization") String accessToken, @PathParam("coin") String coin, @PathParam("id") String id);

    @GET
    @Path("/{coin}/wallet/{walletid}/address/{id}")
    Map<String, Object> getWalletAddressById(@HeaderParam("Authorization") String accessToken, @PathParam("coin") String coin, @PathParam("walletid") String walletId, @PathParam("id") String id);
}
