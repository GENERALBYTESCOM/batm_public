package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto.BittrexNewWithdrawal;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto.BittrexWithdrawal;
import org.knowm.xchange.bittrex.BittrexAuthenticated;
import org.knowm.xchange.bittrex.dto.BittrexException;
import si.mazi.rescu.ParamsDigest;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Temporary solution. Remove when withdrawal will be available in org.knowm.xchange:xchange-bitfinex
 */
//TODO: BATM-2327 remove this class
@Path("v3")
@Produces(MediaType.APPLICATION_JSON)
public interface BittrexAuthenticatedCustom extends BittrexAuthenticated {

    @POST
    @Path("withdrawals")
    @Consumes(MediaType.APPLICATION_JSON)
    BittrexWithdrawal createNewWithdrawal(
        @HeaderParam("Api-Key") String apiKey,
        @HeaderParam("Api-Timestamp") Long timestamp,
        @HeaderParam("Api-Content-Hash") ParamsDigest hash,
        @HeaderParam("Api-Signature") ParamsDigest signature,
        BittrexNewWithdrawal withdrawal)
        throws IOException, BittrexException;
}
