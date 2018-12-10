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

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPerson;
import com.generalbytes.batm.server.extensions.ITerminal;
import com.generalbytes.batm.server.extensions.ITransactionDetails;
import com.generalbytes.batm.server.extensions.chat.AbstractChatCommnad;
import com.generalbytes.batm.server.extensions.chat.ChatCommand;
import com.generalbytes.batm.server.extensions.chat.IConversation;
import com.vdurmont.emoji.EmojiParser;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_BUY_COMPLETED;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_BUY_ERROR;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_BUY_IN_PROGRESS;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_SELL_ERROR;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_SELL_PAYMENT_ARRIVED;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_SELL_PAYMENT_ARRIVING;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_SELL_PAYMENT_REQUESTED;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_WITHDRAW_COMPLETED;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_WITHDRAW_ERROR;
import static com.generalbytes.batm.server.extensions.ITransactionDetails.STATUS_WITHDRAW_IN_PROGRESS;

@ChatCommand( names = {"info","i"},
    help = "/info remoteTransactionId - display transaction information.")
//    help = "/info [balances|terminals [serial number]|remoteTransactionId|identityId] - display various information\n")
public class InfoCommand extends AbstractChatCommnad{

    public boolean processCommand(IExtensionContext ctx, IConversation conversation, String command, StringTokenizer parameters, String commandLine) {
        boolean displayInfoAboutTerminals = false;
        boolean displayInfoAboutBalances = false;
        List<String> serialNumbers = new ArrayList<>();

        if (!parameters.hasMoreTokens()) {
            //info command executed without parameters
            displayInfoAboutBalances = true;
            displayInfoAboutTerminals = true;
        }else {
            String parameter = parameters.nextToken();
            if ("balances".equalsIgnoreCase(parameter) || "balance".equalsIgnoreCase(parameter) || "b".equalsIgnoreCase(parameter)) {
                displayInfoAboutBalances = true;
            }else if ("terminals".equalsIgnoreCase(parameter) || "terminal".equalsIgnoreCase(parameter) || "t".equalsIgnoreCase(parameter)) {
                displayInfoAboutTerminals = true;
                while (parameters.hasMoreTokens()) {
                    String serialNumber = parameters.nextToken();
                    if (serialNumber.startsWith("BT") || serialNumber.startsWith("PS")) {
                        serialNumbers.add(serialNumber);
                    }
                }
            }else if ((parameter.startsWith("R") || (parameter.startsWith("L"))) && parameter.length() == 6) {
                displayTransactionInformation(ctx, conversation, parameter.toUpperCase());
                return true;
            }else if (parameter.startsWith("I") && parameter.length() == 6) {
                displayIdentityInformation(ctx, conversation, parameter.toUpperCase());
                return true;
            }
        }
        if (displayInfoAboutTerminals) {
            displayTerminalsInformation(ctx, conversation, serialNumbers);
            return true;
        }

        if (displayInfoAboutBalances) {
            displayBalanceInformation(ctx, conversation);
            return true;
        }
        conversation.sendText("Invalid parameters. See help for more information.");
        return true;
    }

    private void displayTransactionInformation(IExtensionContext ctx, IConversation conversation, String remoteOrLocalTransactionId) {
        StringBuilder sb = new StringBuilder();
        ITransactionDetails td = ctx.findTransactionByTransactionId(remoteOrLocalTransactionId);
        if (td == null) {
            sb.append("I'm sorry but transaction with id " + remoteOrLocalTransactionId + " was not found.");
        }else {
            //something was found does chat user has permission to se this information?
            IPerson person = ctx.findPersonByChatId(conversation.getSenderUserId());
            if (ctx.hasPersonPermissionToObject(IExtensionContext.PERMISSION_READ, person, td)) {
                sb.append("Transaction details of " + td.getRemoteTransactionId() + ":\n");
                sb.append(formatTransactionDetails(ctx, person, td));
            }
        }
        String response = sb.toString();
        conversation.sendText(response);
    }

    private String formatTransactionDetails(IExtensionContext ctx, IPerson person, ITransactionDetails td) {
        ITerminal terminal = ctx.findTerminalBySerialNumber(td.getTerminalSerialNumber());

        SimpleDateFormat timeFormat = ctx.getTimeFormatByPerson(person);
        String result = emojiByTransactionStatus(td) + " " + timeFormat.format(td.getTerminalTime()) + " " +  td.getTerminalSerialNumber() + " (" + terminal.getName() + ") " + formatTxType(td) + " " + td.getRemoteTransactionId();
        if (td.getType() == ITransactionDetails.TYPE_BUY_CRYPTO) {
            result+=" " + formatFiat(td.getCashAmount()) + " " + td.getCashCurrency() +  " > " + formatCrypto(td.getCryptoAmount()) + " " + td.getCryptoCurrency() + " " + td.getCryptoAddress() + " " + formatTxStatus(td);
        }else if (td.getType() == ITransactionDetails.TYPE_SELL_CRYPTO) {
            result+= " " + formatCrypto(td.getCryptoAmount()) + " " + td.getCryptoCurrency() + " > " + formatFiat(td.getCashAmount()) + " " + td.getCashCurrency() + " " + td.getCryptoAddress() + " " + formatTxStatus(td);
        }else if (td.getType() == ITransactionDetails.TYPE_WITHDRAW_CASH) {
            result+= " "  + formatFiat(td.getCashAmount()) + " " + td.getCashCurrency() + " " + formatTxStatus(td);
            ITransactionDetails td2 = ctx.findTransactionByTransactionId(td.getRelatedRemoteTransactionId());
            if (td2 != null) {
                result+="\nRelated SELL transaction " + td2.getRemoteTransactionId();
                result+=":\n " + formatTransactionDetails(ctx, person, td2);
            }
        }
        return result;

    }

