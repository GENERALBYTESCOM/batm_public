package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

class SMSBranaCZCredentialsServiceTest {

    private final SMSBranaCZCredentialsService service = new SMSBranaCZCredentialsService();

    @ParameterizedTest
    @ValueSource(strings = {"", "justOneEntry", "two;entries"})
    void testGetCredentials_invalidCredentials(String credentials) {
        SMSBranaCZValidationException exception = assertThrows(SMSBranaCZValidationException.class,
            () -> service.getCredentials(credentials));

        assertEquals("Invalid credentials format", exception.getMessage());
    }

    @Test
    void testGetCredentials() {
        UUID uuid = UUID.fromString("28b1943a-98c5-4ecb-a81b-4849c5b3dedd");
        LocalDateTime now = LocalDateTime.of(2025, 8, 8, 16, 24, 32);

        try (MockedStatic<UUID> uuidMock = mockStatic(UUID.class);
             MockedStatic<LocalDateTime> localDateTimeMock = mockStatic(LocalDateTime.class)) {
            uuidMock.when(UUID::randomUUID).thenReturn(uuid);
            localDateTimeMock.when(() -> LocalDateTime.now(ZoneId.of("Europe/Prague"))).thenReturn(now);

            SMSBranaCZApiCredentials credentials = service.getCredentials("myLogin:somePassword");

            assertEquals("myLogin", credentials.login());
            assertEquals("28b1943a-98c5-4ecb-a81b-4849c5b3dedd", credentials.salt());
            assertEquals("20250808T162432", credentials.time());
            assertEquals("f6b2927a760c5dad2db9a55ba08c97c4", credentials.auth());
        }
    }
}