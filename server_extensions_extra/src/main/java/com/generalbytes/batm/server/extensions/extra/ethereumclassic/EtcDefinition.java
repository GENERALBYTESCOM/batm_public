package com.generalbytes.batm.server.extensions.extra.ethereumclassic;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class EtcDefinition extends CryptoCurrencyDefinition{
    private final IPaymentSupport paymentSupport = new EtcPaymentSupport();

    public EtcDefinition() {
        super(CryptoCurrency.ETC.getCode(), "Ethereum Classic", "ethclassic", "http://ethereumclassic.org/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
