package com.generalbytes.batm.server.extensions.extra.lightningbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class LightningBitcoinDefinition extends CryptoCurrencyDefinition {
    private IPaymentSupport paymentSupport = new LightningBitcoinPaymentSupport();

    public LightningBitcoinDefinition() {
        super(CryptoCurrency.LBTC.getCode(), CryptoCurrency.LBTC.getCurrencyName(), "lightning", "https://lightning.network/");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return paymentSupport;
    }

    @Override
    public String getRateSourceSymbol() {
        return CryptoCurrency.BTC.getCode();
    }
}