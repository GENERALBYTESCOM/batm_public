package com.generalbytes.batm.server.extensions.extra.ethereum.erc20.bizz;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class BizzDefinition extends CryptoCurrencyDefinition{
    private IPaymentSupport paymentSupport = new BizzPaymentSupport();

    public BizzDefinition() {
        super(CryptoCurrency.BIZZ.getCode(), "BIZZ ERC20 Token", "ethereum","https://bizzcoin.com/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
