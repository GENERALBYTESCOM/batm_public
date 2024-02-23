package com.generalbytes.batm.server.extensions.examples.communication;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

// uncomment in batm-extensions.xml
public class ExternalCommunicationExampleExtension extends AbstractExtension {
    Logger log = LoggerFactory.getLogger(ExternalCommunicationExampleExtension.class);

    @Override
    public String getName() {
        return "External CommunicationExtension (example)";
    }

    @Override
    public Set<ICommunicationProvider> getCommunicationProviders() {
        Set<ICommunicationProvider> providers = new HashSet<>();
        providers.add(new NexmoSmsProvider());

        return providers;
    }
}
