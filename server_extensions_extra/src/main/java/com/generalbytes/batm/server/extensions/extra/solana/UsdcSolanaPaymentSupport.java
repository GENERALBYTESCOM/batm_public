package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.QueryableWalletPaymentSupport;

import java.util.concurrent.TimeUnit;

public class UsdcSolanaPaymentSupport extends QueryableWalletPaymentSupport {
    @Override
    protected String getCryptoCurrency() {
        return CryptoCurrency.USDCSOL.getCode();
    }

    @Override
    protected long getPollingPeriodMillis() {
        return TimeUnit.SECONDS.toMillis(30);
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return TimeUnit.SECONDS.toMillis(60);
    }
}
