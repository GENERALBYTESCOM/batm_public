package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import si.mazi.rescu.RestProxyFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class SMSBranaCZFactoryTest {
    @Test
    void testCreateProvider_withCorrectParameters() {
        try (MockedStatic<RestProxyFactory> factoryMock = mockStatic(RestProxyFactory.class)) {
            SMSBranaCZProvider provider = SMSBranaCZFactory.createProvider();
            assertNotNull(provider);

            factoryMock.verify(() -> RestProxyFactory.createProxy(ISMSBranaCZAPI.class, "https://api.smsbrana.cz/smsconnect"));
        }
    }

}