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

package com.generalbytes.batm.server.extensions.extra.examples.chat;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPerson;
import com.generalbytes.batm.server.extensions.ITerminal;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {

    /**
     *
     * @param ctx
     * @param person
     * @return terminal IDs of active terminals that the person can display and are in the same organization
     */
    public static List<String> getTerminals(IExtensionContext ctx, IPerson person) {
        List<ITerminal> foundTerminals = new ArrayList<>(ctx.findAllTerminals());
        List<String> resultingTerminalSerials = new ArrayList<>();
        for (int i = 0; i < foundTerminals.size(); i++) {
            ITerminal terminal = foundTerminals.get(i);
            if (ctx.hasPersonPermissionToObject(IExtensionContext.PERMISSION_READ, person, terminal)) {
                if (terminal.isActive()) {
                    if (ctx.isTerminalFromSameOrganizationAsPerson(terminal.getSerialNumber(), person)) {
                        resultingTerminalSerials.add(terminal.getSerialNumber());
                    }
                } else {
                    //do not show terminals that are not marked as active.
                }
            }
        }
        return resultingTerminalSerials;
    }
}
