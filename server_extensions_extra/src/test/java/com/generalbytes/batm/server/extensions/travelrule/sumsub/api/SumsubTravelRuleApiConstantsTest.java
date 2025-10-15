package com.generalbytes.batm.server.extensions.travelrule.sumsub.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumsubTravelRuleApiConstantsTest {

    @Test
    void testConstants() {
        assertEquals("travelRule", SumsubTravelRuleApiConstants.TRAVEL_RULE_REQUEST_TYPE);
        assertEquals("crypto", SumsubTravelRuleApiConstants.TransactionInfo.CRYPTO_CURRENCY_TYPE);
        assertEquals("out", SumsubTravelRuleApiConstants.TransactionInfo.OUT_DIRECTION);
        assertEquals("individual", SumsubTravelRuleApiConstants.IdentityInfo.INDIVIDUAL_TYPE);
        assertEquals("crypto", SumsubTravelRuleApiConstants.PaymentMethod.CRYPTO_TYPE);
        assertEquals("applicantKytTxnApproved", SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_TXN_APPROVED);
        assertEquals("applicantKytTxnRejected", SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_TXN_REJECTED);
        assertEquals("applicantKytOnHold", SumsubTravelRuleApiConstants.WebhookType.APPLICANT_KYT_ON_HOLD);
        assertEquals("HMAC_SHA512_HEX", SumsubTravelRuleApiConstants.DigestAlgorithm.SHA_512);
        assertEquals("Vasp-Did", SumsubTravelRuleApiConstants.HttpHeaderParam.VASP_DID);
        assertEquals("X-Payload-Digest-Alg", SumsubTravelRuleApiConstants.HttpHeaderParam.DIGEST_ALGORITHM);
        assertEquals("X-Payload-Digest", SumsubTravelRuleApiConstants.HttpHeaderParam.PAYLOAD_DIGEST);
    }

}