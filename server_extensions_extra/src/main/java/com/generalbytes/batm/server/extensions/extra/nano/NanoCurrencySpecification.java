package com.generalbytes.batm.server.extensions.extra.nano;

import uk.oczadly.karl.jnano.model.NanoAccount;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Oczadly
 */
public class NanoCurrencySpecification {

    private static final Pattern ADDR_URI_PATTERN = Pattern.compile("^(?:(\\w+):)?(\\w+)(?:\\?.+)?$");

    private final String currency, uri;
    private final String[] prefixes;

    /**
     * @param currency the currency code
     * @param uri      uri protocol
     * @param prefixes supported prefixes, first element is the default
     */
    public NanoCurrencySpecification(String currency, String uri, String... prefixes) {
        this.currency = currency;
        this.uri = uri;
        this.prefixes = prefixes;
    }


    public String getCurrencyCode() {
        return currency;
    }

    public String getAddressPrefix() {
        return prefixes[0];
    }

    public String[] getAddressPrefixes() {
        return prefixes;
    }

    public String getAddressURI() {
        return uri;
    }


    public boolean isPrefixValid(NanoAccount account) {
        return Arrays.stream(prefixes).anyMatch(p -> p.equalsIgnoreCase(account.getPrefix()));
    }

    /** Parses an address with the URI, and enforces the prefix. */
    public NanoAccount parseAddress(String addr) {
        NanoAccount account = parseAddressRaw(addr);
        if (isPrefixValid(account)) {
            return account.withPrefix(getAddressPrefix());
        } else {
            throw new IllegalArgumentException("Unrecognized address prefix.");
        }
    }

    /** Parses an address, keeping the specified prefix. */
    public NanoAccount parseAddressRaw(String addr) {
        Matcher m = ADDR_URI_PATTERN.matcher(addr);
        if (m.matches()) {
            String uri = m.group(1);
            String address = m.group(2);
            if (uri == null || uri.equalsIgnoreCase(this.uri)) {
                return NanoAccount.parseAddress(address);
            } else {
                throw new IllegalArgumentException("Invalid URI scheme.");
            }
        } else {
            throw new IllegalArgumentException("Couldn't parse address \"" + addr + "\".");
        }
    }

    public String toUriAddress(NanoAccount account) {
        return (uri != null ? uri + ":" : "") + account.withPrefix(getAddressPrefix()).toAddress();
    }

}
