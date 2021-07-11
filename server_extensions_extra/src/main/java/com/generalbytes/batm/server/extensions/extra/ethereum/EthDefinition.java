package com.generalbytes.batm.server.extensions.extra.ethereum;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class EthDefinition extends CryptoCurrencyDefinition{
    private final IPaymentSupport paymentSupport = new EthPaymentSupport();

    public EthDefinition() {
        super(CryptoCurrency.ETH.getCode(), "Ether ETH", "ethereum", "https://ethereum.org/en/eth/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }
}
