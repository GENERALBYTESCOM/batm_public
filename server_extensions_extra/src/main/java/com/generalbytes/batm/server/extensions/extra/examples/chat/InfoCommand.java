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

import com.generalbytes.batm.server.extensions.IBanknoteCounts;
import com.generalbytes.batm.server.extensions.ICryptoConfiguration;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.ILocation;
import com.generalbytes.batm.server.extensions.IPerson;
import com.generalbytes.batm.server.extensions.ITerminal;
import com.generalbytes.batm.server.extensions.ITransactionDetails;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.chat.AbstractChatCommnad;
import com.generalbytes.batm.server.extensions.chat.ChatCommand;
import com.generalbytes.batm.server.extensions.chat.IConversation;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static com.generalbytes.batm.server.extensions.ITransactionDetails.*;

@ChatCommand( names = {"info","i"},
    help = "/info [balances|terminals|cash [serial number]|remoteTransactionId|identityId|phonenumber] - display various information.")
public class InfoCommand extends AbstractChatCommnad{
    private static final Logger log = LoggerFactory.getLogger(InfoCommand.class);

    //TODO: See status of last 50 transactions.
    public static final String PERMISSION_DENIED = "Permission denied. We are sorry, but you don't have access to this information.";

    public boolean processCommand(IExtensionContext ctx, IConversation conversation, String command, StringTokenizer parameters, String commandLine) {
        boolean displayInfoAboutTerminals = false;
        boolean displayInfoAboutBalances = false;
        boolean displayInfoAboutCash = false;
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
                serialNumbers = readSerialNumbers(parameters);
            }else if ((parameter.startsWith("R") || parameter.startsWith("r") || parameter.startsWith("L") || parameter.startsWith("l")) && parameter.length() == 5+1) {
                displayTransactionInformation(ctx, conversation, parameter.toUpperCase());
                return true;
            }else if ((parameter.startsWith("I") || parameter.startsWith("i")) && parameter.length() == 15+1) {
                displayIdentityInformation(ctx, conversation, parameter.toUpperCase(), null);
                return true;
            }else if (isPhoneNumber(parameter)) {
                displayIdentityInformation(ctx, conversation, null, parsePhoneNumber(parameter));
                return true;
            } else if ("cash".equalsIgnoreCase(parameter) || "c".equalsIgnoreCase(parameter)) {
                displayInfoAboutCash = true;
                serialNumbers = readSerialNumbers(parameters);
            }
        }
        if (displayInfoAboutTerminals || displayInfoAboutBalances || displayInfoAboutCash) {
            if (displayInfoAboutTerminals) {
                displayTerminalsInformation(ctx, conversation, serialNumbers);
            }

            if (displayInfoAboutBalances) {
                displayBalanceInformation(ctx, conversation);
            }

            if (displayInfoAboutCash) {
                displayTerminalCashInformation(ctx, conversation, serialNumbers);
            }

            return true;
        }else {
            conversation.sendText("Invalid parameters. See help for more information.");
        }
        return true;
    }

    private List<String> readSerialNumbers(StringTokenizer parameters) {
        List<String> serialNumbers = new ArrayList<>();
        while (parameters.hasMoreTokens()) {
            String serialNumber = parameters.nextToken();
            if (serialNumber.startsWith("BT") || serialNumber.startsWith("PS") || serialNumber.startsWith("bt") || serialNumber.startsWith("ps")) {
                serialNumbers.add(serialNumber);
            }
        }
        return serialNumbers;
    }

    private void displayTerminalCashInformation(IExtensionContext ctx, IConversation conversation, List<String> serialNumbers) {
        StringBuilder sb = new StringBuilder();
        final String cashBoxNameText = "    Cash Box Name: ";
        final String totalText = "    Total:                      ";
        final String currencyText = "      Currency:             ";
        final String denominationText = "        Denomination: ";
        final String countText = "        Count:                ";
        final String newLine = "\n";
        for (String serialNumber : serialNumbers) {
            ITerminal terminal = ctx.findTerminalBySerialNumber(serialNumber);
            if (terminal != null) {
                List<IBanknoteCounts> cashBoxes = ctx.getCashBoxes(serialNumber);
                if (cashBoxes != null) {
                    cashBoxes.sort((t1, t2) -> t1.getCashboxName().compareTo(t2.getCashboxName()));
                    Map<String, List<IBanknoteCounts>> sortedCashBoxes = cashBoxes.stream().collect(Collectors.groupingBy(IBanknoteCounts::getCashboxName));
                    sb.append(serialNumber).append(" (").append(terminal.getName()).append(")").append(newLine);
                    for (String s : sortedCashBoxes.keySet()) {
                        sb.append(cashBoxNameText).append(sortedCashBoxes.get(s).get(0).getCashboxName()).append(newLine);
                        Map<String, BigDecimal> finalTotals = new HashMap<>();
                        for (IBanknoteCounts cashBox : sortedCashBoxes.get(s)) {
                            if (finalTotals.get(cashBox.getCurrency()) == null) {
                                sb.append(currencyText).append(cashBox.getCurrency()).append(newLine);
                            }
                            sb.append(denominationText);
                            sb.append(cashBox.getDenomination().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()).append(newLine);
                            sb.append(countText).append(cashBox.getCount()).append(newLine);
                            BigDecimal subTotal = cashBox.getDenomination().multiply(new BigDecimal(String.valueOf(cashBox.getCount())));
                            BigDecimal finalTotal = finalTotals.get(cashBox.getCurrency());
                            if (finalTotal == null) {
                                finalTotals.put(cashBox.getCurrency(), subTotal);
                            } else {
                                finalTotal = finalTotal.add(subTotal);
                                finalTotals.put(cashBox.getCurrency(), finalTotal);
                            }
                        }
                        for (String currency : finalTotals.keySet()) {
                            sb.append(totalText);
                            sb.append(finalTotals.get(currency).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
                            sb.append(" ").append(currency).append(newLine);
                        }
                    }

                    for (int i = 0; i < 60; i++) {
                        sb.append("-");
                    }
                    sb.append(newLine);
                }
            }
        }
        conversation.sendText(sb.toString());
    }

    private boolean isPhoneNumber(String text) {
        String phonenumber = parsePhoneNumber(text);
        return phonenumber != null;
    }

    private String parsePhoneNumber(String text) {
        text = text.replace(" ", "").replace("-","").replace("\"","");
        try {
            //try to parse number
            long testNumber = Long.parseLong(text.replace("+",""));
            return text;
        } catch (NumberFormatException e) {
        }
        return null;
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
            }else{
                sb.append(PERMISSION_DENIED);
            }
        }
        String response = sb.toString();
        conversation.sendText(response);
    }

    private String formatTransactionDetails(IExtensionContext ctx, IPerson person, ITransactionDetails td) {
        ITerminal terminal = ctx.findTerminalBySerialNumber(td.getTerminalSerialNumber());

        SimpleDateFormat timeFormat = ctx.getTimeFormatByPerson(person);
        String name = terminal.getName();
        if (name == null) {
            name = "";
        }else{
            name = " (" + name + ")";
        }
        String result = emojiByTransactionStatus(td) + " " + timeFormat.format(td.getTerminalTime()) + " " +  td.getTerminalSerialNumber() + name + " "  + formatTxType(td) + " " + td.getRemoteTransactionId();
        if (td.getType() == ITransactionDetails.TYPE_BUY_CRYPTO) {
            result+=" " + formatFiat(td.getCashAmount()) + " " + td.getCashCurrency() +  " > " + formatCrypto(td.getCryptoAmount()) + " " + td.getCryptoCurrency() + " " + td.getCryptoAddress() + " " + formatTxStatus(td);
            if (td.getIdentityPublicId() != null) {
                result+=" " + td.getIdentityPublicId();
            }
        }else if (td.getType() == ITransactionDetails.TYPE_SELL_CRYPTO) {
            result+= " " + formatCrypto(td.getCryptoAmount()) + " " + td.getCryptoCurrency() + " > " + formatFiat(td.getCashAmount()) + " " + td.getCashCurrency() + " " + td.getCryptoAddress() + " " + formatTxStatus(td);
            if (td.getIdentityPublicId() != null) {
                result+=" " + td.getIdentityPublicId();
            }
        }else if (td.getType() == ITransactionDetails.TYPE_WITHDRAW_CASH) {
            result+= " "  + formatFiat(td.getCashAmount()) + " " + td.getCashCurrency() + " " + formatTxStatus(td);
            if (td.getIdentityPublicId() != null) {
                result+=" " + td.getIdentityPublicId();
            }
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
                    return EmojiParser.parseToUnicode(":blue_heart:");
                case STATUS_BUY_COMPLETED:
                    return EmojiParser.parseToUnicode(":green_heart:");
                case STATUS_BUY_ERROR:
                    return EmojiParser.parseToUnicode(":red_circle:");
            }
        }else if (td.getType() == ITransactionDetails.TYPE_SELL_CRYPTO) {
            //Sell states
            switch (td.getStatus()) {
                case STATUS_SELL_PAYMENT_REQUESTED:
                    return EmojiParser.parseToUnicode(":red_circle:");
                case STATUS_SELL_PAYMENT_ARRIVING:
                    return EmojiParser.parseToUnicode(":blue_heart:");
                case STATUS_SELL_ERROR:
                    return EmojiParser.parseToUnicode(":red_circle:");
                case STATUS_SELL_PAYMENT_ARRIVED:
                    return EmojiParser.parseToUnicode(":green_heart:");
            }
        }else if (td.getType() == ITransactionDetails.TYPE_WITHDRAW_CASH) {
            //Sell states
            switch (td.getStatus()) {
                case STATUS_WITHDRAW_IN_PROGRESS:
                    return EmojiParser.parseToUnicode(":blue_heart:");
                case STATUS_WITHDRAW_COMPLETED:
                    return EmojiParser.parseToUnicode(":green_heart:");
                case STATUS_WITHDRAW_ERROR:
                    return EmojiParser.parseToUnicode(":red_circle:");
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
                    return getErrorMessage(td);
            }
        }else if (td.getType() == ITransactionDetails.TYPE_SELL_CRYPTO) {
            //Sell states
            switch (td.getStatus()) {
                case STATUS_SELL_PAYMENT_REQUESTED:
                    return "PAYMENT_REQUESTED";
                case STATUS_SELL_PAYMENT_ARRIVING:
                    return "PAYMENT_ARRIVING";
                case STATUS_SELL_ERROR:
                    return getErrorMessage(td);
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
                    return getErrorMessage(td);
            }
        }
        return "UNKNOWN";
    }

    private String getErrorMessage(ITransactionDetails td) {
        if (td.getType() == ITransactionDetails.TYPE_BUY_CRYPTO) {
            String error = "";
            switch (td.getErrorCode()) {
                case BUY_ERROR_NO_ERROR:
                    error = "NO ERROR";
                    break;
                case BUY_ERROR_INVALID_PARAMETERS:
                    error = "INVALID PARAMETERS";
                    break;
                case BUY_ERROR_INVALID_CURRENCY:
                    error = "INVALID CURRENCY";
                    break;
                case BUY_ERROR_INVALID_BALANCE:
                    error = "INVALID BALANCE";
                    break;
                case BUY_ERROR_INVALID_UNKNOWN_ERROR:
                    error = "INVALID UNKNOWN ERROR";
                    break;
                case BUY_ERROR_PROBLEM_SENDING_FROM_HOT_WALLET:
                    error = "PROBLEM SENDING FROM HOT WALLET";
                    break;
                case BUY_ERROR_PROBLEM_GETTING_BALANCE_FROM_HOT_WALLET:
                    error = "PROBLEM GETTING BALANCE FROM HOT WALLET";
                    break;
                case BUY_ERROR_PROBLEM_GETTING_BALANCE_FROM_EXCHANGE:
                    error = "PROBLEM GETTING BALANCE FROM EXCHANGE";
                    break;
                case BUY_ERROR_EXCHANGE_WITHDRAWAL:
                    error = "EXCHANGE WITHDRAWAL";
                    break;
                case BUY_ERROR_EXCHANGE_PURCHASE:
                    error = "EXCHANGE PURCHASE";
                    break;
                case BUY_ERROR_UNKNOWN_EXCHANGE_STRATEGY:
                    error = "UNKNOWN EXCHANGE STRATEGY";
                    break;
                case BUY_ERROR_CONFIGURATION_PROBLEM:
                    error = "CONFIGURATION PROBLEM";
                    break;
                case BUY_ERROR_FINGERPRINT_UNKNOWN:
                    error = "FINGERPRINT UNKNOWN";
                    break;
                case BUY_ERROR_FEE_GREATER_THAN_AMOUNT:
                    error = "FEE GREATER THAN AMOUNT";
                    break;
                case BUY_ERROR_PUBLIC_ID_UNKNOWN:
                    error = "PUBLIC ID UNKNOWN";
                    break;
                case BUY_ERROR_NOT_APPROVED:
                    error = "NOT APPROVED";
                    break;
                default:
                    error = "UNKNOWN";
                    break;
            }
            return "ERROR (" + error + ")";
        } else if (td.getType() == ITransactionDetails.TYPE_SELL_CRYPTO) {
            String error = "";
            switch (td.getErrorCode()) {
                case SELL_ERROR_NO_ERROR:
                    error = "NO ERROR";
                    break;
                case SELL_ERROR_INVALID_PARAMETERS:
                    error = "INVALID PARAMETERS";
                    break;
                case SELL_ERROR_INVALID_CURRENCY:
                    error = "INVALID CURRENCY";
                    break;
                case SELL_ERROR_INVALID_BALANCE:
                    error = "INVALID BALANCE";
                    break;
                case SELL_ERROR_INVALID_UNKNOWN_ERROR:
                    error = "INVALID UNKNOWN ERROR";
                    break;
                case SELL_ERROR_CONFIGURATION_PROBLEM:
                    error = "CONFIGURATION PROBLEM";
                    break;
                case SELL_ERROR_FINGERPRINT_UNKNOWN:
                    error = "FINGERPRINT UNKNOWN";
                    break;
                case SELL_ERROR_GETTING_DEPOSIT_ADDRESS:
                    error = "GETTING DEPOSIT ADDRESS";
                    break;
                case SELL_ERROR_PAYMENT_WAIT_TIMED_OUT:
                    error = "PAYMENT WAIT TIMED OUT";
                    break;
                case SELL_ERROR_NOT_ENOUGH_COINS_ON_EXCHANGE:
                    error = "COINS UNCONFIRMED ON EXCHANGE";
                    break;
                case SELL_ERROR_EXCHANGE_SELL:
                    error = "EXCHANGE SELL";
                    break;
                case SELL_ERROR_PAYMENT_INVALID:
                    error = "INVALID PAYMENT";
                    break;
                case SELL_ERROR_NOT_APPROVED:
                    error = "NOT APPROVED";
                    break;
                default:
                    error = "UNKNOWN";
                    break;
            }
            return "ERROR (" + error + ")";
        } else if (td.getType() == ITransactionDetails.TYPE_WITHDRAW_CASH) {
            String error= "";
            switch (td.getErrorCode()) {
                case WITHDRAW_ERROR_NO_ERROR:
                    error = "NO ERROR";
                    break;
                case WITHDRAW_ERROR_INVALID_PARAMETERS:
                    error = "INVALID PARAMETERS";
                    break;
                case WITHDRAW_ERROR_INVALID_CURRENCY:
                    error = "INVALID CURRENCY";
                    break;
                case WITHDRAW_ERROR_INVALID_UNKNOWN_ERROR:
                    error = "INVALID UNKNOWN ERROR";
                    break;
                case WITHDRAW_ERROR_FINGERPRINT_UNKNOWN:
                    error = "FINGERPRINT UNKNOWN";
                    break;
                case WITHDRAW_ERROR_NOT_ENOUGH_CASH:
                    error = "NOT ENOUGH CASH";
                    break;
                case WITHDRAW_ERROR_PHONE_NUMBER_UNKNOWN:
                    error = "PHONE NUMBER UNKNOWN";
                    break;
                case WITHDRAW_ERROR_NOT_APPROVED:
                    error = "NOT APPROVED";
                    break;
                default:
                    error = "UNKNOWN";
                    break;
            }
            return "ERROR (" + error + ")";
        }
        return "ERROR";
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
    private void displayTerminalsInformation(IExtensionContext ctx, IConversation conversation, List<String> serialNumbers) {
        List<ITerminal> foundTerminals = new ArrayList<>();
        if (serialNumbers == null || serialNumbers.isEmpty()) {
            //all terminals.
            foundTerminals.addAll(ctx.findAllTerminals());
        }else{
            for (int i = 0; i < serialNumbers.size(); i++) {
                String serialNumber = serialNumbers.get(i);
                ITerminal t = ctx.findTerminalBySerialNumber(serialNumber);
                foundTerminals.add(t);
            }
        }
        IPerson person = ctx.findPersonByChatId(conversation.getSenderUserId());
        List<ITerminal> resultingTerminals = new ArrayList<>();
        for (int i = 0; i < foundTerminals.size(); i++) {
            ITerminal terminal = foundTerminals.get(i);
            if (ctx.hasPersonPermissionToObject(IExtensionContext.PERMISSION_READ, person, terminal)) {
                if (terminal.isActive()) {
                    resultingTerminals.add(terminal);
                }else{
                    //do not show terminals that are not marked as active.
                }
            }
        }

        if (resultingTerminals.isEmpty()) {
            conversation.sendText("I'm sorry but no terminals were found.");
            return;
        }else{
            //sort terminals by serial number
            Collections.sort(resultingTerminals, new Comparator<ITerminal>() {
                @Override
                public int compare(ITerminal t1, ITerminal t2) {
                    return t1.getSerialNumber().compareTo(t2.getSerialNumber());
                }
            });

            StringBuilder sb = new StringBuilder();
            sb.append("Status of terminals:\n");
            for (int i = 0; i < resultingTerminals.size(); i++) {
                ITerminal terminal = resultingTerminals.get(i);
                sb.append(emojiByTerminalStatus(terminal) + " " + terminal.getSerialNumber());
                String name = terminal.getName();
                if (name != null) {
                    sb.append(" (" + name + ")");
                }
                ILocation location = terminal.getLocation();
                if (location != null) {
                    sb.append(" " + location.getName());
                }
                sb.append("\n");
            }
            conversation.sendText(sb.toString());
        }
    }

    private String emojiByTerminalStatus(ITerminal terminal) {
        Date lastPingAt = terminal.getLastPingAt();
        if (lastPingAt != null) {
            if (lastPingAt.getTime() + (60000) > System.currentTimeMillis()) {
                if (terminal.getErrors() == 0) {
                    return EmojiParser.parseToUnicode(":green_heart:");
                } else {
                    return EmojiParser.parseToUnicode(":triangular_flag_on_post:");
                }
            } else {
                // Not online (very old ping)
                return EmojiParser.parseToUnicode(":red_circle:");
            }
        } else {
            //never pinged terminal
            return EmojiParser.parseToUnicode(":large_blue_circle:");
        }
    }
    private void displayBalanceInformation(IExtensionContext ctx, IConversation conversation) {
        List<String> resultingTerminalSerials = ChatUtils.getTerminals(ctx, ctx.findPersonByChatId(conversation.getSenderUserId()));

        if (resultingTerminalSerials.isEmpty()) {
            conversation.sendText("I'm sorry but no terminals in your organization were found. So no balance can be shown.");
            return;
        }else{
            conversation.sendText("Please wait...");
            List<ICryptoConfiguration> ccs = ctx.findCryptoConfigurationsByTerminalSerialNumbers(resultingTerminalSerials);
            Collections.sort(ccs, new Comparator<ICryptoConfiguration>() {
                @Override
                public int compare(ICryptoConfiguration c1, ICryptoConfiguration c2) {
                    return c1.getCryptoCurrency().compareTo(c2.getCryptoCurrency());
                }
            });
            StringBuilder sb = new StringBuilder();
            sb.append("Cryptoconfigurations are following:\n");
            for (int i = 0; i < ccs.size(); i++) {
                ICryptoConfiguration c = ccs.get(i);
                sb.append(EmojiParser.parseToUnicode(":money_with_wings:") + c.getName() + ":\n");
                try {
                    IWallet wallet = c.getBuyWallet();
                    if (wallet != null) {
                        BigDecimal cryptoBalance = wallet.getCryptoBalance(c.getCryptoCurrency());
                        if (cryptoBalance != null) {
                            sb.append(" Buy Wallet: " + cryptoBalance + " " + c.getCryptoCurrency() + " " + wallet.getCryptoAddress(c.getCryptoCurrency()) + "\n");
                        }
                    }
                    wallet = c.getSellWallet();
                    if (wallet != null) {
                        BigDecimal cryptoBalance = wallet.getCryptoBalance(c.getCryptoCurrency());
                        if (cryptoBalance != null) {
                            sb.append(" Sell Wallet: " + cryptoBalance + " " + c.getCryptoCurrency() + " " + wallet.getCryptoAddress(c.getCryptoCurrency()) + "\n");
                        }
                    }
                    IExchange exchange = c.getBuyExchange();
                    if (exchange != null) {
                        BigDecimal cryptoBalance = exchange.getCryptoBalance(c.getCryptoCurrency());
                        BigDecimal fiatBalance = exchange.getFiatBalance(exchange.getPreferredFiatCurrency());
                        if (cryptoBalance != null) {
                            sb.append(" Buy Exchange: " + cryptoBalance + " " + c.getCryptoCurrency() + "\n");
                        }
                        if (cryptoBalance != null) {
                            sb.append(" Buy Exchange: " + fiatBalance + " " + exchange.getPreferredFiatCurrency() + "\n");
                        }
                        sb.append(" Buy Exchange: " + exchange.getDepositAddress(c.getCryptoCurrency()) + "\n");
                    }

                    exchange = c.getSellExchange();
                    if (exchange != null) {
                        BigDecimal cryptoBalance = exchange.getCryptoBalance(c.getCryptoCurrency());
                        BigDecimal fiatBalance = exchange.getFiatBalance(exchange.getPreferredFiatCurrency());
                        if (cryptoBalance != null) {
                            sb.append(" Sell Exchange: " + cryptoBalance + " " + c.getCryptoCurrency() + "\n");
                        }
                        if (fiatBalance != null) {
                            sb.append(" Sell Exchange: " + fiatBalance + " " + exchange.getPreferredFiatCurrency() + "\n");
                        }
                        sb.append(" Sell Exchange: " + exchange.getDepositAddress(c.getCryptoCurrency()) + "\n");
                    }
                } catch (Exception e) {
                    sb.append(" "+EmojiParser.parseToUnicode(":no_entry:")+" failed to retrieve\n");
                    log.error("", e);
                }
            }
            conversation.sendText(sb.toString());
        }
    }

    private void displayIdentityInformation(IExtensionContext ctx, IConversation conversation, String publicIdentityId, String phoneNumber) {
        IIdentity identity = null;
        if (publicIdentityId != null) {
            identity = ctx.findIdentityByIdentityId(publicIdentityId);
            if (identity == null) {
                conversation.sendText("Identity was not found.");
            }
        } else if (phoneNumber != null) {
            IPerson person = ctx.findPersonByChatId(conversation.getSenderUserId());
            List<IIdentity> identities = ctx.findIdentitiesByPhoneNumber(phoneNumber, person.getContactCountryIso2());
            if (identities == null) {
                conversation.sendText("Identity was not found by phone number.");
            } else {
                identity = identities.get(0);
            }
        }
        if (identity != null) {
            IPerson person = ctx.findPersonByChatId(conversation.getSenderUserId());
            if (ctx.hasPersonPermissionToObject(IExtensionContext.PERMISSION_READ, person, identity)) {
                formatAndSendIdentityDetails(ctx, conversation, person, identity);
            } else{
                conversation.sendText(PERMISSION_DENIED);
            }
        }
    }

    private static String emptyStringIfNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }
    private static String formatDateTime(SimpleDateFormat sdf, Date date) {
        if (date == null) {
            return "";
        }else{
            return sdf.format(date);
        }
    }

    private void formatAndSendIdentityDetails(IExtensionContext ctx, IConversation conversation, IPerson person, IIdentity identity) {
        StringBuilder sb = new StringBuilder();
        if (identity != null) {
            SimpleDateFormat timeFormat = ctx.getTimeFormatByPerson(person);
            sb.append("Id: " + identity.getPublicId() + "\n");
            sb.append("Status: " + formatIdentityStatus(identity.getState()) + "\n");
            sb.append("Created on terminal: " + identity.getCreatedByTerminalSerialNumber() + "\n");
            sb.append("Created at: " + formatDateTime(timeFormat, identity.getCreated()) + "\n");
            sb.append("Last updated at: " + formatDateTime(timeFormat, identity.getLastUpdatedAt()) + "\n");
            sb.append("Watchlist scanned at: " + formatDateTime(timeFormat, identity.getWatchListLastScanAt()) + "\n");
            sb.append("Note: " + emptyStringIfNull(identity.getNote()) +"\n");
            List<IIdentityPiece> identityPieces = identity.getIdentityPieces();
            if (identityPieces != null) {
                for (int i = 0; i < identityPieces.size(); i++) {
                    IIdentityPiece p = identityPieces.get(i);
                    switch (p.getPieceType()) {
                        case IIdentityPiece.TYPE_CELLPHONE:
                            sb.append("Cellphone: " + emptyStringIfNull(p.getPhoneNumber()) +"\n");
                            break;
                        case IIdentityPiece.TYPE_EMAIL:
                            sb.append("Email: " + emptyStringIfNull(p.getEmailAddress()) +"\n");
                            break;
                        case IIdentityPiece.TYPE_PERSONAL_INFORMATION:
                            sb.append("Created at: " + formatDateTime(timeFormat, p.getCreated()) + "\n");
                            sb.append("First name: " + emptyStringIfNull(p.getFirstname()) + "\n");
                            sb.append("Last name: " + emptyStringIfNull(p.getLastname()) + "\n");
                            sb.append("Document Number: " + emptyStringIfNull(p.getIdCardNumber()) + "\n");
                            sb.append("Address: " + emptyStringIfNull(p.getContactAddress()) + "\n");
                            sb.append("City: " + emptyStringIfNull(p.getContactCity()) + "\n");
                            sb.append("ZIP: " + emptyStringIfNull(p.getContactZIP()) + "\n");
                            sb.append("Country: " + emptyStringIfNull(p.getContactCountry()) + "\n");
                            break;
                        case IIdentityPiece.TYPE_CAMERA_IMAGE:
                            if (p.getData() != null) {
                                conversation.sendPhoto("Camera " + formatDateTime(timeFormat, p.getCreated()), new ByteArrayInputStream(p.getData()));
                            }
                            break;
                        case IIdentityPiece.TYPE_ID_SCAN:
                            if (p.getData() != null) {
                                conversation.sendPhoto("Document " + formatDateTime(timeFormat, p.getCreated()), new ByteArrayInputStream(p.getData()));
                            }
                            break;
                        case IIdentityPiece.TYPE_SELFIE:
                            if (p.getData() != null) {
                                conversation.sendPhoto("Selfie " + formatDateTime(timeFormat, p.getCreated()), new ByteArrayInputStream(p.getData()));
                            }
                            break;
                    }
                }
            }
            conversation.sendText(sb.toString());
        }
    }

    private String formatIdentityStatus(int state) {
        switch (state){
            case IIdentity.STATE_ANONYMOUS:
                return "ANONYMOUS";
            case IIdentity.STATE_NOT_REGISTERED:
                return "NOT REGISTERED";
            case IIdentity.STATE_PROHIBITED:
                return "PROHIBITED";
            case IIdentity.STATE_REGISTERED:
                return "REGISTERED";
            case IIdentity.STATE_TO_BE_REGISTERED:
                return "AWAITING REGISTRATION";
            case IIdentity.STATE_PROHIBITED_TO_BE_REGISTERED:
                return "REJECTED WANTS TO BE REGISTERED";
            case IIdentity.STATE_TO_BE_VERIFIED:
                return "AWAITING VERIFICATION";
        }
        return "UNKNOWN";
    }

}
