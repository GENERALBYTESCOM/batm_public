package com.generalbytes.batm.server.extensions.extra.ethereum.erc20.dai;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class DaiDefinition extends CryptoCurrencyDefinition{
    private IPaymentSupport paymentSupport = new DaiPaymentSupport();

    public DaiDefinition() {
        super(CryptoCurrency.DAI.getCode(), "DAI Stablecoin ERC20 Token", "ethereum","https://makerdao.com/en/dai/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
