package com.generalbytes.batm.server.extensions.extra.ethereum.erc20.usdt;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class UsdtDefinition extends CryptoCurrencyDefinition{
    private IPaymentSupport paymentSupport = new UsdtPaymentSupport();

    public UsdtDefinition() {
        super(CryptoCurrency.USDT.getCode(), "USDT Stablecoin ERC20 Token", "ethereum","https://tether.to/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
