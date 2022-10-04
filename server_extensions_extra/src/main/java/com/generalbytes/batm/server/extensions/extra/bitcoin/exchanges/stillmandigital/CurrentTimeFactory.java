package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital;

import si.mazi.rescu.SynchronizedValueFactory;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

public class CurrentTimeFactory  implements SynchronizedValueFactory<Long> {

    private static final Clock UTC_CLOCK = Clock.systemUTC();


    @Override
    public Long createValue() {
        return TimeUnit.MILLISECONDS.toSeconds(UTC_CLOCK.millis());
    }
}
