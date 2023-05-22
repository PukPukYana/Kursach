package com.github.ynovice.sbermanager.backend.exception;

import com.github.ynovice.sbermanager.sbermarketapiclient.exception.UnsuccessfulHttpResponseException;
import lombok.Getter;

@Getter
public class SmClientInteractionException extends RuntimeException {

    private UnsuccessfulHttpResponseException unsuccessfulHttpResponseException;

    public SmClientInteractionException(String message) {
        super(message);
    }

    public SmClientInteractionException(UnsuccessfulHttpResponseException unsuccessfulHttpResponseException) {
        this.unsuccessfulHttpResponseException = unsuccessfulHttpResponseException;
    }
}
