package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApiException;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneAddressOwnershipInfoResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneCryptoAddressType;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsQueryParams;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneListVaspsResponse;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneRegisterWebhookRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferCreateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneUnregisterWebhookRequest;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneVaspInfoSimple;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class NotabeneService {

    private final NotabeneApiWrapper api;
    private final NotabeneConfiguration configuration;

    /**
     * Get all available VASPs from Notabene.
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to get the VASPs for.
     * @return List of available VASPs. Empty if the retrieval fails.
     */
    public List<NotabeneVaspInfoSimple> getAllVasps(ITravelRuleProviderCredentials providerCredentials) {
        NotabeneListVaspsQueryParams queryParams = new NotabeneListVaspsQueryParams();
        return getVasps(providerCredentials, queryParams);
    }

    private List<NotabeneVaspInfoSimple> getVasps(ITravelRuleProviderCredentials providerCredentials, NotabeneListVaspsQueryParams queryParams) {
        try {
            NotabeneListVaspsResponse response = api.listVasps(providerCredentials, queryParams);
            return response.getVasps();
        } catch (Exception e) {
            log.warn("Failed to get VASPs from Notabene: {}", getExceptionMessage(e));
            throw new TravelRuleProviderException("Failed to fetch VASPs from Notabene.");
        }
    }

    /**
     * Create a new transfer.
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to create the transfer for.
     * @param request             The request.
     * @return A {@link NotabeneTransferInfo} or null if the creation fails.
     */
    public NotabeneTransferInfo createTransfer(ITravelRuleProviderCredentials providerCredentials, NotabeneTransferCreateRequest request) {
        try {
            return api.createTransfer(providerCredentials, request);
        } catch (Exception e) {
            log.warn("Failed to create transfer at Notabene: {}", getExceptionMessage(e));
            return null;
        }
    }

    /**
     * Approve an existing transfer.
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to approve the transfer for.
     * @param transferId          Identifier of the transfer to approve.
     * @return The approved {@link NotabeneTransferInfo} or null if the approval fails.
     */
    public NotabeneTransferInfo approveTransfer(ITravelRuleProviderCredentials providerCredentials, String transferId) {
        try {
            return api.approveTransfer(providerCredentials, transferId);
        } catch (Exception e) {
            log.warn("Failed to approve transfer at Notabene: {}", getExceptionMessage(e));
            return null;
        }
    }

    /**
     * Update an existing transfer.
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to update the transfer for.
     * @param request             The request.
     * @return The updated {@link NotabeneTransferInfo} or null if the update fails.
     */
    public NotabeneTransferInfo updateTransfer(ITravelRuleProviderCredentials providerCredentials, NotabeneTransferUpdateRequest request) {
        try {
            return api.updateTransfer(providerCredentials, request);
        } catch (Exception e) {
            log.warn("Failed to update transfer at Notabene: {}", getExceptionMessage(e));
            return null;
        }
    }

    /**
     * Get the ownership information about a customer blockchain address.
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to create the transfer for.
     * @param request             The request.
     * @return A {@link NotabeneAddressOwnershipInfoResponse}.
     */
    public NotabeneAddressOwnershipInfoResponse getAddressOwnershipInformation(ITravelRuleProviderCredentials providerCredentials,
                                                                               NotabeneAddressOwnershipInfoRequest request) {
        try {
            return api.getAddressOwnershipInformation(providerCredentials, request);
        } catch (Exception e) {
            if (isCausedByHttpNotFound(e)) {
                // 404 Not Found -> Notabene does not have the information, which is not a failure.
                return createNotabeneAddressOwnershipInfoResponseUnknown();
            }
            log.warn("Failed to get address ownership information from Notabene: {}", getExceptionMessage(e));
            return null;
        }
    }

    private NotabeneAddressOwnershipInfoResponse createNotabeneAddressOwnershipInfoResponseUnknown() {
        NotabeneAddressOwnershipInfoResponse response = new NotabeneAddressOwnershipInfoResponse();
        response.setAddressType(NotabeneCryptoAddressType.UNKNOWN);
        return response;
    }

    /**
     * Register a webhook. This operation is idempotent.
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to register the webhook for.
     * @return True if the webhook was registered, false otherwise.
     */
    public boolean registerWebhook(ITravelRuleProviderCredentials providerCredentials) {
        NotabeneRegisterWebhookRequest request = new NotabeneRegisterWebhookRequest();
        request.setVaspDid(providerCredentials.getVaspDid());
        String webhookUrl = getWebhookUrl();
        log.info("Registering webhook at Notabene: {}", webhookUrl);
        request.setUrl(webhookUrl);

        try {
            api.registerWebhook(providerCredentials, request);
            return true;
        } catch (Exception e) {
            log.error("Failed to register webhook at Notabene: {}", getExceptionMessage(e));
        }

        return false;
    }

    private String getWebhookUrl() {
        String masterExtensionsUrl = configuration.getMasterExtensionsUrl();
        return String.format("%s/notabene/webhooks", masterExtensionsUrl);
    }

    /**
     * Unregister a webhook.
     *
     * @param providerCredentials The {@link ITravelRuleProviderCredentials} to unregister the webhook for.
     * @return True if the webhook was unregistered, false otherwise.
     */
    public boolean unregisterWebhook(ITravelRuleProviderCredentials providerCredentials) {
        NotabeneUnregisterWebhookRequest request = new NotabeneUnregisterWebhookRequest();
        request.setVaspDid(providerCredentials.getVaspDid());

        try {
            api.unregisterWebhook(providerCredentials, request);
            return true;
        } catch (Exception e) {
            if (isCausedByHttpNotFound(e)) {
                // There was no webhook to unregister, which is ok.
                return true;
            }

            log.warn("Failed to unregister webhook at Notabene: {}", getExceptionMessage(e));
        }

        return false;
    }

    /**
     * Tests whether the credentials are valid. The endpoint for obtaining VASPs is used for testing.
     *
     * @param providerCredentials {@link ITravelRuleProviderCredentials}
     * @return {@code True} if credentials are valid, otherwise {@code false}.
     */
    public boolean testProviderCredentials(ITravelRuleProviderCredentials providerCredentials) {
        try {
            api.listVasps(providerCredentials, new NotabeneListVaspsQueryParams());
            return true;
        } catch (Exception e) {
            log.warn("Notabene credentials test failed: {}", getExceptionMessage(e));
            return false;
        }
    }

    private String getExceptionMessage(Exception e) {
        log.trace("Notabene service failure: ", e);
        return e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
    }

    private boolean isCausedByHttpNotFound(Exception e) {
        log.trace("Checking if exception is caused by HTTP 404 Not Found: ", e);
        return e instanceof NotabeneApiException notabeneApiException && notabeneApiException.getCode() == HttpServletResponse.SC_NOT_FOUND;
    }

}
