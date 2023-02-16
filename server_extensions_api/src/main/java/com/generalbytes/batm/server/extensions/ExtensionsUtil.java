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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.NoSuchElementException;

public class ExtensionsUtil {

    private static final Logger log = LoggerFactory.getLogger(ExtensionsUtil.class);

    /**
     * Check if specified string is valid email address
     * @param email
     * @return
     */
    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
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

    public static void logExtensionParamsException(String label, String colonDelimitedParameters, Exception e) {
        if (e instanceof NoSuchElementException) {
            // thrown by StringTokenizer.nextToken() if there are no more tokens in the tokenizer's string.
            // Replace the exception name with a more meaningful message.
            // The exception message does not contain anything useful
            log.warn("{} failed for prefix: {}, missing mandatory parameter(s)",
                label, getPrefixWithCountOfParameters(colonDelimitedParameters)
            );

        } else {
            log.warn("{} failed for prefix: {}, {}: {} ",
                label, getPrefixWithCountOfParameters(colonDelimitedParameters), e.getClass().getSimpleName(), e.getMessage()
            );
        }
    }
}
