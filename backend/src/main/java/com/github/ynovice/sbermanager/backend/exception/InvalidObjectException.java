package com.github.ynovice.sbermanager.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class InvalidObjectException extends RuntimeException {

    private final List<FieldError> fieldErrors;
}
