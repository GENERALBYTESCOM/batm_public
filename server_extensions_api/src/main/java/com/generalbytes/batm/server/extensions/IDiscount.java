package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;
import java.util.Date;

public interface IDiscount {
    public long getOrganizationId();

    public String getCode();

    public BigDecimal getBuyDiscount();

    public BigDecimal getSellDiscount();

    public Date getValidityFrom();

    public Date getValidityTill();

    public Long getMaximumUsages();

    public Long getMaximumUsagesPerIdentity();

    public BigDecimal getMinimumTransactionAmount();

    public BigDecimal getTotalLimit();

    public String getFiatCurrency();

    public Boolean isActive();

    public Boolean isDeleted();

    public String getNotes();

    public Date getCreatedAt();

    public String getLinkedIdentityPublicId();
}
