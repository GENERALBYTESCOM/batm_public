/* ##
# Part of the SmartCash API price extension
#
# Copyright 2018 dustinface
# Created 29.04.2018
#
* This software may be distributed and modified under the terms of the GNU
* General Public License version 2 (GPL2) as published by the Free Software
* Foundation and appearing in the file GPL2.TXT included in the packaging of
* this file. Please note that GPL2 Section 2[b] requires that all works based
* on this software must also be made publicly available under the terms of
* the GPL2 ("Copyleft").
#
## */

package com.generalbytes.batm.server.extensions.extra.smartcash.sources.smartcash;

import java.math.BigDecimal;

public class APIResponse {

    public BigDecimal count;
    public Item[] items;
    public Last last;
    public String resource;
    public BigDecimal status;
    public BigDecimal execution;

    public static class Item {
        public String updated;
        public Currencies currencies;
    }

    public static class Currencies {
        public BigDecimal USD;
        public BigDecimal EUR;
        public BigDecimal CHF;
    }

    public class Last {
        public String id;
        public String created;
    }

    public BigDecimal getPrice(String fiatCurrency) {

        if (fiatCurrency.equalsIgnoreCase("USD")) {
            return this.items[0].currencies.USD;
        }else if (fiatCurrency.equalsIgnoreCase("EUR")) {
            return this.items[0].currencies.EUR;
        }else if (fiatCurrency.equalsIgnoreCase("CHF")) {
            return this.items[0].currencies.CHF;
        }

        return null;
    }
}
