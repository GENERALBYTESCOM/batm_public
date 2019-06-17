package com.generalbytes.batm.server.extensions.extra.lightningbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;

public class LightningBitcoinDefinition extends CryptoCurrencyDefinition {
//    private IPaymentSupport paymentSupport = new LitecoinPaymentSupport();

    public LightningBitcoinDefinition() {
        super(CryptoCurrency.LBTC.getCode(), CryptoCurrency.LBTC.getCurrencyName(), "lightning", "https://lightning.network/");
    }
// implement for sell
//    @Override
//    public IPaymentSupport getPaymentSupport() {
//        return paymentSupport;
//    }

    @Override
    public String getRateSourceSymbol() {
        return CryptoCurrency.BTC.getCode();
    }
}