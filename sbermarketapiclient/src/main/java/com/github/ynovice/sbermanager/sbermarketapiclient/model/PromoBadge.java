package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PromoBadge (
        Integer id,
        String type,
        Attributes attributes
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Attributes (
            String name,
            PromoBadgeAttributeOptions options
    ) {}
}
