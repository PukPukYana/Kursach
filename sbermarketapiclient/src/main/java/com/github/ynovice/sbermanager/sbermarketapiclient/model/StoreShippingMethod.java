package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoreShippingMethod (
        Integer id,
        Integer storeId,
        String tenantId,
        ZonedDateTime availableOn,
        ShippingMethod shippingMethod
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ShippingMethod (
            Integer id,
            String name,
            ShippingMethodKind kind
    ) {}
}
