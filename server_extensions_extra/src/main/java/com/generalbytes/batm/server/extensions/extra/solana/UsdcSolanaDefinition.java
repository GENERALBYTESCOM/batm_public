package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.CryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

public class UsdcSolanaDefinition extends CryptoCurrencyDefinition {
    private final UsdcSolanaPaymentSupport usdcSolanaPaymentSupport = new UsdcSolanaPaymentSupport();

    public UsdcSolanaDefinition() {
        super(CryptoCurrency.USDCSOL.getCode(), "USDC (Solana SPL)", "solana", "https://usdc.com");
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return usdcSolanaPaymentSupport;
    }
}
