package com.generalbytes.batm.server.services.amlkyc.verification.veriff.api;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CreateIdentityVerificationSessionRequest {

    public Verification verification;

    static class Verification {
        /**
         * redirect (callback) URL to which the user will be sent after completing web verification flow
         * (usually it is "Thank you" or "Waiting" page).
         * If callback URL is not specified for the session (this value), user will be redirected to Integration's default Callback URL,
         * which can be set up in Veriff Station (Integrations -> select an integration -> Settings -> Callback URL).
         * The callback does not contain any decision or verification information yet.
         */
        public String callback;
        public Verification.Person person;
        /**
         * restricts the accepted documents
         */
        public Verification.Document document;
        /**
         * Vendor specific data string, max 400 characters long, will be sent back unmodified using webhooks
         */
        public String vendorData;
        /**
         * Combined ISO 8601 date and time in UTC (YYYY-MM-DDTHH:MM:S+Timezone Offset|Z, i.e., 2018-04-18T11:02:05.261Z)
         */
        public String timestamp;

        static class Person {
            /**
             * In case the Given name / Last name / Vendor Data or all of them are known, they can be passed to the SDK,
             * therefore text input fields will not be rendered.
             */
            public String firstName;
            public String lastName;
            public String idNumber;
            public String gender;
            /**
             * YYYY-MM-DD
             */
            public String dateOfBirth;
        }

        static class Document {
            public String number;
            /**
             * ISO 2-char code
             */
            public String country;
            public Verification.Document.Type type;

            enum Type {PASSPORT, ID_CARD, DRIVERS_LICENSE, RESIDENCE_PERMIT}
        }
    }

    public static CreateIdentityVerificationSessionRequest create(String vendorData) {
        CreateIdentityVerificationSessionRequest r = new CreateIdentityVerificationSessionRequest();
        r.verification = new Verification();
        r.verification.vendorData = vendorData;
        r.verification.timestamp = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        return r;
    }
}
