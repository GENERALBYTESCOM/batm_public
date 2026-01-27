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

package com.generalbytes.batm.server.extensions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class ExtensionsUtil {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,63}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final Logger log = LoggerFactory.getLogger(ExtensionsUtil.class);

    /**
     * Validates an email address using a standard regex.
     * Replaces the legacy javax.mail.internet.InternetAddress approach.
     */
    public static boolean isValidEmailAddress(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static String getErrorMessage(String body) {
        if(body == null) {
            return "";
        }
        int index1 = body.indexOf("error") + 8;
        int index2 = body.indexOf(",") - 1;
        return body.substring(index1, index2);
    }

    public static String getPrefixWithCountOfParameters(String colonDelimitedParameters) {
        if (colonDelimitedParameters == null) {
            return "null";
        }
        String[] i = colonDelimitedParameters.split(":");
        return i[0] + " + " + (i.length - 1) + " params";
    }

    public static void logExtensionParamsException(String method, String extension, String colonDelimitedParameters, Exception e) {
        if (e instanceof NoSuchElementException) {
            // thrown by StringTokenizer.nextToken() if there are no more tokens in the tokenizer's string.
            // Replace the exception name with a more meaningful message.
            // The exception message does not contain anything useful
            log.warn("{} failed for extension {}, prefix: {}, missing mandatory parameter(s)",
                method, extension, getPrefixWithCountOfParameters(colonDelimitedParameters));

        } else {
            log.warn("{} failed for extension {}, prefix: {}, {}: {} ",
                method, extension, getPrefixWithCountOfParameters(colonDelimitedParameters), e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
