package com.github.ynovice.sbermanager.backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SendSmsCodeRequestDto {

    private String phone;
}
