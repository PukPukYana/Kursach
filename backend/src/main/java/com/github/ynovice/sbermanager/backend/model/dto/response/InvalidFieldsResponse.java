package com.github.ynovice.sbermanager.backend.model.dto.response;

import com.github.ynovice.sbermanager.backend.model.dto.InvalidFieldDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvalidFieldsResponse extends ExceptionDto {

    private List<InvalidFieldDto> invalidFields;

    public InvalidFieldsResponse(List<InvalidFieldDto> invalidFields) {
        super(ExceptionCode.INVALID_FIELDS);
        this.invalidFields = invalidFields;
    }
}
