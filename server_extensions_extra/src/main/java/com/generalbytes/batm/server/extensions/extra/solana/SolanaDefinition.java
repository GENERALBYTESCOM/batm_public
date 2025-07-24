package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class SolanaDefinition extends CryptoCurrencyDefinition {
    private final SolanaPaymentSupport solanaPaymentSupport = new SolanaPaymentSupport();

    public SolanaDefinition() {
        super(CryptoCurrency.SOL.getCode(), "Solana", "solana", "https://solana.com");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return solanaPaymentSupport;
    }
}
