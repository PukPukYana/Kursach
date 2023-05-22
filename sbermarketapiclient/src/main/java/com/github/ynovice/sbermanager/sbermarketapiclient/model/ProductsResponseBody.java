package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductsResponseBody (
        List<Product> products,
        Meta meta,
        Meta paginationMeta,
        List<Facet> facets,
        PrivateFilters privateFilters,
        List<Sort> sort,
        RootCategories rootCategories,
        List<PromoBadge> promoBadges
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Facet (
            String key,
            String name,
            String type,
            List<Option> options
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PrivateFilters (
            Boolean inStock,
            Boolean withSimilar
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sort (
            String key,
            String name,
            String order,
            Boolean active
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RootCategories (
            String key,
            String name,
            String type,
            List<Option> options
    ) {}
}