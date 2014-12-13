package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

/**
 * Created by b00lean on 12/12/14.
 */
public interface IExchangeAdvanced extends IExchange{
    public ITask createPurchaseCoinsTask(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description);
}
