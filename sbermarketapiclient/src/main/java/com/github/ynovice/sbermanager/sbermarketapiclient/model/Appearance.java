package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Appearance(
        String backgroundColor,
        String imageColor,
        String cornerColor,
        Boolean blackTheme,
        String logoImage,
        String sideImage,
        String miniLogoImage
) {}