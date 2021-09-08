package com.generalbytes.batm.server.extensions.extra.examples.activeTerminals;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.extra.examples.rest.SecuredRESTServiceExample;
import com.generalbytes.batm.server.extensions.extra.examples.rest.ServletFilterExample;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// P44DXXC7X4PDZZPRBBZS4OIICSTMIRI3R

/**
 * This class demonstrates how to write a simple REST service that will respond to HTTPS calls.
 * For exact URLs @see {@link RestServiceActiveTerminals}
 * For security reasons this extension is not enabled by default.
 * You need to enable it in batm-extensions.xml
 */
public class ActiveTerminalsExtension extends AbstractExtension {
    private static IExtensionContext ctx;

    @Override
    public String getName() {
        return "RestService to get list of active Terminals";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        this.ctx = ctx;
    }

    @Override
    public Set<IRestService> getRestServices() {
        HashSet<IRestService> services = new HashSet<>();
        //This is simplest implementation of rest service
        services.add(new IRestService() {
            @Override
            public String getPrefixPath() {
                return "active";
            }

            @Override
            public Class getImplementation() {
                return RestServiceActiveTerminals.class;
            }

            @Override
            public List<Class> getFilters() {
                return Arrays.asList(ServletFilterExample.class);
            }
        });

        services.add(new IRestService() {
            @Override
            public String getPrefixPath() {
                return "secured";
            }

            @Override
            public Class getImplementation() {
                return SecuredRESTServiceExample.class;
            }
        });
        return services;
    }


    public static IExtensionContext getExtensionContext() {
        return ctx;
    }
}

