package com.generalbytes.batm.server.extensions.travelrule.sumsub.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class containing API constants for calling endpoints on {@link SumsubTravelRuleApi}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SumsubTravelRuleApiConstants {
    public static final String TRAVEL_RULE_REQUEST_TYPE = "travelRule";

    /**
     * Transaction info constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TransactionInfo {
        public static final String CRYPTO_CURRENCY_TYPE = "crypto";
        public static final String OUT_DIRECTION = "out";
    }

    /**
     * Identity info constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class IdentityInfo {
        public static final String INDIVIDUAL_TYPE = "individual";
    }

    /**
     * Payment method constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PaymentMethod {
        public static final String CRYPTO_TYPE = "crypto";
    }

    /**
     * Webhook type constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class WebhookType {
        public static final String APPLICANT_KYT_TXN_APPROVED = "applicantKytTxnApproved";
        public static final String APPLICANT_KYT_TXN_REJECTED = "applicantKytTxnRejected";
        public static final String APPLICANT_KYT_ON_HOLD = "applicantKytOnHold";
    }

    /**
     * Digest algorithm constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DigestAlgorithm {
        public static final String SHA_512 = "HMAC_SHA512_HEX";
    }

    /**
     * HTTP header params.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class HttpHeaderParam {
        public static final String VASP_DID = "Vasp-Did";
        public static final String DIGEST_ALGORITHM = "X-Payload-Digest-Alg";
        public static final String PAYLOAD_DIGEST = "X-Payload-Digest";
    }

}
