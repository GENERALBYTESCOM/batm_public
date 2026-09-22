package com.generalbytes.batm.server.extensions.extra.sui;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class SuiDefinition extends CryptoCurrencyDefinition {

    private final SuiPaymentSupport paymentSupport = new SuiPaymentSupport();

    public SuiDefinition() {
        super(CryptoCurrency.SUI.getCode(), "Sui", "sui", "https://sui.io");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
