package com.generalbytes.batm.server.extensions.extra.ethereum.erc20.btbs;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class BtbsDefinition extends CryptoCurrencyDefinition{
    private IPaymentSupport paymentSupport = new BtbsPaymentSupport();

    public BtbsDefinition() {
        super(CryptoCurrency.BTBS.getCode(), "BTBS ERC20 Token", "ethereum","https://bitbase.es/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
