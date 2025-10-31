package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SozuriNetCredentialsServiceTest {

    private final SozuriNetCredentialsService service = new SozuriNetCredentialsService();

    @ParameterizedTest
    @ValueSource(strings = {"", "justOneEntry", "two:entries", "three:entries:only", "four:entries:only:here"})
    void testGetCredentials_invalidCredentials(String credentials) {
        SozuriNetValidationException exception = assertThrows(SozuriNetValidationException.class,
            () -> service.getCredentials(credentials));

        assertEquals("Invalid credentials format", exception.getMessage());
    }

    @Test
    void testGetCredentials() {
        SozuriNetApiCredentials credentials = service.getCredentials("myApiKey:myProject:mySender:myCampaign:myChannel");

        assertEquals("myProject", credentials.project());
        assertEquals("mySender", credentials.from());
        assertEquals("myCampaign", credentials.campaign());
        assertEquals("myChannel", credentials.channel());
        assertEquals("myApiKey", credentials.apiKey());
    }
}

