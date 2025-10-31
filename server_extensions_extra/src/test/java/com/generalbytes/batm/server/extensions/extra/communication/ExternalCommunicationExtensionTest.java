package com.generalbytes.batm.server.extensions.extra.communication;

import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.extra.communication.smsbranacz.SmsBranaCzFactory;
import com.generalbytes.batm.server.extensions.extra.communication.smsbranacz.SmsBranaCzProvider;
import com.generalbytes.batm.server.extensions.extra.communication.sozurinet.SozuriNetFactory;
import com.generalbytes.batm.server.extensions.extra.communication.sozurinet.SozuriNetProvider;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        SmsBranaCzProvider smsBranaCzProvider = mock(SmsBranaCzProvider.class);
        SozuriNetProvider sozuriNetProvider = mock(SozuriNetProvider.class);

        ExternalCommunicationExtension extension = new ExternalCommunicationExtension();

        try (MockedStatic<SmsBranaCzFactory> smsBranaCzFactoryMock = mockStatic(SmsBranaCzFactory.class);
             MockedStatic<SozuriNetFactory> sozuriNetFactoryMock = mockStatic(SozuriNetFactory.class)) {
            
            smsBranaCzFactoryMock.when(SmsBranaCzFactory::createProvider).thenReturn(smsBranaCzProvider);
            sozuriNetFactoryMock.when(SozuriNetFactory::createProvider).thenReturn(sozuriNetProvider);

            Set<ICommunicationProvider> communicationProviders = extension.getCommunicationProviders();
            assertEquals(2, communicationProviders.size());
            
            assertTrue(communicationProviders.stream().anyMatch(p -> p instanceof SmsBranaCzProvider));
            assertTrue(communicationProviders.stream().anyMatch(p -> p instanceof SozuriNetProvider));

            smsBranaCzFactoryMock.verify(SmsBranaCzFactory::createProvider);
            sozuriNetFactoryMock.verify(SozuriNetFactory::createProvider);
        }
    }
}