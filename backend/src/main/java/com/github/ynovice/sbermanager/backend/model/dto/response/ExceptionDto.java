package com.github.ynovice.sbermanager.backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionDto {

    public ExceptionCode exceptionCode;

    public ExceptionDto(ExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
