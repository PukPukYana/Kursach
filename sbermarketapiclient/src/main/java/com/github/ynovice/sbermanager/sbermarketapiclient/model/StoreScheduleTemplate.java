package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.ynovice.sbermanager.sbermarketapiclient.deserializer.DayOfWeekDeserializer;
import com.github.ynovice.sbermanager.sbermarketapiclient.deserializer.LocalTimeDeserializer;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoreScheduleTemplate (
        List<DeliveryTime> deliveryTimes
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeliveryTime (
            @JsonDeserialize(using = LocalTimeDeserializer.class)
            LocalTime start,
            @JsonDeserialize(using = LocalTimeDeserializer.class)
            LocalTime end,

            Integer ordersLimit,
            Integer surgeAmount,
            Double shipmentMinKilos,
            Double shipmentMaxKilos,
            Double shipmentsExcessKilos,
            Integer shipmentsExcessItemsCount,

            Integer closingTimeGap,
            DeliveryKind kind,

            List<Integer> storeZoneIds,
            @JsonDeserialize(using = DayOfWeekDeserializer.class)
            Set<DayOfWeek> slotActiveDays
    ) {}
}
