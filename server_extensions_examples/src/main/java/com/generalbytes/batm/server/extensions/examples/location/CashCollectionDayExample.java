package com.generalbytes.batm.server.extensions.examples.location;

import com.generalbytes.batm.server.extensions.ICashCollectionDay;

public class CashCollectionDayExample implements ICashCollectionDay {

    private Integer dayOfMonth;

    public CashCollectionDayExample() {
    }

    @Override
    public Integer getDayOfMonth() {
        return dayOfMonth;
    }
}
