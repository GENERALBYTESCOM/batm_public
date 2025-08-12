package com.generalbytes.batm.server.extensions.extra.cardano.wallets;

import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.CreateTransactionRequest;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Transaction;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Wallet;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.WalletAddress;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/v2/wallets")
@Produces(MediaType.APPLICATION_JSON)
public interface CardanoWalletApi {

    @GET
    @Path("/{walletId}")
    Wallet getWallet(@PathParam("walletId") String walletId) throws IOException;

    @GET
    @Path("/{walletId}/addresses")
    List<WalletAddress> getWalletAddresses(@PathParam("walletId") String walletId)  throws IOException;

    @POST
    @Path("/{walletId}/transactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Transaction createTransaction(@PathParam("walletId") String walletId, CreateTransactionRequest requestBody)  throws IOException;
}
