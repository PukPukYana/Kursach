package com.github.ynovice.sbermanager.sbermarketapiclient.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.Area;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.GeographicCoordinate;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.StoreZone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StoreZoneDeserializer extends StdDeserializer<StoreZone> {

    public StoreZoneDeserializer() {
        super(StoreZone.class);
    }

    @Override
    public StoreZone deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        JsonNode storeZoneNode = jp.getCodec().readTree(jp);

        Integer id = storeZoneNode.get("id").isNull() ? null : storeZoneNode.get("id").intValue();
        String name = storeZoneNode.get("name").isNull() ? null : storeZoneNode.get("name").textValue();

        JsonNode areasNode = storeZoneNode.get("area");

        Set<Area> areas = new HashSet<>();
        for(JsonNode areaNode : areasNode) {

            List<GeographicCoordinate> dots = new ArrayList<>();
            for(JsonNode geographicCoordinateNode : areaNode) {

                Double lon = geographicCoordinateNode.get(0).doubleValue();
                Double lat = geographicCoordinateNode.get(1).doubleValue();

                dots.add(new GeographicCoordinate(lon, lat));
            }

            areas.add(new Area(dots));
        }

        return new StoreZone(id, name, areas);
    }
}