    private String emojiByTransactionStatus(ITransactionDetails td) {

        if (td.getType() == ITransactionDetails.TYPE_BUY_CRYPTO) {
            //Buy states
            switch (td.getStatus()) {
                case STATUS_BUY_IN_PROGRESS:
                    return EmojiParser.parseToUnicode(" :blue_heart:");
                case STATUS_BUY_COMPLETED:
                    return EmojiParser.parseToUnicode(" :green_heart:");
                case STATUS_BUY_ERROR:
                    return EmojiParser.parseToUnicode(" :red_circle:");
            }
        }else if (td.getType() == ITransactionDetails.TYPE_SELL_CRYPTO) {
            //Sell states
            switch (td.getStatus()) {
                case STATUS_SELL_PAYMENT_REQUESTED:
                    return EmojiParser.parseToUnicode(" :red_circle:");
                case STATUS_SELL_PAYMENT_ARRIVING:
                    return EmojiParser.parseToUnicode(" :blue_heart:");
                case STATUS_SELL_ERROR:
                    return EmojiParser.parseToUnicode(" :red_circle:");
                case STATUS_SELL_PAYMENT_ARRIVED:
                    return EmojiParser.parseToUnicode(" :green_heart:");
            }
        }else if (td.getType() == ITransactionDetails.TYPE_WITHDRAW_CASH) {
            //Sell states
            switch (td.getStatus()) {
                case STATUS_WITHDRAW_IN_PROGRESS:
                    return EmojiParser.parseToUnicode(" :blue_heart:");
                case STATUS_WITHDRAW_COMPLETED:
                    return EmojiParser.parseToUnicode(" :green_heart:");
                case STATUS_WITHDRAW_ERROR:
                    return EmojiParser.parseToUnicode(" :red_circle:");
            }
        }
        return " ";

    }

    private String formatTxStatus(ITransactionDetails td) {
        if (td.getType() == ITransactionDetails.TYPE_BUY_CRYPTO) {
            //Buy states
            switch (td.getStatus()) {
                case STATUS_BUY_IN_PROGRESS:
                    return "IN_PROGRESS";
                case STATUS_BUY_COMPLETED:
                    return "COMPLETED";
                case STATUS_BUY_ERROR:
                    return "ERROR";
            }
        }else if (td.getType() == ITransactionDetails.TYPE_SELL_CRYPTO) {
            //Sell states
            switch (td.getStatus()) {
                case STATUS_SELL_PAYMENT_REQUESTED:
                    return "PAYMENT_REQUESTED";
                case STATUS_SELL_PAYMENT_ARRIVING:
                    return "PAYMENT_ARRIVING";
                case STATUS_SELL_ERROR:
                    return "ERROR";
                case STATUS_SELL_PAYMENT_ARRIVED:
                    return "PAYMENT_ARRIVED";
            }
        }else if (td.getType() == ITransactionDetails.TYPE_WITHDRAW_CASH) {
            //Sell states
            switch (td.getStatus()) {
                case STATUS_WITHDRAW_IN_PROGRESS:
                    return "IN_PROGRESS";
                case STATUS_WITHDRAW_COMPLETED:
                    return "COMPLETED";
                case STATUS_WITHDRAW_ERROR:
                    return "ERROR";
            }
        }
        return "UNKNOWN";
    }

    private String formatTxType(ITransactionDetails td) {
        if (td.getType() == ITransactionDetails.TYPE_BUY_CRYPTO) {
            return "BUY";
        } if (td.getType() == ITransactionDetails.TYPE_SELL_CRYPTO) {
            return  "SELL";
        } if (td.getType() == ITransactionDetails.TYPE_WITHDRAW_CASH) {
            return  "WITHDRAW";
        }
        return "UNKNOWN";
    }

    private static String formatFiat(BigDecimal amount) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#######.##",formatSymbols);
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setNegativePrefix("-");
        return df.format(amount);
    }

    private static String formatCrypto(BigDecimal amount) {
        if (amount == null) {
            return "null"; // Event data
        }
        if (BigDecimal.ZERO.compareTo(amount) == 0) {
            return "0";
        }
        return amount.stripTrailingZeros().toPlainString();
    }

    private void displayBalanceInformation(IExtensionContext ctx, IConversation conversation) {
        //TODO:
    }

    private void displayTerminalsInformation(IExtensionContext ctx, IConversation conversation, List<String> serialNumbers) {
        //TODO:
    }

    private void displayIdentityInformation(IExtensionContext ctx, IConversation conversation, String publicIdentityId) {
        //TODO:
    }

}
