package com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks;

import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderException;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.SumsubProvider;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo.SumsubTransactionInformationResponse;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.webhooks.dto.SumsubWebhookRequest;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Validator for {@link SumsubWebhookRestService}.
 */
public class SumsubWebhookValidator {

    private static final String HMAC_ALGORITHM = "HmacSHA512";

    /**
     * Validate Sumsub webhook message received on the {@link SumsubWebhookRestService#handleWebhookMessage}.
     *
     * @param message {@link SumsubWebhookMessage}
     */
    public void validateSumsubWebhookMessage(SumsubWebhookMessage message) {
        if (StringUtils.isBlank(message.getType())) {
            throw new TravelRuleProviderException(String.format(
                    "Sumsub incoming message does not contain type. Txn ID: %s, Sumsub transaction ID: %s",
                    message.getKytDataTxnId(), message.getKytTxnId()
            ));
        }

        if (StringUtils.isBlank(message.getKytDataTxnId())) {
            throw new TravelRuleProviderException(String.format(
                    "Sumsub incoming message does not contain kytDataTxnId, unable to pair transfer. Sumsub transaction ID: %s",
                    message.getKytTxnId()
            ));
        }
    }

    /**
     * Validate response from {@link SumsubProvider#getTransactionInformation}.
     *
     * @param response Response.
     * @param message  Received incoming message.
     */
    public void validateTransactionInformationResponse(SumsubTransactionInformationResponse response, SumsubWebhookMessage message) {
        if (response.getData().getApplicant() == null) {
            throwException(message, "'data.applicant' object is null");
        }

        if (response.getData().getApplicant().getInstitutionInfo() == null) {
            throwException(message, "'data.applicant.institutionInfo' object is null");
        }

        if (response.getData().getCounterparty() == null) {
            throwException(message, "'data.counterparty' object is null");
        }

        if (response.getData().getCounterparty().getInstitutionInfo() == null) {
            throwException(message, "'data.counterparty.institutionInfo' object is null");
        }

        if (response.getData().getCounterparty().getPaymentMethod() == null) {
            throwException(message, "'data.counterparty.paymentMethod' object is null");
        }
    }

    private void throwException(SumsubWebhookMessage message, String reason) {
        throw new TravelRuleProviderException(String.format(
                "The transaction data obtained from Sumsub is not valid, %s. Txn ID: %s, Sumsub transaction ID: %s",
                reason, message.getKytDataTxnId(), message.getKytTxnId()
        ));
    }

    /**
     * Verifies that the signature of the received webhook message matches the expected value.
     *
     * @param webhookRequest Received webhook request containing HTTP headers and message.
     * @param secretKey      The secret key for signature verification.
     */
    public void validateSignature(SumsubWebhookRequest webhookRequest, String secretKey) {
        validateRequestParams(webhookRequest);
        validateHmacSha512Signature(webhookRequest, secretKey);
    }

    private void validateRequestParams(SumsubWebhookRequest webhookRequest) {
        if (StringUtils.isBlank(webhookRequest.message())) {
            throw new TravelRuleProviderException(
                    "Received Sumsub webhook does not contain a message payload."
            );
        }

        if (StringUtils.isBlank(webhookRequest.digestAlgorithm())) {
            throw new TravelRuleProviderException(
                    "Digest algorithm is not present in HTTP request header. Check signature settings in Sumsub Webhook Manager."
            );
        }

        if (!SumsubTravelRuleApiConstants.DigestAlgorithm.SHA_512.equals(webhookRequest.digestAlgorithm())) {
            throw new TravelRuleProviderException(
                    "Digest algorithm '" + webhookRequest.digestAlgorithm()
                            + "' is not supported. Use SHA-512 algorithm in Sumsub Webhook Manager."
            );
        }

        if (StringUtils.isBlank(webhookRequest.payloadDigest())) {
            throw new TravelRuleProviderException("Payload digest is not present in HTTP request header.");
        }
    }

    private void validateHmacSha512Signature(SumsubWebhookRequest webhookRequest, String secretKey) {
        Mac mac = initializeMac(secretKey);

        byte[] rawHmac = mac.doFinal(webhookRequest.message().getBytes(StandardCharsets.UTF_8));
        String calculatedSignature = bytesToHexString(rawHmac);

        boolean isSignatureValid = webhookRequest.payloadDigest().equals(calculatedSignature);
        if (!isSignatureValid) {
            throw new TravelRuleProviderException("The received digest does not match the signature of the received payload.");
        }
    }

    private Mac initializeMac(String secretKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return mac;
        } catch (NoSuchAlgorithmException e) {
            throw new TravelRuleProviderException("HMAC algorithm 'HmacSHA512' is not available.");
        } catch (InvalidKeyException e) {
            throw new TravelRuleProviderException(
                    "Failed to initialize Mac for verifying signature. Check if the private key is set in the Sumsub provider settings."
            );
        }
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

}
