package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import lombok.experimental.UtilityClass;
import si.mazi.rescu.RestProxyFactory;

@UtilityClass
public class SMSBranaCZFactory {

    private static final String API_BASE_URL = "https://api.smsbrana.cz/smsconnect";

    public static SMSBranaCZProvider createProvider() {
        ISMSBranaCZAPI api = RestProxyFactory.createProxy(ISMSBranaCZAPI.class, API_BASE_URL);

        return new SMSBranaCZProvider(new SMSBranaCZApiService(api), new SMSBranaCZCredentialsService());
    }
}
