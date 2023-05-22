package com.github.ynovice.sbermanager.backend.controller;

import com.github.ynovice.sbermanager.backend.exception.*;
import com.github.ynovice.sbermanager.backend.model.dto.InvalidFieldDto;
import com.github.ynovice.sbermanager.backend.model.dto.response.DefaultExceptionDto;
import com.github.ynovice.sbermanager.backend.model.dto.response.ExceptionCode;
import com.github.ynovice.sbermanager.backend.model.dto.response.InvalidFieldsResponse;
import com.github.ynovice.sbermanager.sbermarketapiclient.exception.UnsuccessfulHttpResponseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(InvalidObjectException.class)
    public ResponseEntity<InvalidFieldsResponse> handleInvalidObjectException(InvalidObjectException e) {

        List<InvalidFieldDto> invalidFieldDtos = e.getFieldErrors()
                .stream()
                .map(fieldError -> new InvalidFieldDto(
                        fieldError.getField(),
                        fieldError.getCode(),
                        fieldError.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(new InvalidFieldsResponse(invalidFieldDtos));
    }

    @ExceptionHandler(InvalidFieldsException.class)
    public ResponseEntity<InvalidFieldsResponse> handleInvalidFieldsException(InvalidFieldsException e) {

        List<InvalidFieldDto> invalidFieldDtos = e.getObjectErrors()
                .stream()
                .map(objectError -> new InvalidFieldDto(
                        objectError.getObjectName(),
                        objectError.getCode(),
                        objectError.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(new InvalidFieldsResponse(invalidFieldDtos));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<InvalidFieldsResponse> handleMissingServletParameterException(
            MissingServletRequestParameterException e) {

        InvalidFieldDto invalidFieldDto = new InvalidFieldDto(
                e.getParameterName(),
                e.getDetailMessageCode(),
                e.getMessage());
        return ResponseEntity.badRequest().body(
                new InvalidFieldsResponse(List.of(invalidFieldDto))
        );
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<DefaultExceptionDto> handleInternalServerError(InternalServerError e) {

        DefaultExceptionDto responseDto = new DefaultExceptionDto(ExceptionCode.INTERNAL_SERVER_ERROR, e.getMessage());
        return ResponseEntity.internalServerError().body(responseDto);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DefaultExceptionDto> handleBadRequestException(BadRequestException e) {

        DefaultExceptionDto responseDto = new DefaultExceptionDto(ExceptionCode.BAD_REQUEST, e.getMessage());
        return ResponseEntity.badRequest().body(responseDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DefaultExceptionDto> handleEntityNotFoundException(EntityNotFoundException e) {

        DefaultExceptionDto responseDto = new DefaultExceptionDto(ExceptionCode.NOT_FOUND, e.getMessage());
        return ResponseEntity.badRequest().body(responseDto);
    }

    @ExceptionHandler(SmClientInteractionException.class)
    public ResponseEntity<DefaultExceptionDto> handleSmClientInteractionException(SmClientInteractionException e) {

        UnsuccessfulHttpResponseException cause = e.getUnsuccessfulHttpResponseException();

        if (cause == null) {
            DefaultExceptionDto responseDto = new DefaultExceptionDto(
                    ExceptionCode.INTERNAL_SERVER_ERROR,
                    e.getMessage());
            return ResponseEntity.internalServerError().body(responseDto);
        }

        String exceptionMessage = cause.getUnsuccessfulResponseBody().phoneConfirmation().errors().base().get(0);

        DefaultExceptionDto responseDto = new DefaultExceptionDto(ExceptionCode.BAD_REQUEST, exceptionMessage);
        return ResponseEntity.badRequest().body(responseDto);
    }
}
