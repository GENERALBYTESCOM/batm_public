package com.generalbytes.batm.server.extensions.extra.ico;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class ICODefinition extends CryptoCurrencyDefinition {
    private final IPaymentSupport paymentSupport = new ICOPaymentSupport();

    public ICODefinition() {
        super(CryptoCurrency.ICO.getCode(), "ICO", "ethereum", "https://betverse.ch/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
