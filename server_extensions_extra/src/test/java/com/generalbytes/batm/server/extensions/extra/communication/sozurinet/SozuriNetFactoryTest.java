package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import si.mazi.rescu.RestProxyFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class SozuriNetFactoryTest {
    @Test
    void testCreateProvider_withCorrectParameters() {
        try (MockedStatic<RestProxyFactory> factoryMock = mockStatic(RestProxyFactory.class)) {
            SozuriNetProvider provider = SozuriNetFactory.createProvider();
            assertNotNull(provider);

            factoryMock.verify(() -> RestProxyFactory.createProxy(ISozuriNetAPI.class, "https://sozuri.net/api"));
        }
    }
}

