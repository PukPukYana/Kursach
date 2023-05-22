package com.github.ynovice.sbermanager.sbermarketapiclient.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalTime;

public class LocalTimeDeserializer extends StdDeserializer<LocalTime> {

    public LocalTimeDeserializer() {
        super(LocalTime.class);
    }

    @Override
    public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode localTimeNode = jp.getCodec().readTree(jp);
        String localTimeString = localTimeNode.textValue().trim();

        if(localTimeString.startsWith("24:")) {
            localTimeString = localTimeString.replace("24:", "00:");
        }
        else if(localTimeString.startsWith("25:")) {
            localTimeString = localTimeString.replace("25:", "01:");
        }
        else if(localTimeString.startsWith("26:")) {
            localTimeString = localTimeString.replace("26:", "02:");
        }
        else if(localTimeString.startsWith("27:")) {
            localTimeString = localTimeString.replace("27:", "03:");
        }

        return LocalTime.parse(localTimeString);
    }
}
