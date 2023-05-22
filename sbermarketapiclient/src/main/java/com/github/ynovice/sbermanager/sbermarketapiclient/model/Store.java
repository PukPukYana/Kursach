package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.ynovice.sbermanager.sbermarketapiclient.deserializer.LocalTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Store (
        Integer id,  // The id of a particular store, unique for each store in the retail chain.
        String uuid,  // The uuid of the retailer
        String retailerStoreId,
        String name,

        TimeZone timeZone,
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        LocalTime openingTime,
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        LocalTime closingTime,

        String deliveryForecastText,
        Boolean availableForPickup,
        String pickupInstruction,
        String phone,

        Boolean assemblyDispatch,
        Boolean parallelAssembly,
        Boolean externalAssembly,
        Integer secondsForAssemblyItem,
        Integer additionalSecondsForAssembly,

        Integer cityId,
        Boolean onDemand,
        Boolean onDemandRaw,
        ZonedDateTime availableOn,
        Boolean hasConveyor,
        Boolean autoRouting,
        Boolean expressDelivery,
        String importKeyPostfix,
        Boolean active,
        Boolean boxScanning,
        Boolean training,

        OrdersApiIntegrationType ordersApiIntegrationType,
        Retailer retailer,
        Location location,
        Config config,
        OperationalZone operationalZone,
        City city,
        List<StoreZone> storeZones,
        StoreSchedule storeSchedule,
        PharmacyLegalInfo pharmacyLegalInfo,
        List<Tenant> tenants,
        List<License> licenses,
        List<StoreShippingMethod> storeShippingMethods,
        List<StorePaymentMethod> paymentMethodsStores
) {

    @AllArgsConstructor
    @Getter
    public enum OrdersApiIntegrationType {
        SHOPPER("shopper"),
        DELIVERY_BY_SBERMARKET("delivery_by_sbermarket"),
        DELIVERY_BY_RETAILER("delivery_by_retailer");

        @JsonValue
        private final String type;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location (

            Integer id,

            String fullAddress,
            String city,
            String street,
            String building,
            String block,
            String floor,
            String apartment,
            String entrance,
            String elevator,
            String region,
            String comments,
            String phone,
            String area,
            String settlement,

            Double lat,
            Double lon,

            Integer cityKladrId,
            Integer streetKladrId,

            Integer userId,
            String doorPhone,
            String kind,

            Boolean deliveryToDoor
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Config (
            String lifepayIdentifier,
            String importKeyPostfix,
            Integer secondsForAssemblyItem,
            Integer additionalSecondsForAssembly
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OperationalZone (
            Integer id,
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record City (
            Integer id,
            String name,
            String nameIn,
            String nameFrom,
            String nameTo,
            String slug
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PharmacyLegalInfo (
            String name,
            String license,
            String address,
            String inn,
            String ogrn,
            String workSchedule,
            String phone,
            String email,
            String linkText
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record License (
            String kind,
            String number,
            LocalDate issueDate,
            LocalDate endDate
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StorePaymentMethod (
            Integer id,
            Integer storeId,
            String tenantId,
            PaymentMethod paymentMethod
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StoreSchedule (
            Integer id,
            Integer storeId,
            StoreScheduleTemplate template
    ) {}
}
