package com.generalbytes.batm.server.extensions;

import java.util.Collection;

public interface IApiAccess {
    boolean isAuthenticated();
    Collection<String> getTerminalSerialNumbers();

}
