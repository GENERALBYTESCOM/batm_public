package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import lombok.experimental.UtilityClass;
import si.mazi.rescu.RestProxyFactory;

@UtilityClass
public class SmsBranaCzFactory {

    private static final String API_BASE_URL = "https://api.smsbrana.cz/smsconnect";

    public static SmsBranaCzProvider createProvider() {
        ISmsBranaCzAPI api = RestProxyFactory.createProxy(ISmsBranaCzAPI.class, API_BASE_URL);

        return new SmsBranaCzProvider(new SmsBranaCzApiService(api), new SmsBranaCzCredentialsService());
    }
}
