/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ILightningChannel;
import com.generalbytes.batm.server.extensions.ILightningWallet;
import com.generalbytes.batm.server.extensions.chat.AbstractChatCommnad;
import com.generalbytes.batm.server.extensions.chat.ChatCommand;
import com.generalbytes.batm.server.extensions.chat.IConversation;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ChatCommand(names = {"lightning", "ln"},
    help = "/lightning - display various information about lightning network")
public class LightningCommand extends AbstractChatCommnad {
    private static final Logger log = LoggerFactory.getLogger(LightningCommand.class);

    public boolean processCommand(IExtensionContext ctx, IConversation conversation, String command, StringTokenizer parameters, String commandLine) {
        displayBalanceInformation(ctx, conversation);
        return true;
    }


    private void displayBalanceInformation(IExtensionContext ctx, IConversation conversation) {
        List<String> resultingTerminalSerials = ChatUtils.getTerminals(ctx, ctx.findPersonByChatId(conversation.getSenderUserId()));

        if (resultingTerminalSerials.isEmpty()) {
            conversation.sendText("I'm sorry but no terminals in your organization were found. So no information can be shown.");
            return;
        }
        try {
            conversation.sendText("Please wait");

            Map<String, ? extends ILightningChannel> channels = ctx.findCryptoConfigurationsByTerminalSerialNumbers(resultingTerminalSerials).stream()
                .filter(conf -> CryptoCurrency.LBTC.getCode().equals(conf.getCryptoCurrency()))
                .flatMap(conf -> Stream.of(conf.getBuyWallet(), conf.getSellWallet()))
                .filter(Objects::nonNull)
                .filter(w -> w instanceof ILightningWallet)
                .flatMap(w -> ((ILightningWallet) w).getChannels().stream())
                .collect(Collectors.toMap(ILightningChannel::getShortChannelId, c -> c, (o, o2) -> o));

            channels.values().forEach(c -> conversation.sendText(getChannelInfo(c)));

            Long totalCapacityMsat = (channels.values().stream()
                .filter(ILightningChannel::isOnline)
                .map(ILightningChannel::getCapacityMsat)
                .reduce(0l, Long::sum));

            Long totalReceiveMsat = channels.values().stream()
                .filter(ILightningChannel::isOnline)
                .map(ILightningChannel::getBalanceMsat)
                .reduce(0l, Long::sum);

            Long totalSendMsat = channels.values().stream()
                .filter(ILightningChannel::isOnline)
                .map(c -> c.getCapacityMsat() - c.getBalanceMsat())
                .reduce(0l, Long::sum);

            conversation.sendText("" +
                "Σ online capacity:      " + formatMsat(totalCapacityMsat) + "\n" +
                "Σ online can receive: " + formatMsat(totalReceiveMsat) + "\n" +
                "Σ online can send:     " + formatMsat(totalSendMsat));

        } catch (Exception e) {
            log.error("", e);
            conversation.sendText(EmojiParser.parseToUnicode(" :no_entry: failed to retrieve\n"));
        }

    }

    private String getChannelInfo(ILightningChannel c) {
        return getChannelIcons(c) + c.getShortChannelId() + "\n" +
            "      " + getNodes(c) + "\n" +
            "        - capacity:      " + formatMsat(c.getCapacityMsat()) + "\n" +
            "        - can receive: " + formatMsat(c.getCapacityMsat() - c.getBalanceMsat()) + "\n" +
            "        - can send:     " + formatMsat(c.getBalanceMsat()) + "\n";
    }

    private String getNodes(ILightningChannel c) {
        String local = c.getLocalNodeAlias() != null ? c.getLocalNodeAlias() : formatNodeId(c.getLocalNodeId());
        String remote = c.getRemoteNodeAlias() != null ? c.getRemoteNodeAlias() : formatNodeId(c.getRemoteNodeId());
        String to = EmojiParser.parseToUnicode(" :arrow_right: ");
        return c.isLocalFunder() ? local + to + remote : remote + to + local;
    }

    private String formatNodeId(String id) {
        int ending = 4;
        int beginning = 6;
        return (id == null || id.length() < beginning + ending) ? id : id.substring(0, beginning) + "…" + id.substring(id.length() - ending);
    }

    private String getChannelIcons(ILightningChannel c) {
        String online = c.isOnline() ? ":zap:" : ":small_red_triangle_down:";
        String funder = c.isLocalFunder() ? ":moneybag:" : "";
        return EmojiParser.parseToUnicode(online + funder);
    }

    protected String formatMsat(Long amountMsat) {
        return new BigDecimal(amountMsat).movePointLeft(3 + 8).setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString() + " BTC";
    }

}
