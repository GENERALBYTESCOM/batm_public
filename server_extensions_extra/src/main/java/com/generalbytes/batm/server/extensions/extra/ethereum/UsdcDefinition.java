package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class UsdcDefinition extends CryptoCurrencyDefinition{
    private final IPaymentSupport paymentSupport = new UsdcPaymentSupport();

    public UsdcDefinition() {
        super(CryptoCurrency.USDC.getCode(), "USD Coin ERC-20", "ethereum", "https://www.centre.io/usdc");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
