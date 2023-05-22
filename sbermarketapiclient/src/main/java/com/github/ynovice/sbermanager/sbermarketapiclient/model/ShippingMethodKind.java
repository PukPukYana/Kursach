package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShippingMethodKind {

    BY_COURIER("by_courier"),
    BY_COURIER_FOR_COMPANIES("by_courier_for_companies"),
    PICKUP("pickup");

    @JsonValue
    private final String kind;
}