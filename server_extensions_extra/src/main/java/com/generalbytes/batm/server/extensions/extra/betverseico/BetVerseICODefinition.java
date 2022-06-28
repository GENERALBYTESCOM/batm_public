package com.generalbytes.batm.server.extensions.extra.betverseico;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class BetVerseICODefinition extends CryptoCurrencyDefinition{
    private final IPaymentSupport paymentSupport = new BetVerseICOPaymentSupport();

    public BetVerseICODefinition() {
        super(CryptoCurrency.BVTOKENATMICO.getCode(), "BETVERSEICO", "ethereum", "https://betverseh.ch/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
