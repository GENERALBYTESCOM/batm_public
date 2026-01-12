package com.generalbytes.batm.server.extensions.payment.external;

import lombok.Data;

/**
 * Represents the details of an external payment created using a third-party payment provider.
 * This class typically encapsulates information required to complete the payment, such as the
 * link (URL) to redirect the customer for payment processing.
 */
@Data
public class ExternalPaymentDetails {

    /**
     * URL to redirect the customer to for payment processing.
     */
    private String paymentLink;
}
