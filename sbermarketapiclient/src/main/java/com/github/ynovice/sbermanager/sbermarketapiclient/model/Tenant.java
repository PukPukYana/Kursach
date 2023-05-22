package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Tenant (
        String id,
        String name,
        String hostname,
        PreferredCardPaymentMethod preferredCardPaymentMethod
) {

    @AllArgsConstructor
    @Getter
    public enum PreferredCardPaymentMethod {
        CLOUD_PAYMENTS("cloud_payments"),
        SBER("sber");

        @JsonValue
        private final String method;
    }
}