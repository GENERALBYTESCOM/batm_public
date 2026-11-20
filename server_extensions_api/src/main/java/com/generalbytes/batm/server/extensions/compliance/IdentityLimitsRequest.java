package com.generalbytes.batm.server.extensions.compliance;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.TransactionPaymentType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Identifies the customer, terminal and currency to calculate identity limits for.
 *
 * <p>Create one with {@link #forComplianceTier} or {@link #forAmlKyc}, depending on the terminal's configuration.
 */
@ToString
@EqualsAndHashCode
public class IdentityLimitsRequest {

    private final String fiatCurrency;
    private final String terminalSerialNumber;
    private final String identityPublicId;
    private final Long complianceTierId;
    private final TransactionPaymentType paymentType;

    private IdentityLimitsRequest(String fiatCurrency,
                                  String terminalSerialNumber,
                                  String identityPublicId,
                                  Long complianceTierId,
                                  TransactionPaymentType paymentType) {
        this.fiatCurrency = requireNotBlank(fiatCurrency, "fiatCurrency");
        this.terminalSerialNumber = requireNotBlank(terminalSerialNumber, "terminalSerialNumber");
        this.identityPublicId = requireNotBlank(identityPublicId, "identityPublicId");
        this.complianceTierId = complianceTierId;
        this.paymentType = paymentType;
    }

    /**
     * For a terminal using a compliance scheme. Get the available tier IDs from {@link IExtensionContext#getComplianceTiers()}.
     *
     * @param paymentType the payment type to calculate for, since compliance limits can differ per payment type
     */
    public static IdentityLimitsRequest forComplianceTier(String fiatCurrency,
                                                          String terminalSerialNumber,
                                                          String identityPublicId,
                                                          long complianceTierId,
                                                          TransactionPaymentType paymentType) {
        if (paymentType == null) {
            throw new IllegalArgumentException("paymentType is required");
        }
        return new IdentityLimitsRequest(fiatCurrency, terminalSerialNumber, identityPublicId, complianceTierId, paymentType);
    }

    /**
     * For a terminal using AML/KYC settings.
     */
    public static IdentityLimitsRequest forAmlKyc(String fiatCurrency, String terminalSerialNumber, String identityPublicId) {
        return new IdentityLimitsRequest(fiatCurrency, terminalSerialNumber, identityPublicId, null, null);
    }

    /**
     * @return the fiat currency to report the limits in
     */
    public String getFiatCurrency() {
        return fiatCurrency;
    }

    /**
     * @return serial number of the terminal whose configuration the limits come from
     */
    public String getTerminalSerialNumber() {
        return terminalSerialNumber;
    }

    /**
     * @return public ID of the customer's identity
     */
    public String getIdentityPublicId() {
        return identityPublicId;
    }

    /**
     * @return the compliance tier to calculate for, or {@code null} for an AML/KYC terminal
     */
    public Long getComplianceTierId() {
        return complianceTierId;
    }

    /**
     * @return the payment type to calculate for, or {@code null} for an AML/KYC terminal
     */
    public TransactionPaymentType getPaymentType() {
        return paymentType;
    }

    private static String requireNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value;
    }
}
