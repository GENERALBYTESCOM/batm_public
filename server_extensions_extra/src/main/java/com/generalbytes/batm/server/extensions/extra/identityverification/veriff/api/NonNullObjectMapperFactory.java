package com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import si.mazi.rescu.serialization.jackson.DefaultJacksonObjectMapperFactory;

public class NonNullObjectMapperFactory extends DefaultJacksonObjectMapperFactory {
    public void configureObjectMapper(ObjectMapper objectMapper) {
        super.configureObjectMapper(objectMapper);
        // do not include fields with null values when serializing
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
