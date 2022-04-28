package com.generalbytes.batm.server.extensions.exceptions.helper;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionHelper {

    private static final Pattern pattern = Pattern.compile("BT\\d{6}$");

    /**
     * @return serial number of terminal from exception stack trace.
     */
    public static String findSerialNumberInStackTrace() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
            .map(StackTraceElement::getClassName)
            .map(pattern::matcher)
            .filter(Matcher::find)
            .map(Matcher::group)
            .findFirst()
            .orElse(null);
    }
}
