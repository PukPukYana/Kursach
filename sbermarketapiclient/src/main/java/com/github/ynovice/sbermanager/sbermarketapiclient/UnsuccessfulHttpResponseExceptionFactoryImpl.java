package com.github.ynovice.sbermanager.sbermarketapiclient;

import com.github.ynovice.sbermanager.sbermarketapiclient.exception.UnsuccessfulHttpResponseException;
import com.github.ynovice.sbermanager.sbermarketapiclient.model.UnsuccessfulResponseBody;

import java.io.IOException;
import java.net.http.HttpResponse;

public class UnsuccessfulHttpResponseExceptionFactoryImpl implements UnsuccessfulHttpResponseExceptionFactory<String> {

    private final ModelMapper modelMapper;

    public UnsuccessfulHttpResponseExceptionFactoryImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UnsuccessfulHttpResponseException nexException(HttpResponse<String> httpResponse) throws IOException {

        String responseBodyJson = httpResponse.body();

        int statusCode = httpResponse.statusCode();
        String message = "The response from the server has the status code " + statusCode;

        UnsuccessfulResponseBody responseBody = modelMapper.map(responseBodyJson, UnsuccessfulResponseBody.class);

        return new UnsuccessfulHttpResponseException(message, statusCode, responseBody);
    }
}
