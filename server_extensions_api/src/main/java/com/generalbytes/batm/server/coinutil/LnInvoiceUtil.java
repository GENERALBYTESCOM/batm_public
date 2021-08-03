package com.generalbytes.batm.server.coinutil;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LnInvoiceUtil {

    private static final Pattern invoicePattern = Pattern.compile("lnbc((?<amount>\\d+)(?<multiplier>[munp])?)?1[^1\\s]+");

    /**
     * Parses invoice amount according to
     * https://github.com/lightningnetwork/lightning-rfc/blob/master/11-payment-encoding.md#human-readable-part
     * @return invoice amount in bitcoins, zero if the invoice has no amount
     * @throws RuntimeException if invoice format is incorrect
     */
    public BigDecimal getAmount(String invoice) {
        try {
            Bech32.decodeUnlimitedLength(invoice); // checksum must match
        } catch (AddressFormatException e) {
            throw new IllegalArgumentException("Cannot decode invoice", e);
        }

        Matcher matcher = invoicePattern.matcher(invoice);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Failed to match HRP pattern");
        }

        String amountGroup = matcher.group("amount");
        String multiplierGroup = matcher.group("multiplier");

        if (amountGroup == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal amount = new BigDecimal(amountGroup);
        if (multiplierGroup == null) {
            return amount;
        }

        if (multiplierGroup.equals("p") && amountGroup.charAt(amountGroup.length() - 1) != '0') {
            throw new IllegalArgumentException("sub-millisatoshi amount");
        }

        return amount.multiply(multiplier(multiplierGroup));

    }


    private BigDecimal multiplier(String multiplier) {
        switch (multiplier) {
            case "m": // milli
                return new BigDecimal("0.001");
            case "u": // micro
                return new BigDecimal("0.000001");
            case "n": // nano
                return new BigDecimal("0.000000001");
            case "p": // pico
                return new BigDecimal("0.000000000001");
            default:
                throw new IllegalArgumentException("Invalid multiplier: " + multiplier);
        }
    }

    /**
     * Finds LN invoice in the provided input string and returns it.
     * For example for input = "aaa bbb lnbc1xxx ccc" it will return "lnbc1xxx"
     * It will only return the first invoice found in the input.
     *
     * @return the invoice if it was found. null for null input or if no invoice is found
     */
    public String findInvoice(String input) {
        if (input == null) {
            return null;
        }

        Matcher matcher = invoicePattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
