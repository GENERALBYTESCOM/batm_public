package com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The result of the verification sent by Veriff to us to the URL configured as Webhook decisions URL in Veriff dashboard.
 */
public class VerificationDecisionWebhookRequest {

    public static boolean matches(Map<String, ?> payload) {
        return payload.containsKey("verification");
    }

    /**
     * Status of the response (the message received, NOT the verification status / decision), "success", "fail"
     */
    public String status;
    public Verification verification;

    public String getApplicantId() {
        return this.verification.id;
    }

    public static class Verification {
        /**
         * UUID v4 which identifies the verification session
         */
        public String id;
        public Verification.Status status;

        public enum Status {
            /**
             * Positive: Person was verified. The verification process is complete. Accessing the sessionURL again will show the client that nothing is to be done here.
             */
            approved,
            /**
             * Negative: Person has not been verified. The verification process is complete. Either it was a fraud case or some other severe reason that the person can not be verified. You should investigate the session further and read the "reason". If you decide to give the client another try you need to create a new session.
             */
            declined,
            /**
             * Resubmitted: Resubmission has been requested. The verification process is not completed. Something was missing from the client and they need to go through the flow once more. The same sessionURL can and should be used for this purpose. And the reference image should be uploaded again (same if related to the face, different one if related to the reference image)
             */
            resubmission_requested,
            /**
             * Negative: Verification has been expired. The verification process is complete. After the session creation, if the end user has not started the verification in 7 days, the session gets expired.
             */
            expired,
            /**
             * Negative: Verification has been abandoned. The verification process is complete. After the session creation, if the end user has started but not completed the verification in 7 days, the session gets abandoned status.
             */
            abandoned;

            public static final Set<Status> complete = Collections.unmodifiableSet(EnumSet.of(approved, declined, expired, abandoned));
        }


        /**
         * e.g. 9001, 9102, 9103, 9104, see https://developers.veriff.com/#response-and-error-codes
         */
        public Integer code;
        /**
         * Reason of failed Verification
         */
        public String reason;
        /**
         * e.g. 102, see https://developers.veriff.com/#response-and-error-codes
         */
        public Integer reasonCode;

        /**
         * Verified person
         */
        public Verification.Person person;

        public static class Person {
            public Verification.Person.Gender gender;

            public enum Gender {M, F}

            /**
             * National Identification number
             */
            public String idNumber;
            public String lastName;
            public String firstName;
            /**
             * YYYY-MM-DD
             */
            public String dateOfBirth;
            public String nationality;
            /**
             * YYYY
             */
            public String yearOfBirth;
            public String placeOfBirth;
            public String pepSanctionMatch;

        }

        /**
         * Verified document
         */
        public Verification.Document document;

        public static class Document {
            public Verification.Document.Type type;

            public enum Type {PASSPORT, ID_CARD, DRIVERS_LICENSE, RESIDENCE_PERMIT, OTHER}

            /**
             * Document number
             */
            public String number;
            /**
             * ISO-2
             */
            public String country;
            /**
             * YYYY-MM-DD
             */
            public String validFrom;
            public String validUntil;
        }

        /**
         * echoed from create session request.
         * identity public ID in our case
         */
        public String vendorData;
        public String decisionTime;
        /**
         * Timestamp of the session generation
         */
        public String acceptanceTime;
        public Verification.AdditionalVerifiedData additionalVerifiedData;

        public static class AdditionalVerifiedData {
            // depending on integration
        }

        /**
         * Optional array of risk labels related to the session. The presence of this property depends on risk labels being enabled for the integration
         */
        List<Verification.RiskLabel> riskLabels;

        public static class RiskLabel {
            public String label;
            /**
             * one of client_data_mismatch, crosslinks, device, document, images, network, session, person
             */
            public String category;
        }
    }

    public TechnicalData technicalData;

    public static class TechnicalData {
        /**
         * IP of the device from which the verification was made
         */
        public String ip;
    }
}
