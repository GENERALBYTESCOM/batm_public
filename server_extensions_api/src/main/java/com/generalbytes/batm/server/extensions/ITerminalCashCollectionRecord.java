package com.generalbytes.batm.server.extensions;

import java.util.Collection;
import java.util.Date;

public interface ITerminalCashCollectionRecord {

    String getTerminalSerialNumber();
    Date getServerTime();
    Date getTerminalTime();
    Collection<IAmount> getAmounts();
    IPerson getCollectingPerson();
    String getContains();
    String getNote();
    String getCountersLong();
    String getCountersShort();
    String getCashboxName();
}
