package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

public interface IAmount {

    BigDecimal getAmount();
    String getCurrency();
}
