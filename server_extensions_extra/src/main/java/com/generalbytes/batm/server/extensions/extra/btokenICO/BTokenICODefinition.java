package com.generalbytes.batm.server.extensions.extra.btokenICO;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class BTokenICODefinition extends CryptoCurrencyDefinition{
    private final IPaymentSupport paymentSupport = new BTokenICOPaymentSupport();

    public BTokenICODefinition() {
        super(CryptoCurrency.BTOKENICO.getCode(), "BTOKEN-ICO", "ethereum", "https://betverseh.ch/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
