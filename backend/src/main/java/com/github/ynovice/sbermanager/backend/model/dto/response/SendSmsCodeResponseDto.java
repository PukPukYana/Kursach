package com.github.ynovice.sbermanager.backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SendSmsCodeResponseDto {

    private Integer codeLength;
}
