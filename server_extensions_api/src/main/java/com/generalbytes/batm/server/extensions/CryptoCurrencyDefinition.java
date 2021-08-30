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
package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.server.extensions.payment.IPaymentSupport;

/**
 * This class is used to define cryptocurrency
 */
public class CryptoCurrencyDefinition implements ICryptoCurrencyDefinition {
    private String symbol;
    private String name;
    private String protocol;
    private String authorWebsiteURL;
    private boolean requiresTag;

    public CryptoCurrencyDefinition(String symbol, String name, String protocol, String authorWebsiteURL) {
        this.symbol = symbol;
        this.name = name;
        this.protocol = protocol;
        this.authorWebsiteURL = authorWebsiteURL;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getRateSourceSymbol() {
        return getSymbol();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAuthorWebsiteURL() {
        return authorWebsiteURL;
    }

    @Override
    public IPaymentSupport getPaymentSupport() {
        return null;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public boolean isRequiresTag() {
        return requiresTag;
    }
}
