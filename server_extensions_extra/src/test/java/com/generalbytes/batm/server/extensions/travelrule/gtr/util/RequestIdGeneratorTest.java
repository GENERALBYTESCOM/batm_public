package com.generalbytes.batm.server.extensions.travelrule.gtr.util;

import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestIdGeneratorTest {

    @Mock
    private GtrConfiguration configuration;
    @InjectMocks
    private RequestIdGenerator generatorService;

    @Test
    void testGenerateRequestId() {
        when(configuration.getRequestIdPrefix()).thenReturn("generalBytes");

        String requestId = generatorService.generateRequestId();

        assertTrue(requestId.startsWith("generalBytes-"));

        try {
            UUID uuid = UUID.fromString(requestId.substring(13));
            assertNotNull(uuid);
        } catch (Exception e) {
            fail();
        }
    }

}