package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApi;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneAuthApi;
import lombok.AllArgsConstructor;
import si.mazi.rescu.RestProxyFactory;

/**
 * Factory for creating Notabene API proxies.
 */
@AllArgsConstructor
public class NotabeneApiFactory {

    private final NotabeneConfiguration configuration;

    public NotabeneApi getNotabeneApi() {
        return RestProxyFactory.createProxy(NotabeneApi.class, configuration.getApiUrl());
    }

    public NotabeneAuthApi getNotabeneAuthApi() {
        return RestProxyFactory.createProxy(NotabeneAuthApi.class, configuration.getAuthApiUrl());
    }

}
