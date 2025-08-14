package com.generalbytes.batm.server.extensions.extra.communication;

import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.extra.communication.smsbranacz.SMSBranaCZFactory;
import com.generalbytes.batm.server.extensions.extra.communication.smsbranacz.SMSBranaCZProvider;
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
        SMSBranaCZProvider provider = mock(SMSBranaCZProvider.class);

        ExternalCommunicationExtension extension = new ExternalCommunicationExtension();

        try (MockedStatic<SMSBranaCZFactory> factoryMock = mockStatic(SMSBranaCZFactory.class)) {
            factoryMock.when(SMSBranaCZFactory::createProvider).thenReturn(provider);

            Set<ICommunicationProvider> communicationProviders = extension.getCommunicationProviders();
            assertEquals(1, communicationProviders.size());
            ICommunicationProvider communicationProvider = communicationProviders.iterator().next();
            assertInstanceOf(SMSBranaCZProvider.class, communicationProvider);

            factoryMock.verify(SMSBranaCZFactory::createProvider);
        }
    }
}