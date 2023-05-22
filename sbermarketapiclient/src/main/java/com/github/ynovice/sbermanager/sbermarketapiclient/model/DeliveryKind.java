package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeliveryKind {
    COURIER("courier"),
    PICKUP("pickup"),
    EXPRESS_DELIVERY("express_delivery");

    @JsonValue
    private final String kind;
}