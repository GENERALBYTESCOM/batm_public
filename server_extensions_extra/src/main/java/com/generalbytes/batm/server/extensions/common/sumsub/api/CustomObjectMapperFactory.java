package com.generalbytes.batm.server.extensions.common.sumsub.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import si.mazi.rescu.serialization.jackson.JacksonObjectMapperFactory;

/**
 * A factory for creating and configuring instances of the {@link ObjectMapper} class to be
 * used with Jackson for JSON processing. This implementation of the {@link JacksonObjectMapperFactory}
 * provides basic functionality, registering all available Jackson modules by default
 * without additional configuration.
 *
 * <p>This class is meant to be used where default Jackson serialization and deserialization behavior
 * is sufficient, while still enabling easy extension or customization if needed through overriding.
 */
public class CustomObjectMapperFactory implements JacksonObjectMapperFactory {
    @Override
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Override
    public void configureObjectMapper(ObjectMapper objectMapper) {
        // not configured on purpose
    }
}
