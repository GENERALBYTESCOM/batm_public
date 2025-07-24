package com.generalbytes.batm.server.extensions.aml.verification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object for overriding verification web URL.
 */
@Setter
@Getter
@AllArgsConstructor
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
     * <li>{@code null} if unknown (e.g., old invoked from old terminal)</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_BUY_CRYPTO}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_SELL_CRYPTO}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_WITHDRAW_CASH}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_CASHBACK}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_ORDER_CRYPTO}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.ITransactionPreparation#TYPE_DEPOSIT_CASH}</li>
     */
    private Integer transactionType;

    /**
     * Specifies the direction of the related transaction.
     * The value is an integer where each number corresponds to a specific direction:
     * <li>{@code null} if unknown (e.g., old invoked from old terminal)</li>
     * <li>{@link com.generalbytes.batm.server.extensions.IExtensionContext#DIRECTION_NONE}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.IExtensionContext#DIRECTION_BUY_CRYPTO}</li>
     * <li>{@link com.generalbytes.batm.server.extensions.IExtensionContext#DIRECTION_SELL_CRYPTO}</li>
     */
    private Integer direction;

    /**
     * The identity public ID.
     */
    private String identityPublicId;

    /**
     * Terminal serial number. Not always available, might be null in case it is not invoked by terminal (e.g., by an extension).
     */
    private String serialNumber;

}
