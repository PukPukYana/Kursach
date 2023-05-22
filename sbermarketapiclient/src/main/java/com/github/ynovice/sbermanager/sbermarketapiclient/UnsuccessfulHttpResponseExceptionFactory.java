package com.github.ynovice.sbermanager.sbermarketapiclient;

import com.github.ynovice.sbermanager.sbermarketapiclient.exception.UnsuccessfulHttpResponseException;

import java.io.IOException;
import java.net.http.HttpResponse;

public interface UnsuccessfulHttpResponseExceptionFactory <T> {

    UnsuccessfulHttpResponseException nexException(HttpResponse<T> httpResponse) throws IOException;
}
