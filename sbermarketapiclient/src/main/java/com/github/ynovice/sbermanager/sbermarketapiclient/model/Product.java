package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Product (
        Integer id,
        String sku,
        Boolean available,
        String retailerSku,
        String name,
        Double price,
        Double originalPrice,
        Double discount,
        String humanVolume,
        Double volume,
        String volumeType,
        Integer itemsPerPack,
        ZonedDateTime discountEndsAt,
        String priceType,
        Double gramsPerUnit,
        Double unitPrice,
        Double originalUnitPrice,
        List<Integer> promoBadgeIds,
        Double score,
        List<String> imageUrls,
        List<String> labels,
        String canonicalUrl,
        Long legacyOfferId,
        Long legacyProductId,
        String slug,
        Integer maxSelectQuantity,

        String description,
        List<Property> properties,
        List<Product> relatedProducts,

        List<Requirement> requirements,
        ScoreDetails scoreDetails
){

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ScoreDetails (
            Integer commentCount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Property (
            String name,
            String presentation,
            String value
    ) {}
}