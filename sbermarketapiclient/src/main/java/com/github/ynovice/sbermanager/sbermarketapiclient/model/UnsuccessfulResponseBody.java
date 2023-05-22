package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A universal DTO, which provides all the necessary fields to contain the body data of any
 * failed http response (whose status is not 200)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UnsuccessfulResponseBody (
        Integer code,
        String error,
        List<ErrorData> errors,
        PhoneConfirmationError phoneConfirmation
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ErrorData (
            String type,
            String field,
            String message
    ) {}
}