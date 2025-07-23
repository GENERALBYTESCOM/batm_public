package com.generalbytes.batm.server.extensions.aml.verification;

/**
 * Request object for overriding verification web URL.
 */
public class OverrideVerificationWebUrlRequest {

    /**
     * The original verification web URL associated with the verification process.
     * This URL is typically provided by the identity verification provider and can be overridden for specific
     * use cases during the verification process.
     */
    private String originalVerificationWebUrl;

    /**
     * Represents the type of related transaction being performed.
     * The value is an integer where each number corresponds to a specific transaction type:
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_BUY_CRYPTO}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_SELL_CRYPTO}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_WITHDRAW_CASH}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_CASHBACK}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_ORDER_CRYPTO}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_DEPOSIT_CASH}</li>
     */
    private int transactionType;

    /**
     * Specifies the direction of the related transaction.
     * The value is an integer where each number corresponds to a specific direction:
     * <li>{@link com.generalbytes.batm.server.extensions.IExtensionContext#DIRECTION_NONE}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.IExtensionContext#DIRECTION_BUY_CRYPTO}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.IExtensionContext#DIRECTION_SELL_CRYPTO}</li>
     */
    private int direction;

    /**
     * The identity public ID.
     */
    private String identityPublicId;

    /**
     * Terminal serial number. Not always available, might be null.
     */
    private String serialNumber;

    public OverrideVerificationWebUrlRequest() {
    }

    public OverrideVerificationWebUrlRequest(String originalVerificationWebUrl, int transactionType, int direction, String identityPublicId, String serialNumber) {
        this.originalVerificationWebUrl = originalVerificationWebUrl;
        this.transactionType = transactionType;
        this.direction = direction;
        this.identityPublicId = identityPublicId;
        this.serialNumber = serialNumber;
    }

    public String getOriginalVerificationWebUrl() {
        return originalVerificationWebUrl;
    }

    public void setOriginalVerificationWebUrl(String originalVerificationWebUrl) {
        this.originalVerificationWebUrl = originalVerificationWebUrl;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getIdentityPublicId() {
        return identityPublicId;
    }

    public void setIdentityPublicId(String identityPublicId) {
        this.identityPublicId = identityPublicId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
