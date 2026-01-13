package com.generalbytes.batm.server.extensions.payment.external;

import com.generalbytes.batm.common.currencies.FiatCurrency;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Represents an external payment request containing payment and transaction identifications and amount details.
 */
@Data
public class ExternalPaymentRequest {

    /**
     * Not null unique identifier (UUID) of the payment. Used to reference the payment.
     *
     */
    private String externalPaymentId;

    /**
     * Not null transaction identifier (RID).
     */
    private String remoteTransactionId;

    /**
     * Not null payment amount in fiat currency.
     */
    private BigDecimal fiatAmount;

    /**
     * Not null fiat currency of the payment amount.
     */
    private FiatCurrency fiatCurrency;
}
