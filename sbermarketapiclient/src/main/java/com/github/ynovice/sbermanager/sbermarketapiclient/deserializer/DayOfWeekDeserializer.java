package com.github.ynovice.sbermanager.sbermarketapiclient.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

public class DayOfWeekDeserializer extends StdDeserializer<Set<DayOfWeek>> {

    public DayOfWeekDeserializer() {
        super(Set.class);
    }

    @Override
    public Set<DayOfWeek> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode slotActiveDays = jp.getCodec().readTree(jp);

        Set<DayOfWeek> daysOfWeek = new HashSet<>();

        for(JsonNode nameOfDayOfWeekNode : slotActiveDays) {
            daysOfWeek.add(DayOfWeek.valueOf(nameOfDayOfWeekNode.textValue().toUpperCase()));
        }

        return daysOfWeek;
    }
}
