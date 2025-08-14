package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsBranaCzApiServiceTest {
    @Mock
    private ISmsBranaCzAPI api;
    @InjectMocks
    private SmsBranaCzApiService service;

    @Test
    void testSendSms() throws IOException {
        when(api.sendSms("login", "time", "salt", "auth", "send_sms", "+420123456789", "some message", "ucs2"))
            .thenReturn("someResponse");

        String someMessage = service.sendSms(createCredentials(), "+420123456789", "some message");
        assertEquals("someResponse", someMessage);
    }

    private SmsBranaCzApiCredentials createCredentials() {
        return new SmsBranaCzApiCredentials("login", "salt", "time", "auth");
    }
}