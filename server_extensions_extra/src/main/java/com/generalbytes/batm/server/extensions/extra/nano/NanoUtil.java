package com.generalbytes.batm.server.extensions.extra.nano;

import uk.oczadly.karl.jnano.model.NanoAccount;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Oczadly
 */
public class NanoUtil {

    /** Supports parsing of URI addresses */
    private static final Pattern ADDR_PATTERN = Pattern.compile("^(?:nano:)?(\\w+)(?:\\?.+)?$");


    /** Parses an address with the URI, and enforces the nano prefix. */
    public static NanoAccount parseAddress(String addr) {
        return parseAddressRaw(addr).withPrefix("nano"); // Force nano prefix
    }

    /** Parses an address, keeping the specified prefix. */
    public static NanoAccount parseAddressRaw(String addr) {
        Matcher m = ADDR_PATTERN.matcher(addr);
        if (m.matches()) {
            return NanoAccount.parseAddress(m.group(1));
        } else {
            throw new IllegalArgumentException("Couldn't parse address \"" + addr + "\".");
        }
    }

}
