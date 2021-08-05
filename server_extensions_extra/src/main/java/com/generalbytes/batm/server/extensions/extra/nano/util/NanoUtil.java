package com.generalbytes.batm.server.extensions.extra.nano.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Oczadly
 */
public class NanoUtil {

    public static final NanoUtil NANO = new NanoUtil(30, "nano", "nano", "xrb");

    /*
     * Allows basic validation of address, omitting URI scheme and query arguments if provided.
     * Group 1: URI (optional), 2: prefix, 3: pubkey + checksum
     */
    private static final Pattern ADDR_PATTERN =
            Pattern.compile("^(?:(\\w+):)?(\\w+)_([13][13456789abcdefghijkmnopqrstuwxyz]{59})(?:\\?.*)?$");

    private final int unitExp;
    private final String uriScheme, defaultPrefix;
    private final String[] allPrefixes;
    private final BigDecimal maxUnitVal;

    /**
     * @param unitExp       1 unit = 10<sup>x</sup> raw, {@code x} being this value
     * @param addrUriScheme the uri scheme (eg: {@code nano})
     * @param addrPrefixes  a set of address prefixes, the first being the default (eg: {@code xrb})
     */
    public NanoUtil(int unitExp, String addrUriScheme, String... addrPrefixes) {
        this.unitExp = unitExp;
        this.uriScheme = addrUriScheme;
        this.defaultPrefix = addrPrefixes[0];
        this.allPrefixes = addrPrefixes;
        this.maxUnitVal = BigDecimal.valueOf(2).pow(128).subtract(BigDecimal.ONE).movePointLeft(unitExp);
    }


    public String parseAddress(String addr) {
        Matcher m = ADDR_PATTERN.matcher(addr.trim().toLowerCase());
        if (!m.matches())
            throw new IllegalArgumentException("Invalid address \"" + addr + "\" (regex doesn't match).");
        if (m.group(1) != null && !m.group(1).equalsIgnoreCase(uriScheme))
            throw new IllegalArgumentException("Invalid address \"" + addr + "\" (unsupported URI scheme).");
        String prefix = m.group(2);
        if (Arrays.stream(allPrefixes).noneMatch(prefix::equalsIgnoreCase))
            throw new IllegalArgumentException("Invalid address \"" + addr + "\" (unsupported prefix).");

        return defaultPrefix + "_" + m.group(3);
    }

    public void validateAmount(BigDecimal amount) {
        if (amount.stripTrailingZeros().scale() > unitExp)
            throw new IllegalArgumentException("Invalid amount (too many decimal places).");
        if (amount.compareTo(maxUnitVal) > 0)
            throw new IllegalArgumentException("Invalid amount (value too large).");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Invalid amount (value is negative).");
    }

    public BigInteger amountToRaw(BigDecimal amount) {
        validateAmount(amount);
        return amount.movePointRight(unitExp).toBigIntegerExact();
    }

    public BigDecimal amountFromRaw(BigInteger raw) {
        BigDecimal amount = new BigDecimal(raw).movePointLeft(unitExp);
        validateAmount(amount);
        return amount;
    }

}
