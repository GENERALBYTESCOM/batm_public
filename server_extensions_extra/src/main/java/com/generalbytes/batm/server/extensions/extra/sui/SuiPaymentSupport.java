package com.generalbytes.batm.server.extensions.extra.sui;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.QueryableWalletPaymentSupport;

import java.util.concurrent.TimeUnit;

public class SuiPaymentSupport extends QueryableWalletPaymentSupport {

    @Override
    protected String getCryptoCurrency() {
        return CryptoCurrency.SUI.getCode();
    }

    @Override
    protected long getPollingPeriodMillis() {
        // SUI has ~500ms finality; 10s polling is conservative and safe
        return TimeUnit.SECONDS.toMillis(10);
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return TimeUnit.SECONDS.toMillis(30);
    }
}
