package com.generalbytes.batm.server.extensions.payment.external;

import lombok.Data;

/**
 * Represents an update to an external payment.
 */
@Data
public class ExternalPaymentUpdate {

    /**
     * Unique identifier of the external payment.
     * Can be obtained via {@link IExternalPaymentProvider#initiateExternalPayment(ExternalPaymentRequest)}.
     */
    private String externalPaymentId;

    /**
     * Current status of the payment (SUCCESS, ERROR).
     */
    private ExternalPaymentStatus paymentStatus;

    /**
     * Specific details or notes regarding the update to be stored with the transaction.
     */
    private String updateDetails;
}
