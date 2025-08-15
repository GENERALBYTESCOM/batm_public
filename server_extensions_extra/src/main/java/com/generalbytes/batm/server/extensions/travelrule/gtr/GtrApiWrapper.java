package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrNotifyTxIdRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrNotifyTxIdResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrRegisterTravelRuleResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspListResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import lombok.AllArgsConstructor;

/**
 * Wrapper for {@link GtrApi} providing easy access to Global Travel Rule (GTR) API.
 *
 * @see GtrApi
 * @see GtrApiService
 */
@AllArgsConstructor
public class GtrApiWrapper {

    private final GtrApi api;
    private final GtrApiService apiService;

    /**
     * Get list of all VASPs.
     *
     * @see GtrApi#listVasps(String)
     */
    public GtrVaspListResponse listVasps(GtrCredentials credentials) {
        return apiService.callApi(credentials, api::listVasps);
    }

    /**
     * Get detail about VASP.
     *
     * @see GtrApi#vaspDetail(String, String)
     */
    public GtrVaspResponse vaspDetail(GtrCredentials credentials, String vaspCode) {
        return apiService.callApi(credentials, authorization -> api.vaspDetail(authorization, vaspCode));
    }

    /**
     * Registers a new Travel Rule request.
     *
     * @see GtrApi#registerTravelRuleRequest(String, GtrRegisterTravelRuleRequest)
     */
    public GtrRegisterTravelRuleResponse registerTravelRuleRequest(GtrCredentials credentials, GtrRegisterTravelRuleRequest request) {
        return apiService.callApi(credentials, authorization -> api.registerTravelRuleRequest(authorization, request));
    }

    /**
     * Verify address with GTR.
     *
     * @see GtrApi#verifyAddress(String, GtrVerifyAddressRequest)
     */
    public GtrVerifyAddressResponse verifyAddress(GtrCredentials credentials, GtrVerifyAddressRequest request) {
        return apiService.callApi(credentials, authorization -> api.verifyAddress(authorization, request));
    }

    /**
     * Verify PII (Personally Identifiable Information) with GTR.
     *
     * @see GtrApi#verifyPii(String, GtrVerifyPiiRequest)
     */
    public GtrVerifyPiiResponse verifyPii(GtrCredentials credentials, GtrVerifyPiiRequest request) {
        return apiService.callApi(credentials, authorization -> api.verifyPii(authorization, request));
    }

    /**
     * Notifies the beneficiary VASP via GTR about the on-chain transaction hash.
     *
     * @see GtrApi#notifyTxId(String, GtrNotifyTxIdRequest)
     */
    public GtrNotifyTxIdResponse notifyTxId(GtrCredentials credentials, GtrNotifyTxIdRequest request) {
        return apiService.callApi(credentials, authorization -> api.notifyTxId(authorization, request));
    }

}
