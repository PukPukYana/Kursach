package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Shipment (
        Long id,
        String number,
        String orderNumber,
        Long deliveryWindowId,
        Integer storeId,
        Integer retailerId,


        Double total,
        Integer totalWeight,

        ShipmentState shipmentState,
        String stateName,
        ShippingMethodKind shippingMethodKind,
        ZonedDateTime shippedAt,

        Boolean repeatAllowed,
        Boolean isRte,

        Integer errorCode,

        DeliveryWindow deliveryWindow,
        DeliveryWindow previousDeliveryWindow,

        List<LineItem> lineItems,
        Store store

        /*
            There were also the following fields in API response's JSON (can be added in future):
                sbbol_credit_request=null,
                delivery_forecast=null,
                merge=null
         */
) {

    @AllArgsConstructor
    @Getter
    public enum ShipmentState {
        SHIPPED("shipped");

        @JsonValue
        final String type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeliveryWindow (
            Long id,
            ZonedDateTime startsAt,
            ZonedDateTime endsAt,
            String humanInterval,
            LocalDate date,
            TimeZone timeZone,
            Integer weightBalance,
            Integer itemsCountBalance,
            DeliveryKind deliveryKind
    ) {}
}
