package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LineItem (
        Long id,
        String sku,
        String permalink,
        String state,
        Long shipmentId,
        Integer storeId,
        Integer pcs,
        Integer foundPcs,
        String name,
        Image image,
        String quantity,
        String foundQuantity,
        Double amount,
        Double originalAmount,
        Double discountPercent,
        String priceType,
        String humanVolume,
        Integer itemsPerPack,
        String retailerSku,
        Integer retailerId
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Image (
            String miniUrl,
            String smallUrl,
            String productUrl,
            Boolean isPlaceholder
    ) {}
}