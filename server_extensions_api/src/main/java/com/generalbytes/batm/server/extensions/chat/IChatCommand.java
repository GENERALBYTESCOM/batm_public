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
package com.generalbytes.batm.server.extensions.chat;

import com.generalbytes.batm.server.extensions.IExtensionContext;

import java.util.StringTokenizer;

public interface IChatCommand {
    /**
     * This method is called to process command received by chat bot
     * @param ctx
     * @param conversation - use this object to send messages back or to find out details about sender
     * @param command - command string used in message (first word)
     * @param parameters - parsed parameters (next words used after command)
     * @param commandLine - full unparsed message
     * @return
     */
    boolean processCommand(IExtensionContext ctx, IConversation conversation, String command, StringTokenizer parameters, String commandLine);
}
