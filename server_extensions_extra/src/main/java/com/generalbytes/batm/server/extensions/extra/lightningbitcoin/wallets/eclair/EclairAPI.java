package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair;

import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Channel;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.ErrorResponseException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.Invoice;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto.SentInfo;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface EclairAPI {

    /**
     * @param invoice The invoice you want to decode
     * @return unique identifier for this payment attempt
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/parseinvoice")
    Invoice parseInvoice(@FormParam("invoice") String invoice) throws IOException, ErrorResponseException;

    /**
     * Pays a BOLT11 invoice
     * @param invoice The invoice you want to pay
     * @param amountMsat Amount in to pay if the invoice does not have one (Millisatoshi)
     * @return unique identifier for this payment attempt
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/payinvoice")
    String payInvoice(@FormParam("invoice") String invoice, @FormParam("amountMsat") BigInteger amountMsat) throws IOException, ErrorResponseException;

    /**
     *
     * @param id The unique id of the payment attempt
     * @return a list containing at most one element.
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/getsentinfo")
    List<SentInfo> getSentInfoById(@FormParam("id") String id) throws IOException, ErrorResponseException;

    /**
     *
     * @param paymentHash The payment hash common to all payment attepts to be retrieved
     * @return
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/getsentinfo")
    List<SentInfo> getSentInfoByPaymentHash(@FormParam("paymentHash") String paymentHash) throws IOException, ErrorResponseException;

    /**
     *
     * @return the list of local channels
     * @throws IOException
     * @throws ErrorResponseException
     */
    @POST
    @Path("/channels")
    List<Channel> getChannels() throws IOException, ErrorResponseException;

}
