package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Category (
        Integer id,
        Integer parentId,
        String type,
        String name,
        String slug,
        Integer productsCount,
        String iconUrl,
        String canonicalUrl,
        Integer position,
        Integer depth,
        Appearance appearance,
        Boolean isPromo,
        String altIcon,
        List<String> backgroundColors,
        List<Category> children,
        List<Requirement> requirements
) {}