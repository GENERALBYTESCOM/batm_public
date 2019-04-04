/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.coinutil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DDOSUtils {

    private static final Logger log = LoggerFactory.getLogger(DDOSUtils.class);

    private static final int DEFAULT_CALL_PERIOD_MINIMUM_MILLIS = 2100; // Cannot be called more often than once in 2 seconds

    private static Map<String, Long> calls = new HashMap<>();

    public static long waitForPossibleCall(final Class clazz) {
        return waitForPossibleCall(clazz, DEFAULT_CALL_PERIOD_MINIMUM_MILLIS);
    }

    public static long waitForPossibleCall(final Class clazz, final int callPeriodMinimumMillis) {
        String className = "N/A";
        try {
            className = clazz.getName();
            synchronized (("com.generalbytes.batm.server.coinutil.DDOSUtils.waitForPossibleCall." + className).intern()) {
                Long lastCall = calls.get(className);
                long sleeping = 0;
                if (lastCall != null) {
                    long diff = System.currentTimeMillis() - lastCall;
                    if (diff < callPeriodMinimumMillis) {
                        try {
                            sleeping = callPeriodMinimumMillis - diff;
                            Thread.sleep(sleeping);
                        } catch (InterruptedException e) {
                            log.error("waitForPossibleCall: " + className, e);
                        }
                    }
                }
                calls.put(className, System.currentTimeMillis());
                return sleeping;
            }
        } catch (Throwable e) {
            log.error("waitForPossibleCall: " + className, e);
            return callPeriodMinimumMillis;
        }
    }
}
