package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.ynovice.sbermanager.sbermarketapiclient.deserializer.StoreZoneDeserializer;

import java.util.Set;

@JsonDeserialize(using = StoreZoneDeserializer.class)
public record StoreZone (
        Integer id,
        String name,
        Set<Area> areas
) {}