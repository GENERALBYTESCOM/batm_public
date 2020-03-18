package com.generalbytes.batm.server.extensions.extra.ethereum.erc20.ftt;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class FttDefinition extends CryptoCurrencyDefinition{
    private IPaymentSupport paymentSupport = new FttPaymentSupport();

    public FttDefinition() {
        super(CryptoCurrency.FTT.getCode(), "FTX ERC20 Token", "ethereum","https://ftx.com/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
