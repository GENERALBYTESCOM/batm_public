package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import lombok.experimental.UtilityClass;
import si.mazi.rescu.RestProxyFactory;

@UtilityClass
public class SozuriNetFactory {

    private static final String API_BASE_URL = "https://sozuri.net/api";

    public static SozuriNetProvider createProvider() {
        ISozuriNetAPI api = RestProxyFactory.createProxy(ISozuriNetAPI.class, API_BASE_URL);

        return new SozuriNetProvider(
                new SozuriNetApiService(api),
                new SozuriNetCredentialsService()
        );
    }
}