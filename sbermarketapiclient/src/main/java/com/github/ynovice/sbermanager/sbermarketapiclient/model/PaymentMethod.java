package com.github.ynovice.sbermanager.sbermarketapiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentMethod (
        Integer id,
        String name,
        Environment environment,
        Key key
) {

    @Getter
    @AllArgsConstructor
    public enum Environment {
        PRODUCTION("production");

        @JsonValue
        private final String value;
    }

    @Getter
    @AllArgsConstructor
    public enum Key {
        TRANSFER("transfer"),
        CARD_SBER("card_sber"),
        SBER_PAY("sber_pay"),
        SBER_PAY_V2("sber_pay_v2"),
        SBER_BNPL("sber_bnpl"),
        SBBOL_INSTALLMENT_PLAN("sbbol_installment_plan"),
        CARD_TO_COURIER("card_to_courier"),
        CARD("card"),
        CASH("cash"),
        EXTERNAL_PARTNER_PAY("external_partner_pay"),
        CASH_DESK("cash_desk"),
        SBBOL_CASH_HOLD("sbbol_cash_hold"),
        APPLE_PAY("apple_pay"),
        GOOGLE_PAY("google_pay");

        @JsonValue
        private final String value;
    }
}
