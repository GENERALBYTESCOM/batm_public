package com.generalbytes.batm.server.extensions.examples.location;

import com.generalbytes.batm.server.extensions.IOpeningHours;

import java.util.Date;

public class OpeningHoursExample implements IOpeningHours {

    private IOpeningHours.OpeningDay day;
    private Date from;
    private Date to;
    private boolean cashCollectionDay;

    public OpeningHoursExample() {
    }

    @Override
    public OpeningDay getDay() {
        return day;
    }

    @Override
    public Date getFrom() {
        return from;
    }

    @Override
    public Date getTo() {
        return to;
    }

    @Override
    public boolean isCashCollectionDay() {
        return cashCollectionDay;
    }

}
