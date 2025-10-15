package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto.transactioninfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Object containing info about Sumsub payment method.
 * Used in {@link SumsubCounterparty}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubPaymentMethod {
    private String type;
    /**
     * The crypto address.
     */
    private String accountId;
    /**
     * Destination tag used in some cryptocurrencies to identify a specific address, for example XRP (Ripple) or XLM (Stellar).
     * Optional parameter.
     */
    private String memo;
}
