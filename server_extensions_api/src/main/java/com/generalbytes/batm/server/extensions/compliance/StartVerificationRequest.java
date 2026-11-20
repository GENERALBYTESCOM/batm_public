package com.generalbytes.batm.server.extensions.compliance;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Identifies the customer to start an identity verification for.
 */
@ToString
@EqualsAndHashCode
public class StartVerificationRequest {

    private final String identityPublicId;
    private final String messageToCustomer;
    private final ComplianceContext complianceContext;

    private StartVerificationRequest(String identityPublicId, String messageToCustomer, ComplianceContext complianceContext) {
        if (identityPublicId == null || identityPublicId.isBlank()) {
            throw new IllegalArgumentException("identityPublicId cannot be null or blank");
        }
        this.identityPublicId = identityPublicId;
        this.messageToCustomer = messageToCustomer;
        this.complianceContext = complianceContext;
    }

    /**
     * Create a request with compliance context.
     *
     * @param identityPublicId  public ID of the identity to start verification for
     * @param messageToCustomer the message the customer is sent with the verification link, or {@code null} for
     *                          the default text. May contain a {@code '{link}'} placeholder to be replaced by
     *                          the link
     * @param complianceContext what the verification result is evaluated against once it comes back
     */
    public static StartVerificationRequest of(String identityPublicId, String messageToCustomer, ComplianceContext complianceContext) {
        if (complianceContext == null) {
            throw new IllegalArgumentException("complianceContext is required");
        }
        return new StartVerificationRequest(identityPublicId, messageToCustomer, complianceContext);
    }

    /**
     * Create a request.
     *
     * @param identityPublicId  public ID of the identity to start verification for
     * @param messageToCustomer the message the customer is sent with the verification link, or {@code null} for
     *                          the default text. May contain a {@code '{link}'} placeholder to be replaced by
     *                          the link
     */
    public static StartVerificationRequest of(String identityPublicId, String messageToCustomer) {
        return new StartVerificationRequest(identityPublicId, messageToCustomer, null);
    }

    /**
     * @return public ID of the identity to verify
     */
    public String getIdentityPublicId() {
        return identityPublicId;
    }

    /**
     * @return the message the customer is sent with the verification link, or {@code null} for the default text.
     * May contain a {@code '{link}'} placeholder to be replaced by the link
     */
    public String getMessageToCustomer() {
        return messageToCustomer;
    }

    /**
     * @return what the verification result is evaluated against, or {@code null} when the transaction the
     * customer is about to make is not known
     */
    public ComplianceContext getComplianceContext() {
        return complianceContext;
    }
}
