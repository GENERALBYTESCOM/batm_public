package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101.GtrIvms101Payload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrTransferHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrObjectMapper;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrVerifyPiiMapper;
import com.generalbytes.batm.server.extensions.travelrule.gtr.util.Curve25519Encryptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Service for PII (Personally Identifiable Information) verification with GTR.
 */
@Slf4j
@AllArgsConstructor
public class GtrVerifyPiiService {

    private final GtrApiWrapper api;
    private final Curve25519Encryptor curve25519Encryptor;
    private final GtrObjectMapper objectMapper;
    private final GtrTransferHandler transferHandler;
    private final GtrProviderRegistry gtrProviderRegistry;
    private final TravelRuleExtensionContext extensionContext;

    /**
     * Verify PII (Personally Identifiable Information) using GTR.
     *
     * @param transferData           {@link ITravelRuleTransferData}
     * @param requestId              Request ID.
     * @param targetVaspPublicKey    Public key of target VASP.
     * @return Object containing data about the result of PII verification.
     */
    public GtrVerifyPiiResponse verifyPii(GtrCredentials credentials,
                                          ITravelRuleTransferData transferData,
                                          String requestId,
                                          String targetVaspPublicKey
    ) {
        GtrIvms101Payload ivms101Payload = GtrVerifyPiiMapper.toGtrIvms101Payload(transferData);
        String serializedIvms101Payload = objectMapper.serializeIvms101Payload(ivms101Payload);
        String encryptedIvms101Payload = curve25519Encryptor.encrypt(
                serializedIvms101Payload, targetVaspPublicKey, credentials.getCurvePrivateKey()
        );
        BigDecimal cryptoAmount = getCryptoAmount(transferData);

        GtrVerifyPiiRequest request = GtrVerifyPiiMapper.toGtrVerifyPiiRequest(
                transferData, requestId, credentials.getCurvePublicKey(), targetVaspPublicKey, encryptedIvms101Payload, cryptoAmount
        );

        GtrVerifyPiiResponse response = api.verifyPii(credentials, request);
        log.info("GTR, request ID: '{}', transfer '{}' has been created, target VASP: {}",
                requestId, response.getTravelRuleId(), transferData.getBeneficiaryVasp().getDid());

        return response;
    }

    private BigDecimal getCryptoAmount(ITravelRuleTransferData transferData) {
        return extensionContext.convertCryptoFromBaseUnit(transferData.getTransactionAmount(), transferData.getTransactionAsset());
    }

    /**
     * Processes the PII verification message from GTR and notifies the server of the received data.
     *
     * @param webhookMessage The webhook message from GTR.
     * @param callbackData   Callback data for the webhook used for PII verification.
     */
    public void processVerifyPiiWebhookMessage(GtrWebhookMessage webhookMessage, GtrPiiVerifyWebhookPayload callbackData) {
        String curvePrivateKey = getGtrProviderCurvePrivateKey(webhookMessage);

        GtrIvms101Payload ivms101 = getIvms101Payload(callbackData, curvePrivateKey);
        String rawData = objectMapper.serializeGtrWebhookMessage(webhookMessage);

        transferHandler.handleVerifyPiiWebhookMessage(callbackData, ivms101, rawData);
    }

    private GtrIvms101Payload getIvms101Payload(GtrPiiVerifyWebhookPayload piiVerifyPayload, String curvePrivateKey) {
        String decryptedIvms101Payload = decryptIvms101Payload(piiVerifyPayload, curvePrivateKey);
        return objectMapper.deserializeIvms101Payload(decryptedIvms101Payload);
    }

    private String decryptIvms101Payload(GtrPiiVerifyWebhookPayload piiVerifyPayload, String curvePrivateKey) {
        return curve25519Encryptor.decrypt(
                piiVerifyPayload.getEncryptedPayload(), piiVerifyPayload.getInitiatorPublicKey(), curvePrivateKey
        );
    }

    private String getGtrProviderCurvePrivateKey(GtrWebhookMessage webhookMessage) {
        GtrProvider provider = gtrProviderRegistry.get(webhookMessage.getInvokeVaspCode());
        if (provider == null) {
            throw new TravelRuleProviderException("GTR provider with VASP DID '" + webhookMessage.getInvokeVaspCode() + "' not found");
        }

        GtrCredentials credentials = provider.getCredentials();

        return credentials.getCurvePrivateKey();
    }

}
