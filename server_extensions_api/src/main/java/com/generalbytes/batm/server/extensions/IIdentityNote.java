package com.generalbytes.batm.server.extensions;

import java.util.Date;

public interface IIdentityNote {

    String getText();
    Date getServerTime();
    String getUserName();
    boolean isDeleted();

}
