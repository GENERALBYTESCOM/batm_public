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
package com.generalbytes.batm.server.extensions.extra.examples.chat;

import com.generalbytes.batm.server.extensions.AbstractExtension;

import java.util.HashSet;
import java.util.Set;

public class ChatBotExtensionExample extends AbstractExtension {
    @Override
    public String getName() {
        return  "BATM Example extension that demonstrates how to write ChatBot commands";
    }

    @Override
    public Set<Class> getChatCommands() {
        Set<Class> result = new HashSet<>();
        result.add(VersionCommand.class);
        result.add(InfoCommand.class);
        return result;
    }
}
