package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LineItemsResponseBody (
        @JsonProperty("assembly_items")
        List<LineItem> lineItems,
        Meta meta
) {}
