package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Meta(
        Integer currentPage,
        Integer nextPage,
        Integer previousPage,
        Integer totalPages,
        Integer perPage,
        Integer totalCount
) {}