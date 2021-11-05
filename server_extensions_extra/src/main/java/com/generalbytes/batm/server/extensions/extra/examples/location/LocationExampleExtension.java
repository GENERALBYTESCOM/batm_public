package com.generalbytes.batm.server.extensions.extra.examples.location;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;

import java.util.HashSet;
import java.util.Set;

// uncomment in batm-extensions.xml
public class LocationExampleExtension extends AbstractExtension {

    private static IExtensionContext ctx;

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        this.ctx = ctx;
    }

    @Override
    public String getName() {
        return "Location Extension (example)";
    }

    @Override
    public Set<IRestService> getRestServices() {
        HashSet<IRestService> services = new HashSet<>();
        services.add(new LocationExampleIRestService());
        return services;
    }

    public static IExtensionContext getExtensionContext() {
        return ctx;
    }

}
