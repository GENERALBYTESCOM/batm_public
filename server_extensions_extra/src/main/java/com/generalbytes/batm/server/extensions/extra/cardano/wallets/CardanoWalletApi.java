package com.generalbytes.batm.server.extensions.extra.cardano.wallets;

import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.CreateTransactionRequest;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Transaction;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.Wallet;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.dto.WalletAddress;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
