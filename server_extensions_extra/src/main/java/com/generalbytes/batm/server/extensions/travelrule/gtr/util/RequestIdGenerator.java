package com.generalbytes.batm.server.extensions.travelrule.gtr.util;

import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import lombok.AllArgsConstructor;

import java.util.UUID;

/**
 * Utility service for generating unique Request ID used to call some API endpoints in {@link GtrApi}.
 */
@AllArgsConstructor
public class RequestIdGenerator {

    private final GtrConfiguration configuration;

    /**
     * Generates a new Request ID in the format: [prefix]-[UUIDv4]
     * The prefix is obtained from the {@link GtrConfiguration}.
     *
     * @return Newly generated Request ID.
     */
    public String generateRequestId() {
        return configuration.getRequestIdPrefix() + "-" + generateUuid();
    }

    private UUID generateUuid() {
        return UUID.randomUUID();
    }

}
