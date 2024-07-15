package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.server.extensions.event.ApplicationEvent;

public interface IApplicationListener {

    default void onStartup(ApplicationEvent event) {};
}
