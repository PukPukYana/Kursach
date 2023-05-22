package com.github.ynovice.sbermanager.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class InvalidFieldDto {

    private String fieldName;
    private String errorCode;
    private String errorMessage;
}
