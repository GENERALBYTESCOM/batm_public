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
package com.generalbytes.batm.server.extensions.extra.examples;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ITerminalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

/* Uncomment this extension class in batm-extensions.xml */
public class ProfitOverrideExtension extends AbstractExtension implements ITerminalListener {
    Logger log = LoggerFactory.getLogger(ProfitOverrideExtension.class);

    @Override
    public String getName() {
        return "BATM Example extension that overrides terminal profit";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        ctx.addTerminalListener(this);
    }

    @Override
    public BigDecimal overrideProfitBuy(String serialNumber, String cryptoCurrency, BigDecimal profitBuy) {
        if (LocalDate.now().getDayOfWeek() == DayOfWeek.MONDAY) {
            if (!CryptoCurrency.LTC.getCode().equals(cryptoCurrency)) {
                return profitBuy.add(new BigDecimal("2.5"));
            }
        }
        return profitBuy;
    }

    @Override
    public BigDecimal overrideProfitSell(String serialNumber, String cryptoCurrency, BigDecimal profitSell) {
        if (LocalDate.now().getDayOfWeek() == DayOfWeek.MONDAY) {
            return profitSell.add(new BigDecimal("1.75"));
        }
        return profitSell;
    }
}
