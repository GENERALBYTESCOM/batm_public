package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2;

import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

@Path("/api/v2")
@Produces(MediaType.APPLICATION_JSON)
public interface ICryptXAPI {

    String PRIORITY_LOW = "low";
    String PRIORITY_MEDIUM = "medium";
    String PRIORITY_HIGH = "high";
    String PRIORITY_CUSTOM = "custom";

    @POST
    @Path("/{coin}/wallet/{walletId}/transaction")
    @Consumes(MediaType.APPLICATION_JSON)
    Map<String, Object> sendTransaction(
        @PathParam("coin") String coin,
        @PathParam("walletId") String walletId,
        CryptXSendTransactionRequest request
    ) throws IOException;

    @GET
    @Path("/{coin}/wallet/{walletId}")
    Map<String, Object> getWallet(
        @PathParam("coin") String coin,
        @PathParam("walletId") String walletId,
        @QueryParam("includeBalance") boolean includeBalance
    ) throws IOException;

    @POST
    @Path("/{coin}/wallet/{walletId}/address")
    @Consumes(MediaType.APPLICATION_JSON)
    Map<String, Object> createAddress(
        @PathParam("coin") String coin,
        @PathParam("walletId") String walletId,
        CryptXCreateAddressRequest request
    ) throws IOException;

    @GET
    @Path("{coin}/wallet/{walletId}/balance")
    Balance getWalletBalance(
        @PathParam("coin") String coin,
        @PathParam("walletId") String walletId,
        @QueryParam("allTokens") Boolean allTokens
    ) throws IOException;

    @GET
    @Path("{coin}/wallet/{walletId}/{address}/amount")
    CryptXReceivedAmount getReceivedAmount(
        @PathParam("coin") String coin,
        @PathParam("walletId") String walletId,
        @PathParam("address") String address
    ) throws IOException;

}