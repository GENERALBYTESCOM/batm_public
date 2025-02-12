package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.fasterxml.jackson.core.JsonParseException;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApi;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneFullyValidateTransferResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsQueryParams;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneRegisterWebhookRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferCreateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneUnregisterWebhookRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Wrapper for {@link NotabeneApi} providing easy access to Notabene API.
 *
 * @see NotabeneApi
 * @see NotabeneApiService
 */
@Slf4j
public class NotabeneApiWrapper {

    private final NotabeneApi api;
    private final NotabeneApiService apiService;

    public NotabeneApiWrapper(NotabeneApiFactory apiFactory, NotabeneApiService apiService) {
        this.api = apiFactory.getNotabeneApi();
        this.apiService = apiService;
    }

    /**
     * @see NotabeneApi#listVasps(String, String, Boolean)
     */
    public NotabeneListVaspsResponse listVasps(ITravelRuleProviderCredentials providerCredentials, NotabeneListVaspsQueryParams queryParams) {
        return apiService.callApi(providerCredentials, authorization -> api.listVasps(authorization, queryParams.getQuery(),
            queryParams.getAll()));
    }

    /**
     * @see NotabeneApi#validateFull(String, NotabeneTransferCreateRequest)
     */
    public NotabeneFullyValidateTransferResponse validateFull(ITravelRuleProviderCredentials providerCredentials,
                                                              NotabeneTransferCreateRequest request) {
        return apiService.callApi(providerCredentials, authorization -> api.validateFull(authorization, request));
    }

    /**
     * @see NotabeneApi#createTransfer(String, NotabeneTransferCreateRequest)
     */
    public NotabeneTransferInfo createTransfer(ITravelRuleProviderCredentials providerCredentials,
                                               NotabeneTransferCreateRequest request) {
        return apiService.callApi(providerCredentials, authorization -> api.createTransfer(authorization, request));
    }

    /**
     * @see NotabeneApi#updateTransfer(String, NotabeneTransferUpdateRequest)
     */
    public NotabeneTransferInfo updateTransfer(ITravelRuleProviderCredentials providerCredentials,
                                               NotabeneTransferUpdateRequest request) {
        return apiService.callApi(providerCredentials, authorization -> api.updateTransfer(authorization, request));
    }

    /**
     * @see NotabeneApi#getAddressOwnershipInformation(String, String, String, String)
     */
    public NotabeneAddressOwnershipInfoResponse getAddressOwnershipInformation(ITravelRuleProviderCredentials providerCredentials,
                                                                               NotabeneAddressOwnershipInfoRequest request) {
        return apiService.callApi(providerCredentials, authorization -> api.getAddressOwnershipInformation(authorization,
            request.getAddress(), request.getVaspDid(), request.getAsset()));
    }

    /**
     * @see NotabeneApi#approveTransfer(String, String)
     */
    public NotabeneTransferInfo approveTransfer(ITravelRuleProviderCredentials providerCredentials, String transferId) {
        return apiService.callApi(providerCredentials, authorization -> api.approveTransfer(authorization, transferId));
    }

    /**
     * @see NotabeneApi#registerWebhook(String, NotabeneRegisterWebhookRequest);
     */
    public void registerWebhook(ITravelRuleProviderCredentials providerCredentials, NotabeneRegisterWebhookRequest request) {
        apiService.callApi(providerCredentials, authorization -> {
            try {
                api.registerWebhook(authorization, request);
            } catch (JsonParseException e) {
                // On success, the endpoint returns plain text which cannot be parsed as json.
                // This is a problem because mazi.rescu only supports one media type in @Produces,
                // and we need json for parsing NotabeneApiException.
            }
            return null;
        });
    }

    /**
     * @see NotabeneApi#unregisterWebhook(String, NotabeneUnregisterWebhookRequest);
     */
    public void unregisterWebhook(ITravelRuleProviderCredentials providerCredentials, NotabeneUnregisterWebhookRequest request) {
        apiService.callApi(providerCredentials, authorization -> {
            try {
                api.unregisterWebhook(authorization, request);
            } catch (JsonParseException e) {
                // On success, the endpoint returns plain text which cannot be parsed as json.
                // This is a problem because mazi.rescu only supports one media type in @Produces,
                // and we need json for parsing NotabeneApiException.
            }
            return null;
        });
    }

}
