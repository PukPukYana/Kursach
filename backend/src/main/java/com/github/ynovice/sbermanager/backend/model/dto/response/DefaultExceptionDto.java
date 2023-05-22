package com.github.ynovice.sbermanager.backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultExceptionDto extends ExceptionDto {
    private String message;

    public DefaultExceptionDto(ExceptionCode exceptionCode, String message) {
        super(exceptionCode);
        this.message = message;
    }
}
