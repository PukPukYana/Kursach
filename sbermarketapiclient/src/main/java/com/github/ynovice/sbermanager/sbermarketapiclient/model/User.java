package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User (
        Long id,
        String uuid,
        String externalUuid,
        String email,
        String displayEmail,
        String emailMd5,

        String firstName,
        String lastName,
        String fullName,

        String phone,

        Boolean isAdmin,

        Boolean promoTermsAccepted,
        ZonedDateTime promoTermsChangedAt,
        Boolean privacyTerms,
        Boolean b2b,

        Boolean hasConfirmedPhone,

        Integer shippedAndPaidOrdersCount,

        Benefit benefit

        // there were also auth_services_for_attach=[] and roles=[] fields in API response's JSON
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Benefit (
            Boolean available,
            Double balance,
            String company
    ) {}
}
