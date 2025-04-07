package com.cubix.extsearchbatch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;

@Getter
public class OpenApiRequestException extends RestClientException {
    private static final String message = "Open API Request Error";
    private final HttpStatusCode statusCode = HttpStatus.BAD_REQUEST;

    public OpenApiRequestException(HttpStatusCode status, String providerMessage) {
        super(message + ": <" + status.value() + ">" + providerMessage);
    }
}
