package com.generalbytes.batm.server.extensions.extra.communication;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.extra.communication.smsbranacz.SmsBranaCzFactory;

import java.util.Set;

/**
 * An extension responsible for exposing external communication providers.
 */
public class ExternalCommunicationExtension extends AbstractExtension {
    @Override
    public String getName() {
        return "External Communication Extension";
    }

    @Override
    public Set<ICommunicationProvider> getCommunicationProviders() {
        return Set.of(SmsBranaCzFactory.createProvider());
    }
}
