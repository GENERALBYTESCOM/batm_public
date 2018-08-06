/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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

import com.generalbytes.batm.server.extensions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/* Comment out this extension class in batm-extensions.xml */
public class TransactionExtension extends AbstractExtension implements ITransactionListener {

    private final Map<String, Long> ticketCounters = new HashMap<>(); //each terminal has its counter

    @Override
    public String getName() {
        return "BATM Example extension which respons to creation of transaction an populates data to the ticket.";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        ctx.addTransactionListener(this);
    }

    @Override
    public Map<String, String> onTransactionCreated(ITransactionDetails transactionDetails) {
        boolean incrementTicketId = false;
        if (transactionDetails.getType() == ITransactionDetails.TYPE_BUY_CRYPTO
            && (transactionDetails.getStatus() == ITransactionDetails.STATUS_BUY_IN_PROGRESS || transactionDetails.getStatus() == ITransactionDetails.STATUS_BUY_COMPLETED)) {
            // valid buy
            incrementTicketId = true;
        }
        if (transactionDetails.getType() == ITransactionDetails.TYPE_WITHDRAW_CASH && transactionDetails.getStatus() == ITransactionDetails.STATUS_WITHDRAW_COMPLETED) {
            // valid sell
            incrementTicketId = true;
        }

        String terminalSerialNumber = transactionDetails.getTerminalSerialNumber();
        Long previousValue = ticketCounters.get(terminalSerialNumber);

        Map<String, String> result = new HashMap<>();
        if (incrementTicketId) {
            Long value =  (previousValue == null) ? 1 : (previousValue + 1);
            ticketCounters.put(terminalSerialNumber, value);
            result.put("ticket.counter", "" + value);
        }
        result.put("ticket.previous.counter", ( (previousValue == null) ? "N/A" : "" + previousValue ) ); //result will be stored into database, linked to transdaction record and later be available in ticket template under key ticket.previous.counter
        return result;
    }

    @Override
    public Map<String, String> onTransactionUpdated(ITransactionDetails transactionDetails) {
        Map<String, String> result = new HashMap<>();
        result.put("last.updated.at", "" + System.currentTimeMillis());
        return result;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return null; // no need to define supported cryptocurrencies
    }
}
