package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;
import java.util.Date;

public class DiscountSpec {
    private BigDecimal buyDiscount;
    private BigDecimal sellDiscount;
    private BigDecimal buyFixedFeeDiscount;
    private BigDecimal sellFixedFeeDiscount;
    private Date validityFrom;
    private Date validityTill;
    private Long maximumUsages;
    private Long maximumUsagesPerIdentity;
    private BigDecimal minimumTransactionAmount;
    private BigDecimal totalLimit;
    private String fiatCurrency;
    private String code;
    private String linkedIdentityPublicId;
    private boolean firstTransactionOnly;
    private String notes;

    /**
     * Creates Discount Specification.
     *
     * @param buyDiscount
     * @param sellDiscount
     * @param buyFixedFeeDiscount      The percentage discount applied to the fixed fee on purchase transactions
     *                                 A value of 10 would represent a 10% discount on the fixed fee for buying.
     * @param sellFixedFeeDiscount     The percentage discount applied to the fixed fee on sale transactions
     *                                 A value of 10 would represent a 10% discount on the fixed fee for selling.
     * @param validityFrom
     * @param validityTill
     * @param maximumUsages
     * @param maximumUsagesPerIdentity
     * @param minimumTransactionAmount
     * @param totalLimit
     * @param fiatCurrency             in which currency are limit amounts
     * @param firstTransactionOnly
     * @param code                     (Optional) Defined code to be used (upper-cased) or null for code to be generated.
     * @param linkedIdentityPublicId   (Optional) Public ID of an existing identity to be linked to the Discount.
     * @param notes                    (Optional) Notes worth noting.
     */
    public DiscountSpec(BigDecimal buyDiscount,
                        BigDecimal sellDiscount,
                        BigDecimal buyFixedFeeDiscount,
                        BigDecimal sellFixedFeeDiscount,
                        Date validityFrom,
                        Date validityTill,
                        Long maximumUsages,
                        Long maximumUsagesPerIdentity,
                        BigDecimal minimumTransactionAmount,
                        BigDecimal totalLimit,
                        String fiatCurrency,
                        boolean firstTransactionOnly,
                        String code,
                        String linkedIdentityPublicId,
                        String notes) {
        this.buyDiscount = buyDiscount;
        this.sellDiscount = sellDiscount;
        this.buyFixedFeeDiscount = buyFixedFeeDiscount;
        this.sellFixedFeeDiscount = sellFixedFeeDiscount;
        this.validityFrom = validityFrom;
        this.validityTill = validityTill;
        this.maximumUsages = maximumUsages;
        this.maximumUsagesPerIdentity = maximumUsagesPerIdentity;
        this.minimumTransactionAmount = minimumTransactionAmount;
        this.totalLimit = totalLimit;
        this.fiatCurrency = fiatCurrency;
        this.firstTransactionOnly = firstTransactionOnly;
        this.code = code;
        this.linkedIdentityPublicId = linkedIdentityPublicId;
        this.notes = notes;
    }

    /**
     * @deprecated Use first constructor instead.
     * Creates Discount Specification.
     *
     * @param buyDiscount
     * @param sellDiscount
     * @param validityFrom
     * @param validityTill
     * @param maximumUsages
     * @param maximumUsagesPerIdentity
     * @param minimumTransactionAmount
     * @param totalLimit
     * @param fiatCurrency in which currency are limit amounts
     * @param firstTransactionOnly
     * @param code (Optional) Defined code to be used (upper-cased) or null for code to be generated.
     * @param linkedIdentityPublicId (Optional) Public ID of an existing identity to be linked to the Discount.
     * @param notes (Optional) Notes worth noting.
     *
     */
    @Deprecated
    public DiscountSpec(BigDecimal buyDiscount, BigDecimal sellDiscount, Date validityFrom, Date validityTill, Long maximumUsages, Long maximumUsagesPerIdentity, BigDecimal minimumTransactionAmount, BigDecimal totalLimit, String fiatCurrency, boolean firstTransactionOnly, String code, String linkedIdentityPublicId, String notes) {
        this.buyDiscount = buyDiscount;
        this.sellDiscount = sellDiscount;
        this.validityFrom = validityFrom;
        this.validityTill = validityTill;
        this.maximumUsages = maximumUsages;
        this.maximumUsagesPerIdentity = maximumUsagesPerIdentity;
        this.minimumTransactionAmount = minimumTransactionAmount;
        this.totalLimit = totalLimit;
        this.fiatCurrency = fiatCurrency;
        this.firstTransactionOnly = firstTransactionOnly;
        this.code = code;
        this.linkedIdentityPublicId = linkedIdentityPublicId;
        this.notes = notes;
    }

    public BigDecimal getBuyDiscount() {
        return buyDiscount;
    }

    public void setBuyDiscount(BigDecimal buyDiscount) {
        this.buyDiscount = buyDiscount;
    }

    public BigDecimal getSellDiscount() {
        return sellDiscount;
    }

    public void setSellDiscount(BigDecimal sellDiscount) {
        this.sellDiscount = sellDiscount;
    }

    public BigDecimal getBuyFixedFeeDiscount() {
        return buyFixedFeeDiscount;
    }

    public void setBuyFixedFeeDiscount(BigDecimal buyFixedFeeDiscount) {
        this.buyFixedFeeDiscount = buyFixedFeeDiscount;
    }

    public BigDecimal getSellFixedFeeDiscount() {
        return sellFixedFeeDiscount;
    }

    public void setSellFixedFeeDiscount(BigDecimal sellFixedFeeDiscount) {
        this.sellFixedFeeDiscount = sellFixedFeeDiscount;
    }

    public Date getValidityFrom() {
        return validityFrom;
    }

    public void setValidityFrom(Date validityFrom) {
        this.validityFrom = validityFrom;
    }

    public Date getValidityTill() {
        return validityTill;
    }

    public void setValidityTill(Date validityTill) {
        this.validityTill = validityTill;
    }

    public Long getMaximumUsages() {
        return maximumUsages;
    }

    public void setMaximumUsages(Long maximumUsages) {
        this.maximumUsages = maximumUsages;
    }

    public Long getMaximumUsagesPerIdentity() {
        return maximumUsagesPerIdentity;
    }

    public void setMaximumUsagesPerIdentity(Long maximumUsagesPerIdentity) {
        this.maximumUsagesPerIdentity = maximumUsagesPerIdentity;
    }

    public BigDecimal getMinimumTransactionAmount() {
        return minimumTransactionAmount;
    }

    public void setMinimumTransactionAmount(BigDecimal minimumTransactionAmount) {
        this.minimumTransactionAmount = minimumTransactionAmount;
    }

    public BigDecimal getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(BigDecimal totalLimit) {
        this.totalLimit = totalLimit;
    }

    public String getFiatCurrency() {
        return fiatCurrency;
    }

    public void setFiatCurrency(String fiatCurrency) {
        this.fiatCurrency = fiatCurrency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLinkedIdentityPublicId() {
        return linkedIdentityPublicId;
    }

    public void setLinkedIdentityPublicId(String linkedIdentityPublicId) {
        this.linkedIdentityPublicId = linkedIdentityPublicId;
    }

    public boolean isFirstTransactionOnly() {
        return firstTransactionOnly;
    }

    public void setFirstTransactionOnly(boolean firstTransactionOnly) {
        this.firstTransactionOnly = firstTransactionOnly;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
