package com.github.ynovice.sbermanager.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.ObjectError;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class InvalidFieldsException extends RuntimeException {

    private final List<ObjectError> objectErrors;
}
