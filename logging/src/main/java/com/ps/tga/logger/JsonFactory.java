package com.ps.tga.logger;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonFactory {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setVisibility(FIELD, ANY)
            .registerModule(new JavaTimeModule())
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(NON_NULL);

    private JsonFactory() {
    }

    public static String asString(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ObjectSerializationException("Cannot serialize log message", e);
        }
    }

    public static Object asObject(String json, Class<?> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new ObjectSerializationException("Cannot deserialize json", e);
        }
    }

}