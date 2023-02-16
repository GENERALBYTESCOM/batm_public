package com.generalbytes.batm.server.extensions.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for accessing individual parameters
 * in a colon- (':') delimited parameter configuration string.
 * Escaping is applied (only) to the delimiter character
 * (e.g. 'user:password:http\://example.com').
 */
public class ExtensionParameters {
    private static final ExtensionParameters empty = new ExtensionParameters(Collections.emptyList());

    private static final String delimiter = ":";
    private static final String escape = "\\";

    // delimiter not preceded by the escape character
    private static final String delimiterRegex = "(?<!" + Pattern.quote(escape) + ")" + Pattern.quote(delimiter);
    private static final String escapedDelimiterRegex = Pattern.quote(escape) + Pattern.quote(delimiter);

    private final List<String> parameters;

    public ExtensionParameters(List<String> parameters) {
        this.parameters = parameters == null ? Collections.emptyList() : parameters;
    }

    public static ExtensionParameters fromDelimited(String delimitedParameters) {
        if (delimitedParameters == null) {
            return empty;
        }
        return new ExtensionParameters(
            // limit -1 means trailing empty strings will not be discarded
            Arrays.stream(delimitedParameters.split(delimiterRegex, -1))
                .map(ExtensionParameters::unescape)
                .collect(Collectors.toList()));
    }

    /**
     * @return All parameters (incl. the prefix) separated by the delimiter.
     * If any parameter value contains the delimiter in it, it will be escaped.
     */
    public String getDelimited() {
        return parameters.stream()
            .map(s -> s == null ? "" : s)
            .map(ExtensionParameters::escape)
            .collect(Collectors.joining(delimiter));
    }

    private static String unescape(String parameter) {
        return parameter.replaceAll(escapedDelimiterRegex, delimiter);
    }

    private static String escape(String parameter) {
        return parameter.replaceAll(delimiter, Matcher.quoteReplacement(escape + delimiter));
    }

    /**
     * @param n 0-based index of the parameter to get.
     *          0 means the parameter before the first colon delimiter (the "prefix" or the provider identification).
     * @return the returned parameter could be an empty string (if configured to be empty)
     * or null if it is not configured
     */
    public String get(int n) {
        if (parameters.size() <= n) {
            return null;
        }
        return parameters.get(n);
    }

    /**
     * @param n            0-based index of the parameter to get
     * @param defaultValue the value returned in case the n-th parameter is not present or does not correspond to any enum value
     * @param <T>          type of the returned enum, defined by the defaultValue parameter
     * @return An enum constant of type T which has a name that matches the n-th parameter value (case-sensitive).
     * The default value is returned if the n-th parameter is not present or if it does not match any enum constant of the given type.
     */
    public <T extends Enum<T>> T get(int n, T defaultValue) {
        Objects.requireNonNull(defaultValue, "defaultValue cannot be null");
        String value = get(n);
        T[] enumConstants = defaultValue.getDeclaringClass().getEnumConstants();
        if (value == null || enumConstants == null) {
            return defaultValue;
        }

        return Arrays.stream(enumConstants)
            .filter(enumConstant -> enumConstant.name().equals(value))
            .findFirst()
            .orElse(defaultValue);
    }

    /**
     * @return the parameter before the first colon delimiter (the "prefix" or the provider identification).
     * Could also be null or an empty string.
     */
    public String getPrefix() {
        return get(0);
    }

    /**
     * @return all parameters including the prefix
     */
    public List<String> getAll() {
        return parameters;
    }

    /**
     * @return parameters without the first one (without the prefix)
     */
    public List<String> getWithoutPrefix() {
        return parameters.size() == 0 ? parameters : parameters.subList(1, parameters.size());
    }
}
