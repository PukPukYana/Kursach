package com.github.ynovice.sbermanager.backend.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendPhoneConfirmationCodeRequestDto {
    private String encryptedPhone;
}
