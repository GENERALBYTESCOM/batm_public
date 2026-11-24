package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal;

import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalApiException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalBalance;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalPage;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalSendRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalSendResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.dto.CryptalTransaction;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import si.mazi.rescu.ParamsDigest;

import java.io.IOException;

import static com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.CryptalSignatureDigest.API_KEY_HEADER;
import static com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.CryptalSignatureDigest.SIGNATURE_HEADER;
import static com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptal.CryptalSignatureDigest.TIMESTAMP_HEADER;

@Path("/api/v3")
@Produces(MediaType.APPLICATION_JSON)
public interface ICryptalWalletProApi {

    @GET
    @Path("/balance/{currency}/sendable")
    CryptalBalance getSendableBalance(@HeaderParam(API_KEY_HEADER) String apiKey,
                                      @HeaderParam(TIMESTAMP_HEADER) long timestamp,
                                      @HeaderParam(SIGNATURE_HEADER) ParamsDigest signature,
                                      @PathParam("currency") String currency) throws IOException, CryptalApiException;

    @GET
    @Path("/address")
    CryptalPage<CryptalAddress> getAddresses(@HeaderParam(API_KEY_HEADER) String apiKey,
                                             @HeaderParam(TIMESTAMP_HEADER) long timestamp,
                                             @HeaderParam(SIGNATURE_HEADER) ParamsDigest signature,
                                             @QueryParam("currency") String currency,
                                             @QueryParam("network") String network,
                                             @QueryParam("address") String query,
                                             @QueryParam("page") int page,
                                             @QueryParam("limit") int limit) throws IOException, CryptalApiException;

    @POST
    @Path("/address")
    @Consumes(MediaType.APPLICATION_JSON)
    CryptalAddress createAddress(@HeaderParam(API_KEY_HEADER) String apiKey,
                                 @HeaderParam(TIMESTAMP_HEADER) long timestamp,
                                 @HeaderParam(SIGNATURE_HEADER) ParamsDigest signature,
                                 CryptalCreateAddressRequest request) throws IOException, CryptalApiException;

    @POST
    @Path("/transaction/send")
    @Consumes(MediaType.APPLICATION_JSON)
    CryptalSendResponse send(@HeaderParam(API_KEY_HEADER) String apiKey,
                             @HeaderParam(TIMESTAMP_HEADER) long timestamp,
                             @HeaderParam(SIGNATURE_HEADER) ParamsDigest signature,
                             CryptalSendRequest request) throws IOException, CryptalApiException;

    @GET
    @Path("/transaction/history")
    CryptalPage<CryptalTransaction> getTransactions(@HeaderParam(API_KEY_HEADER) String apiKey,
                                                    @HeaderParam(TIMESTAMP_HEADER) long timestamp,
                                                    @HeaderParam(SIGNATURE_HEADER) ParamsDigest signature,
                                                    @QueryParam("address") String address,
                                                    @QueryParam("currencyCode") String currency,
                                                    @QueryParam("transactionType") String type,
                                                    @QueryParam("status") String status,
                                                    @QueryParam("offset") int page,
                                                    @QueryParam("limit") int limit) throws IOException, CryptalApiException;

    @GET
    @Path("/transaction/history")
    CryptalPage<CryptalTransaction> getTransactionsByUniqueId(@HeaderParam(API_KEY_HEADER) String apiKey,
                                                              @HeaderParam(TIMESTAMP_HEADER) long timestamp,
                                                              @HeaderParam(SIGNATURE_HEADER) ParamsDigest signature,
                                                              @QueryParam("uniqueId") String uniqueId,
                                                              @QueryParam("transactionType") String type,
                                                              @QueryParam("limit") int limit) throws IOException, CryptalApiException;
}
