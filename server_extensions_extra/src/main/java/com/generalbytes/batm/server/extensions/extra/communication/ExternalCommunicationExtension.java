package com.generalbytes.batm.server.extensions.extra.communication;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;

import java.util.HashSet;
import java.util.Set;

public class ExternalCommunicationExtension extends AbstractExtension {
    @Override
    public String getName() {
        return "External CommunicationExtension";
    }

    @Override
    public Set<ICommunicationProvider> getCommunicationProviders() {
        Set<ICommunicationProvider> providers = new HashSet<>();
        providers.add(new NexmoSmsProvider());
        providers.add(new SMSBranaCZProvider());
        return providers;
    }
}
