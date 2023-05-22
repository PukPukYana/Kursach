package com.github.ynovice.sbermanager.sbermarketapiclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Converts json object representations into Java objects.
 */
public class ModelMapperJackson implements ModelMapper {

    private final ObjectMapper objectMapper;

    public ModelMapperJackson() {
        objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new RecordNamingStrategyPatchModule());

        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE);
    }

    @Override
    public <C> C map(String src, Class<C> clazz) throws JsonProcessingException {
        return map(src, clazz, null);
    }

    @Override
    public <C> C map(String src, Class<C> clazz, String jumpTo) throws JsonProcessingException {

        JsonNode parsedJson = objectMapper.readTree(src);
        if(jumpTo != null) parsedJson = parsedJson.get(jumpTo);

        return objectMapper.treeToValue(parsedJson, clazz);
    }
}
