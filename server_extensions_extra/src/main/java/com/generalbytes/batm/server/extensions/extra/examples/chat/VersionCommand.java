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
import com.generalbytes.batm.server.extensions.chat.AbstractChatCommnad;
import com.generalbytes.batm.server.extensions.chat.ChatCommand;
import com.generalbytes.batm.server.extensions.chat.IConversation;
import com.vdurmont.emoji.EmojiParser;

import java.util.StringTokenizer;
@ChatCommand( names = {"version","v"}, help = "/version - display server version")
public class VersionCommand extends AbstractChatCommnad{
    @Override
    public boolean processCommand(IExtensionContext ctx, IConversation conversation, String command, StringTokenizer parameters, String commandLine) {
        conversation.sendText("My " + EmojiParser.parseToUnicode(":computer:") +  " version is " + ctx.getServerVersion() + " thanks for asking.");
        return true;
    }
}
