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
package com.generalbytes.batm.server.extensions.util.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(RateLimiter.class);

    public static final double DEFAULT_PERMITS_PER_SECOND = 0.48; // ~0.5 permits per second is ~2s between calls
    private static final long DEFAULT_TIMEOUT_MILLIS = 60_000;

    private static final Map<Class, com.google.common.util.concurrent.RateLimiter> limiters = new ConcurrentHashMap<>();

    public static void waitForPossibleCall(final Class clazz) throws TimeoutException {
        waitForPossibleCall(clazz, DEFAULT_PERMITS_PER_SECOND, DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * Blocks until a permit for the given class is available if it can be obtained without exceeding the specified timeout,
     * or throws TimeoutException immediately (without waiting) if the permit would not have been granted before the timeout expired.
     * It uses one limiter per class so it is usefull to limit access to resources that have limits per IP address for example,
     * not per user or per API key.
     *
     * @param clazz identifies the limiter used
     * @param permitsPerSecond the rate of the RateLimiter, measured in how many permits become available per second
     * @param timeoutMillis the maximum time to wait for the permit
     * @throws TimeoutException if the permit would not have been granted before the timeout expired
     */
    public static void waitForPossibleCall(final Class clazz, double permitsPerSecond, long timeoutMillis) throws TimeoutException {
        boolean acquired = limiters
            .computeIfAbsent(clazz, aClass -> com.google.common.util.concurrent.RateLimiter.create(permitsPerSecond))
            .tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);
        if (!acquired) {
            throw new TimeoutException("Cannot acquire a permit from rate limiter for: " + clazz);
        }
    }
}
