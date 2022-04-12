package com.generalbytes.batm.server.extensions.extra.btoken;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class BTokenDefinition extends CryptoCurrencyDefinition{
    private final IPaymentSupport paymentSupport = new BTokenPaymentSupport();

    public BTokenDefinition() {
        super(CryptoCurrency.BTOKEN.getCode(), "BTOKEN", "ethereum", "https://betverseh.ch/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
