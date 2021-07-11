/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.slp;

import com.generalbytes.batm.server.extensions.extra.common.QueryableWalletPaymentSupport;
import com.generalbytes.batm.server.extensions.payment.IPaymentRequestSpecification;
import com.generalbytes.batm.server.extensions.payment.PaymentRequest;

import java.util.concurrent.TimeUnit;

public class SlpPaymentSupport extends QueryableWalletPaymentSupport {
    private final String cryptoCurrency;
    private final int decimals;

    public SlpPaymentSupport(String cryptoCurrency, int decimals) {
        this.cryptoCurrency = cryptoCurrency;
        this.decimals = decimals;
    }

    @Override
    protected long getPollingPeriodMillis() {
        return TimeUnit.SECONDS.toMillis(60);
    }

    @Override
    protected long getPollingInitialDelayMillis() {
        return TimeUnit.SECONDS.toMillis(60);
    }

    @Override
    public PaymentRequest createPaymentRequest(IPaymentRequestSpecification spec) {
        if (spec.getTotal().stripTrailingZeros().scale() > decimals) {
            throw new IllegalArgumentException(cryptoCurrency + " has " + decimals + " decimals");
        }
        return super.createPaymentRequest(spec);
    }

    @Override
    protected String getCryptoCurrency() {
        return cryptoCurrency;
    }
}
