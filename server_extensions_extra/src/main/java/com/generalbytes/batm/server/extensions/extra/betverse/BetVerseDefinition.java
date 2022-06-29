package com.generalbytes.batm.server.extensions.extra.betverse;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class BetVerseDefinition extends CryptoCurrencyDefinition {
    private final IPaymentSupport paymentSupport = new BetVersePaymentSupport();

    public BetVerseDefinition() {
        super(CryptoCurrency.BetVerse.getCode(), "BetVerse", "ethereum", "https://betverse.ch/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
