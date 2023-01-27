package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api;

public class CreateIdentityVerificationSessionResponse {
    public String status; // "success", "fail"? not sure about the others
    public Verification verification;

    public String getApplicantId() {
        return verification.id;
    }

    public static class Verification {
        /**
         * String UUID v4 which identifies the verification session
         */
        public String id;
        /**
         * URL of the verification to which the person is redirected (Combination of the baseUrl and sessionToken)
         */
        public String url;
        public String sessionToken;
        public String baseUrl;

        @Override
        public String toString() {
            return "Verification{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
        }
    }

    @Override
    public String toString() {
        return "CreateIdentityVerificationSessionResponse{" +
            "status='" + status + '\'' +
            ", verification=" + verification +
            '}';
    }
}
