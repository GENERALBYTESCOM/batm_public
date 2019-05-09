package com.generalbytes.batm.server.extensions;

import java.util.Date;

public interface IEventRecord {

    String getTerminalSerialNumber();
    int getType();
    String getTypeAsText();
    String getData();
    String getReadableData();
    Date getTerminalTime();
    Date getServerTime();
}
