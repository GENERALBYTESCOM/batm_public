package com.generalbytes.batm.server.extensions.extra.tron;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class UsdttronDefinition extends CryptoCurrencyDefinition {
    private final IPaymentSupport paymentSupport = new UsdttronPaymentSupport();

    public UsdttronDefinition() {
        super(CryptoCurrency.USDTTRON.getCode(), "Tether USDT TRC-20 (TRON)", "tron", "https://tron.network/usdt");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
