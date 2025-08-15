package com.generalbytes.batm.server.extensions.travelrule.gtr.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class containing API constants for calling endpoints on Global Travel Rule (GTR).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GtrApiConstants {
    /**
     * Callback types.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/callback-api-references/enum/CallbackTypeEnum">Global Travel Rule (GTR) documentation</a>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CallbackType {
        public static final int NETWORK_TEST = 0;
        public static final int PII_VERIFICATION = 4;
        public static final int ADDRESS_VERIFICATION = 6;
        public static final int RECEIVE_TX_ID = 7;
        public static final int TX_VERIFICATION = 9;
    }

    /**
     * Secret types.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SecretType {
        public static final int CURVE_25519 = 1;
    }

    /**
     * Verify PII statuses.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/pii-verify-fields">Global Travel Rule (GTR) documentation</a>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PiiStatus {
        public static final int MATCH = 1;
        public static final int MISMATCH = 2;
    }

    /**
     * Verify statuses.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/api-references/enum/VerifyStatusEnum">Global Travel Rule (GTR) documentation</a>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VerifyStatus {
        public static final int SUCCESS = 100000;
        public static final int CLIENT_BAD_PARAMETERS = 100004;
        public static final int BENEFICIARY_INTERNAL_SERVER_ERROR = 100008;
        public static final int ADDRESS_NOT_FOUND = 200001;
        public static final int PII_VERIFICATION_FAILED = 200003;
        public static final int TX_NOT_FOUND = 200007;
    }

    /**
     * Verify fields.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/pii-verify-fields">Global Travel Rule (GTR) documentation</a>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class VerifyField {
        /**
         * Verify fields for originator.
         */
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Originator {

            /**
             * Verify fields for originator natural person.
             */
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class NaturalPerson {
                public static final String NAME = "100026";
            }

        }

        /**
         * Verify fields for beneficiary.
         */
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Beneficiary {
            /**
             * Verify fields for beneficiary natural person.
             */
            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static class NaturalPerson {
                public static final String NAME = "110026";
            }
        }
    }
}
