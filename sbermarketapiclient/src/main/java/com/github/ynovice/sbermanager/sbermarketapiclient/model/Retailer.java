package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Retailer (
        Integer id,
        String name,
        String slug,
        String description,
        String color,
        String secondaryColor,
        String logoBackgroundColor,

        String logo,
        String icon,

        Boolean isAlcohol,
        Boolean isAgentContractTypes,
        Boolean isCertificates,

        Integer homePageDepartmentsDepth,

        Appearance appearance,

        Boolean available,
        String key,
        Integer position,
        ZonedDateTime createdAt,
        String legalName,
        String inn,
        String phone,
        String legalAddress,
        String contractType,
        String shortName,
        Integer retailerCategoryId,
        String seoCategory,
        String phoneTransfer,
        String replacementPhone,
        Boolean availableOrderApiSettings,

        RetailerAgreement retailerAgreement,
        RetailerServiceConfig retailerServiceConfig,
        PromoShield promoShield,
        RetailerReference masterRetailer,
        KeyAccountManager keyAccountManager,
        List<RetailerReference> slaveRetailers
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RetailerAgreement (
            String agreementType,
            String url
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RetailerServiceConfig (
            Integer id,
            String baseUrl,
            String basicAuth,
            Integer pollingInterval
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PromoShield (
            String gradientStartColor,
            String gradientEndColor,
            String textColor,
            String text
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RetailerReference (
            Integer id,
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KeyAccountManager (
            Integer id,
            String fullname
    ) {}
}
