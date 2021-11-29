package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class UsdtDefinition extends CryptoCurrencyDefinition{
    private final IPaymentSupport paymentSupport = new UsdtPaymentSupport();

    public UsdtDefinition() {
        super(CryptoCurrency.USDT.getCode(), "Tether USDT ERC-20", "ethereum", "https://tether.to/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
