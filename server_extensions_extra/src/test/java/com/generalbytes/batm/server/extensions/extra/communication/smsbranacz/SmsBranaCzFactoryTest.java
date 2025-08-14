package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import si.mazi.rescu.RestProxyFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class SmsBranaCzFactoryTest {
    @Test
    void testCreateProvider_withCorrectParameters() {
        try (MockedStatic<RestProxyFactory> factoryMock = mockStatic(RestProxyFactory.class)) {
            SmsBranaCzProvider provider = SmsBranaCzFactory.createProvider();
            assertNotNull(provider);

            factoryMock.verify(() -> RestProxyFactory.createProxy(ISmsBranaCzAPI.class, "https://api.smsbrana.cz/smsconnect"));
        }
    }

}