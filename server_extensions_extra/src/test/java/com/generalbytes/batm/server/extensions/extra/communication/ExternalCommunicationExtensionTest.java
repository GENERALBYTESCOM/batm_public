package com.generalbytes.batm.server.extensions.extra.communication;

import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.extra.communication.smsbranacz.SmsBranaCzFactory;
import com.generalbytes.batm.server.extensions.extra.communication.smsbranacz.SmsBranaCzProvider;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class ExternalCommunicationExtensionTest {

    @Test
    void testGetName() {
        ExternalCommunicationExtension extension = new ExternalCommunicationExtension();
        assertEquals("External Communication Extension", extension.getName());
    }

    @Test
    void testGetCommunicationProviders() {
        SmsBranaCzProvider provider = mock(SmsBranaCzProvider.class);

        ExternalCommunicationExtension extension = new ExternalCommunicationExtension();

        try (MockedStatic<SmsBranaCzFactory> factoryMock = mockStatic(SmsBranaCzFactory.class)) {
            factoryMock.when(SmsBranaCzFactory::createProvider).thenReturn(provider);

            Set<ICommunicationProvider> communicationProviders = extension.getCommunicationProviders();
            assertEquals(1, communicationProviders.size());
            ICommunicationProvider communicationProvider = communicationProviders.iterator().next();
            assertInstanceOf(SmsBranaCzProvider.class, communicationProvider);

            factoryMock.verify(SmsBranaCzFactory::createProvider);
        }
    }
}