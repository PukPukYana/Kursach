package com.github.ynovice.sbermanager.backend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmPhoneNumberRequestDto {

    private String rawPhone;
    private String encryptedPhone;
    private String confirmationCode;
}
