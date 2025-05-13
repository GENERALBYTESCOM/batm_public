package com.generalbytes.batm.server.extensions.travelrule.notabene.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneFullyValidateTransferResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsQueryParams;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneRegisterWebhookRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferCreateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneUnregisterWebhookRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Provides access to relevant Notabene endpoints.
 *
 * @see <a href="https://devx.notabene.id/reference/api-reference">Notabene Documentation</a>
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface NotabeneApi {

    String AUTHORIZATION_HEADER_NAME = "Authorization";

    /**
     * Returns a list of VASPs. VASPs can be searched and sorted and results are paginated.
     *
     * @param authorization    The Authorization header content.
     * @param query            The query filter for VASPs.
     * @param returnAllRecords True to return all records at once, false to use pagination.
     * @return The response.
     * @see <a href="https://devx.notabene.id/reference/tfsimplelistvasps-1">Notabene Documentation</a>
     * @see NotabeneListVaspsQueryParams
     * @see NotabeneListVaspsResponse
     */
    @GET
    @Path("/tf/simple/vasps")
    NotabeneListVaspsResponse listVasps(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                                        @QueryParam("q") String query,
                                        @QueryParam("all") Boolean returnAllRecords) throws NotabeneApiException;

    /**
     * Fully validate a transfer.
     *
     * @param request The request.
     * @return The response.
     * @see <a href="https://devx.notabene.id/reference/txvalidatefull-1">Notabene Documentation</a>
     * @see NotabeneTransferCreateRequest
     * @see NotabeneFullyValidateTransferResponse
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/tx/validate/full")
    NotabeneFullyValidateTransferResponse validateFull(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                                                       NotabeneTransferCreateRequest request) throws NotabeneApiException;

    /**
     * Creates a new transfer. The fields required in a transfer differ depending on the jurisdiction of the originating VASP.
     * Additional data may be provided to the beneficiary VASP depending on their jurisdiction.
     *
     * @param request The request.
     * @return The response.
     * @see <a href="https://devx.notabene.id/reference/txcreate-1">Notabene Documentation</a>
     * @see NotabeneTransferCreateRequest
     * @see NotabeneTransferInfo
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/tx/create")
    NotabeneTransferInfo createTransfer(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                                        NotabeneTransferCreateRequest request) throws NotabeneApiException;

    /**
     * Update a transfer with the passed parameters.
     *
     * @param request The request.
     * @return The response.
     * @see <a href="https://devx.notabene.id/reference/txupdate-1">Notabene Documentation</a>
     * @see NotabeneTransferUpdateRequest
     * @see NotabeneTransferInfo
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/tx/update")
    NotabeneTransferInfo updateTransfer(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                                        NotabeneTransferUpdateRequest request) throws NotabeneApiException;

    /**
     * Approves an outgoing transfer. If the VASP is present in the Notabene Directory,
     * approving a transfer will send the transfer to them and set the transfer status
     * to {@link NotabeneTransferStatus#SENT}. If the VASP is not in the Notabene Directory,
     * approving the transfer will set the status to {@link NotabeneTransferStatus#WAITING_FOR_INFORMATION}.
     *
     * @param transferId Identifier of the transfer to approve.
     * @return The response.
     * @see <a href="https://devx.notabene.id/reference/txapprove-1">Notabene Documentation</a>
     */
    @POST
    @Path("/tx/approve")
    NotabeneTransferInfo approveTransfer(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                                         @QueryParam("id") String transferId) throws NotabeneApiException;

    /**
     * Confirms that the blockchain address of the transfer belongs to the beneficiary VASP.
     * Confirming sets the status of a transfer to {@link NotabeneTransferStatus#ACK}.
     *
     * @param transferId Identifier of the transfer to confirm.
     * @return The response.
     * @see <a href="https://devx.notabene.id/reference/txconfirm-1">Notabene Documentation</a>
     */
    @POST
    @Path("/tx/confirm")
    NotabeneTransferInfo confirmTransfer(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                                         @QueryParam("id") String transferId) throws NotabeneApiException;

    /**
     * Rejects a transfer indicating that the blockchain address is not owned by the beneficiary VASP.
     * Rejecting sets the transfer status to {@link NotabeneTransferStatus#REJECTED}..
     *
     * @param transferId Identifier of the transfer to reject.
     * @return The response.
     * @see <a href="https://devx.notabene.id/reference/txreject-1">Notabene Documentation</a>
     */
    @POST
    @Path("/tx/reject")
    NotabeneTransferInfo rejectTransfer(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                                        @QueryParam("id") String transferId) throws NotabeneApiException;

    /**
     * Get the ownership information about a customer blockchain address.
     *
     * @param address The blockchain address.
     * @param vaspDid VASP did.
     * @param asset   Cryptocurrency.
     * @return The response.
     * @see <a href="https://devx.notabene.id/reference/addressownershipget">Notabene Documentation</a>
     * @see NotabeneAddressOwnershipInfoResponse
     */
    @GET
    @Path("/v1/addresses/address-ownerships/{address}")
    NotabeneAddressOwnershipInfoResponse getAddressOwnershipInformation(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                                                                        @PathParam("address") String address,
                                                                        @QueryParam("vasp_did") String vaspDid,
                                                                        @QueryParam("asset") String asset) throws NotabeneApiException;

    /**
     * Register the multi-message Webhook URL for a given VASP.
     *
     * @param request The request.
     * @see <a href="https://devx.notabene.id/reference/setwebhook">Notabene Documentation</a>
     * @see NotabeneRegisterWebhookRequest
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/webhook")
    void registerWebhook(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                         NotabeneRegisterWebhookRequest request) throws NotabeneApiException, JsonParseException;

    /**
     * Unregister the multi-message Webhook URL for a given VASP.
     *
     * @param request The request.
     * @see <a href="https://devx.notabene.id/reference/deletewebhook">Notabene Documentation</a>
     * @see NotabeneUnregisterWebhookRequest
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/webhook")
    void unregisterWebhook(@HeaderParam(AUTHORIZATION_HEADER_NAME) String authorization,
                           NotabeneUnregisterWebhookRequest request) throws NotabeneApiException, JsonParseException;

}
