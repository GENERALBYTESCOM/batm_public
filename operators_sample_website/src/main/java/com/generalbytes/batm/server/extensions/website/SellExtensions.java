package com.generalbytes.batm.server.extensions.website;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;

import java.util.HashSet;
import java.util.Set;

/**
 * This class demonstrates how to write a simple REST service that will respond to HTTPS calls.
 * For exact URLs @see {@link RestServiceWebsite}
 * For security reasons this extension is not enabled by default.
 * You need to enable it in batm-extensions.xml
 */

public class SellExtensions extends AbstractExtension {

    public static IExtensionContext ctx;

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        this.ctx = ctx;
    }

    @Override
    public String getName() {
        return "RestServices for operators sample website";
    }

    @Override
    public Set<IRestService> getRestServices() {
        HashSet<IRestService> services = new HashSet<>();
        services.add(new IRestService() {
            @Override
            public String getPrefixPath() {
                return "website";
            }

            @Override
            public Class getImplementation() {
                return RestServiceWebsite.class;
            }

        });
        return services;
    }

    public static IExtensionContext getExtensionContext() {
        return ctx;
    }
}
