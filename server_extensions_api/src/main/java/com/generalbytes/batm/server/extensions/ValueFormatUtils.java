package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;

public class ValueFormatUtils {

    public static String formatFiat(BigDecimal amount) {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#######.##",formatSymbols);
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setNegativePrefix("-");
        return df.format(amount);
    }

    public static String formatCrypto(BigDecimal amount) {
        if (amount == null) {
            return "null"; // Event data
        }
        if (BigDecimal.ZERO.compareTo(amount) == 0) {
            return "0";
        }
        return amount.stripTrailingZeros().toPlainString();
    }

    public static String capitalizeFirstLetters(String text) {
        if (text == null) {
            return "";
        }
        String[] words = text.split(" ");
        List<String> newWords = new LinkedList<>();
        for (String word : words) {
            newWords.add((word.length() > 0) ? Character.toUpperCase(word.charAt(0)) + word.substring(1) : "");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < newWords.size(); i++) {
            sb.append(newWords.get(i));
            if (i + 1 < newWords.size()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String logMsg(String methodName) {
        return logMsg(methodName, null);
    }

    public static String logMsg(String methodName, String message) {
        StringBuilder sb = new StringBuilder();
        if (methodName != null) {
            sb.append(methodName);
            if (message != null) {
                sb.append(" - ").append(message);
            }
        } else if (message != null) {
            sb.append(message);
        }

        return sb.toString();
    }
}

