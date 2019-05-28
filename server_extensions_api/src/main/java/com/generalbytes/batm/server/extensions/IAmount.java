package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

public interface IAmount {
    /**
     * Value like 100
     * @return
     */
    BigDecimal getAmount();

    /**
     * Currency symbol for example USD
     * @return
     */
    String getCurrency();
}
