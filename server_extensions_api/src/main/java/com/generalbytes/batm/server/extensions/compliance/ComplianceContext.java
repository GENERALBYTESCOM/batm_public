package com.generalbytes.batm.server.extensions.compliance;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.TransactionPaymentType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The values a compliance decision is taken for.
 */
@ToString
@EqualsAndHashCode
public class ComplianceContext {

    private final long complianceTierId;
    private final TransferType transferType;
    private final TransactionPaymentType paymentType;
    private final String fiatCurrency;

    /**
     * @param complianceTierId the tier the customer is transacting on. Get the available tier IDs from
     *                         {@link IExtensionContext#getComplianceTiers()}
     * @param transferType     whether the customer is buying or selling
     * @param paymentType      how the customer is paying
     * @param fiatCurrency     the fiat currency of the transaction
     */
    public ComplianceContext(long complianceTierId, TransferType transferType, TransactionPaymentType paymentType, String fiatCurrency) {
        if (complianceTierId < 1) {
            throw new IllegalArgumentException("complianceTierId must be a positive number");
        }
        this.complianceTierId = complianceTierId;
        this.transferType = requireNotNull(transferType, "transferType");
        this.paymentType = requireNotNull(paymentType, "paymentType");
        this.fiatCurrency = requireNotBlank(fiatCurrency, "fiatCurrency");
    }

    /**
     * @return the tier the customer is transacting on
     */
    public long getComplianceTierId() {
        return complianceTierId;
    }

    /**
     * @return whether the customer is buying or selling
     */
    public TransferType getTransferType() {
        return transferType;
    }

    /**
     * @return how the customer is paying
     */
    public TransactionPaymentType getPaymentType() {
        return paymentType;
    }

    /**
     * @return the fiat currency of the transaction
     */
    public String getFiatCurrency() {
        return fiatCurrency;
    }

    private static <T> T requireNotNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value;
    }

    private static String requireNotBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value;
    }
}
