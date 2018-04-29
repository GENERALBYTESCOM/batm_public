/* ##
# Part of the SmartCash API price extension
#
# Copyright 2018 dustinface
# Created 29.04.2018
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.
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
