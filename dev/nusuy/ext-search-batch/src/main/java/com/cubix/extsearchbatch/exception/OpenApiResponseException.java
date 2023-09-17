package com.cubix.extsearchbatch.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;

@Getter
public class OpenApiResponseException extends RestClientException {
    private static final String message = "Open API Response Error";
    private final HttpStatusCode statusCode = HttpStatus.BAD_GATEWAY;

    public OpenApiResponseException(HttpStatusCode status, String providerMessage) {
        super(message + ": <" + status.value() + ">" + providerMessage);
    }
}
