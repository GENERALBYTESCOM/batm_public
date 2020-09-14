package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

public interface IRemainingLimit {
    String getLimitType();
    BigDecimal getAmount();
}
