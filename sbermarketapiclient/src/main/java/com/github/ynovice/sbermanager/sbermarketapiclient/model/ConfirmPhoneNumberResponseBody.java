package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConfirmPhoneNumberResponseBody (
        Boolean isRegistration,
        User user,
        String csrfToken
) {}
