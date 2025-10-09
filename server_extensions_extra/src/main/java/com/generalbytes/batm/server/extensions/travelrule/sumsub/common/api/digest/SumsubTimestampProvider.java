package com.generalbytes.batm.server.extensions.travelrule.sumsub.common.api.digest;

import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.RestInvocation;

import java.time.Instant;

/**
 * The SumsubTimestampProvider class provides a simple implementation of the ParamsDigest interface.
 * This implementation generates a signature based on the current Unix epoch timestamp in seconds.
 * It is designed to be used as a lightweight timestamp-based digest mechanism.
 *
 * <p>Note: This class does not utilize any secret keys or cryptographic hashes,
 * and should only be used in scenarios where a timestamp-based signature is sufficient.
 */
public class SumsubTimestampProvider implements ParamsDigest {
    @Override
    public String digestParams(RestInvocation restInvocation) {
        return String.valueOf(Instant.now().getEpochSecond());
    }
}
